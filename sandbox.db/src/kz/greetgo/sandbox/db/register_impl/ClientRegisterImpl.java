package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.controller.render.model.ClientRow;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;
import org.fest.util.Lists;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<JdbcSandbox> jdbc;

  @Override
  public ClientDetails detail(Integer clientId) {
    return getDetails(clientId);
  }

  @Override
  public Integer save(ClientToSave clientToSave) {
    return insertOrUpdateClient(clientToSave);
  }

  private Integer insertOrUpdateClient(ClientToSave clientToSave) {
    return jdbc.get().execute(connection -> {
      String sql = clientToSave.id==null?getClientInsertQuery():getClientUpdateQuery();
      PreparedStatement ps = connection.prepareStatement(sql);
      setObjectsToPS(ps, clientToSave);
      try {
        Integer id = null;
        ResultSet rs = ps.executeQuery();
        try {
          if (rs.next()) {
            id = rs.getInt("id");
            insertPhonesAndAddresses(id, clientToSave);
          }
          return id;
        } finally {
          rs.close();
        }
      } finally {
        ps.close();
      }
    });
  }

  private void setObjectsToPS(PreparedStatement ps, ClientToSave clientToSave) throws SQLException {
    setObjects(1, ps,
      clientToSave.surname,
      clientToSave.name,
      clientToSave.patronymic,
      clientToSave.gender.name(),
      new java.sql.Date(clientToSave.birthDate.getTime()),
      clientToSave.charmId);
    if (clientToSave.id!=null) ps.setObject(7, clientToSave.id);
  }

  private String getClientInsertQuery() {
    return "INSERT INTO client(surname, name, patronymic, gender, birth_date, charm_id) VALUES (?, ?, ?, ?, ?, ?) RETURNING id;";
  }

  private String getClientUpdateQuery() {
    return "UPDATE client SET surname=?, name=?, patronymic=?, gender=?, birth_date=?, charm_id=? WHERE id=? RETURNING id;";
  }

  private void insertPhonesAndAddresses(Integer client, ClientToSave clientToSave) {
    clientToSave.addressFact.client = client;
    insertOrUpdateClientAddress(clientToSave.addressFact);
    clientToSave.addressReg.client = client;
    insertOrUpdateClientAddress(clientToSave.addressReg);
    clientToSave.homePhone.client = client;
    insertOrUpdateClientPhone(clientToSave.homePhone);
    clientToSave.workPhone.client = client;
    insertOrUpdateClientPhone(clientToSave.workPhone);
    clientToSave.mobilePhone.client = client;
    insertOrUpdateClientPhone(clientToSave.mobilePhone);
  }

  private void insertOrUpdateClientPhone(ClientPhone clientPhone) {
    clientDao.get().insertPhone(clientPhone.client, clientPhone.type.name(), clientPhone.number);
  }

  private void insertOrUpdateClientAddress(ClientAddress clientAddress) {
    clientDao.get().insertAddress(clientAddress.client, clientAddress.type.name(), clientAddress.street, clientAddress.house, clientAddress.flat);
  }

  private ClientDetails getDetails(Integer clientId) {
    ClientDetails details = clientDao.get().getDetails(clientId);
    details.addressFact = getClientAddress(clientId, AddressTypeEnum.FACT);
    details.addressReg = getClientAddress(clientId, AddressTypeEnum.REG);
    details.homePhone = getClientPhone(clientId, PhoneType.HOME);
    details.workPhone = getClientPhone(clientId, PhoneType.WORK);
    details.mobilePhone = getClientPhone(clientId, PhoneType.MOBILE);
    return details;
  }

  private ClientPhone getClientPhone(Integer clientId, PhoneType type) {
    return jdbc.get().execute(connection -> {
      ClientPhone clientPhone = null;
      try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM client_phone WHERE client=? AND type=? AND actual=1")) {
        ps.setObject(1, clientId);
        ps.setObject(2, type.name());
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) clientPhone = getClientPhoneFromResultSet(rs);
        }
      }
      return clientPhone;
    });
  }

  private ClientPhone getClientPhoneFromResultSet(ResultSet rs) throws SQLException {
    ClientPhone clientPhone = new ClientPhone();
    clientPhone.client = rs.getInt("client");
    clientPhone.number = rs.getString("number");
    clientPhone.type = PhoneType.valueOf(rs.getString("type"));
    return clientPhone;
  }

  private ClientAddress getClientAddress(Integer clientId, AddressTypeEnum type) {
    return jdbc.get().execute(connection -> {
      ClientAddress clientAddress = null;
      PreparedStatement ps = connection.prepareStatement("SELECT * FROM client_address WHERE client=? AND type=? AND actual=1");
      ps.setObject(1, clientId);
      ps.setObject(2, type.name());
      try {
        ResultSet rs = ps.executeQuery();
        try {
          if (rs.next()) clientAddress = getClientAddressFromResultSet(rs);
        } finally {
          rs.close();
        }
      } finally {
        ps.close();
      }
      return clientAddress;
    });
  }

  private ClientAddress getClientAddressFromResultSet(ResultSet rs) throws SQLException {
    ClientAddress clientAddress = new ClientAddress();
    clientAddress.client = rs.getInt("client");
    clientAddress.type = AddressTypeEnum.valueOf(rs.getString("type"));
    clientAddress.street = rs.getString("street");
    clientAddress.house = rs.getString("house");
    clientAddress.flat = rs.getString("flat");
    return clientAddress;
  }

  @Override
  public void delete(Integer clientId) {
    clientDao.get().setNotActual(clientId);
  }

  @Override
  public List<ClientRecord> getRecords(ClientFilter filter) {
    if (filter == null) return new ArrayList<>();

    List<Object> params = new ArrayList<>();
    StringBuilder sqlQuery = new StringBuilder();

    selectRecords(sqlQuery);
    from(sqlQuery);
    leftJoin(sqlQuery);
    whereActual(sqlQuery);
    andFilter(sqlQuery, filter, params);
    groupBy(sqlQuery);
    orderBy(sqlQuery, filter);
    offsetAndLimit(sqlQuery, filter, params);

    return jdbc.get().execute(connection -> {
      List<ClientRecord> result = Lists.newArrayList();
      PreparedStatement ps = connection.prepareStatement(sqlQuery.toString());
      try {
        appendParams(ps, params);
        ResultSet rs = ps.executeQuery();
        try {
          while (rs.next()) result.add(getClientRecordFromResultSet(rs));
        } finally {
          rs.close();
        }
      } finally {
        ps.close();
      }
      return result;
    });
  }

  private void appendParams(PreparedStatement ps, List<Object> params) throws SQLException {
    for (int i = 1; i <= params.size(); i++) ps.setObject(i, params.get(i-1));
  }

  private void selectRecords(StringBuilder sqlQuery) {
    sqlQuery.append("SELECT client.id, client.surname, client.name, client.patronymic, date_part('year',age(client.birth_date)) AS age, " +
      "AVG(CASE WHEN client_account.money IS NULL THEN 0 ELSE client_account.money END) AS middle_balance, " +
      "MAX(CASE WHEN client_account.money IS NULL THEN 0 ELSE client_account.money END) AS max_balance, " +
      "MIN(CASE WHEN client_account.money IS NULL THEN 0 ELSE client_account.money END) AS min_balance ");
  }

  private void selectCount(StringBuilder sqlQuery) {
    sqlQuery.append("SELECT COUNT(*) AS result ");
  }

  private void from(StringBuilder sql) {
    sql.append("FROM client ");
  }

  private void leftJoin(StringBuilder sql) {
    sql.append("LEFT JOIN client_account ON client_account.client=client.id AND client_account.actual=1 ");
  }

  private void andFilter(StringBuilder sqlQuery, ClientFilter filter, List<Object> params) {
    if (filter.fio != null) {
      if (!filter.fio.isEmpty()) {
        sqlQuery.append("AND (client.name LIKE ? OR client.surname LIKE ? OR client.patronymic LIKE ?) ");
        params.add("%"+filter.fio+"%");
        params.add("%"+filter.fio+"%");
        params.add("%"+filter.fio+"%");
      }
    }
  }

  private void whereActual(StringBuilder sqlQuery) {
    sqlQuery.append("WHERE client.actual=1 ");
  }

  private void groupBy(StringBuilder sqlQuery) {
    sqlQuery.append("GROUP BY client.id ");
  }

  private void offsetAndLimit(StringBuilder sqlQuery, ClientFilter filter, List<Object> params) {
    if (filter.offset != null && filter.limit != null) {
      sqlQuery.append("LIMIT ? OFFSET ?");
      params.add(filter.limit);
      params.add(filter.offset);
    }
  }

  private String getSortDirection(ClientFilter clientFilter) {
    if (clientFilter.sortDirection != null) return clientFilter.sortDirection.toString();
    return "";
  }

  private void orderBy(StringBuilder sqlQuery, ClientFilter filter) {
    String direct = getSortDirection(filter);

    if (filter.sortByEnum != null)
      switch (filter.sortByEnum) {
        case FULL_NAME:
          sqlQuery.append(String.format("ORDER BY client.surname %s, client.name %s, client.patronymic %s ", direct, direct, direct));
          return;
        case AGE:
          sqlQuery.append(String.format("ORDER BY age %s ", direct));
          return;
        case MIDDLE_BALANCE:
          sqlQuery.append(String.format("ORDER BY middle_balance %s ", direct));
          return;
        case MAX_BALANCE:
          sqlQuery.append(String.format("ORDER BY max_balance %s ", direct));
          return;
        case MIN_BALANCE:
          sqlQuery.append(String.format("ORDER BY min_balance %s ", direct));
          return;
      }
  }

  private ClientRecord getClientRecordFromResultSet(ResultSet resultSet) throws SQLException {
    ClientRecord clientRecord = new ClientRecord();
    clientRecord.id = resultSet.getInt("id");
    clientRecord.surname = resultSet.getString("surname");
    clientRecord.name = resultSet.getString("name");
    clientRecord.patronymic = resultSet.getString("patronymic");
    clientRecord.age = resultSet.getInt("age");
    clientRecord.middle_balance = resultSet.getFloat("middle_balance");
    clientRecord.max_balance = resultSet.getFloat("max_balance");
    clientRecord.min_balance = resultSet.getFloat("min_balance");
    return clientRecord;
  }

  @Override
  public int getRecordsCount(ClientFilter filter) {
    if (filter == null) return 0;

    List<Object> params = new ArrayList<>();
    StringBuilder sqlQuery = new StringBuilder();

    selectCount(sqlQuery);
    from(sqlQuery);
    whereActual(sqlQuery);
    andFilter(sqlQuery, filter, params);


    return jdbc.get().execute(connection -> {
      try (PreparedStatement ps = connection.prepareStatement(sqlQuery.toString())) {
        appendParams(ps, params);
        try(ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            return rs.getInt("result");
          }
        }
      }
      return 0;
    });
  }

  @Override
  public List<CharmRecord> getCharms() {
    return jdbc.get().execute(connection -> {
      String sql = "SELECT * FROM charm;";
      List<CharmRecord> charmRecords = Lists.newArrayList();
      PreparedStatement ps = connection.prepareStatement(sql);
      try {
        ResultSet rs = ps.executeQuery();
        try {
          while (rs.next()) charmRecords.add(getCharmFromResultSet(rs));
        } finally {
          rs.close();
        }
      } finally {
        ps.close();
      }
      return charmRecords;
    });
  }

  private CharmRecord getCharmFromResultSet(ResultSet rs) throws SQLException {
    CharmRecord charmRecord = new CharmRecord();
    charmRecord.id = rs.getInt("id");
    charmRecord.name = rs.getString("name");
    charmRecord.description = rs.getString("description");
    charmRecord.energy = rs.getFloat("energy");
    return charmRecord;
  }

  @Override
  public void renderClientList(String name, String author, ClientFilter filter, ClientRender render) {
    List<Object> params = Lists.newArrayList();
    StringBuilder sqlQuery = new StringBuilder();

    selectRecords(sqlQuery);
    from(sqlQuery);
    leftJoin(sqlQuery);
    whereActual(sqlQuery);
    andFilter(sqlQuery, filter, params);
    groupBy(sqlQuery);

    jdbc.get().execute(connection -> {

      PreparedStatement ps = connection.prepareStatement(sqlQuery.toString());
      appendParams(ps, params);

      try {

        ResultSet rs = ps.executeQuery();

        try {

          render.start(name, new Date());
          while (rs.next()) render.append(getClientRowFromResultSet(rs));
          render.finish(author);

        } finally {
          rs.close();
        }

      } finally {
        ps.close();
      }
      return null;
    });
  }

  private ClientRow getClientRowFromResultSet(ResultSet rs) throws SQLException {
    ClientRow row = new ClientRow();
    row.id = rs.getInt("id");
    row.surname = rs.getString("surname");
    row.name = rs.getString("name");
    row.patronymic = rs.getString("patronymic");
    row.age = rs.getInt("age");
    row.middle_balance = rs.getInt("middle_balance");
    row.max_balance = rs.getInt("max_balance");
    row.min_balance = rs.getInt("min_balance");
    return row;
  }

  private void setObjects(int from, PreparedStatement ps, Object... objects) throws SQLException {
    for (int i = from; i < objects.length+from; i++)
      ps.setObject(i, objects[i-from]);
  }
}
