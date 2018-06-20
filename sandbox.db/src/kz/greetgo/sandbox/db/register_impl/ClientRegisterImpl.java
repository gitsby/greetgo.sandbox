package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.render.ClientRender;
import kz.greetgo.sandbox.db.callbacks.ClientReportCallback;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<JdbcSandbox> jdbc;

  @Override
  public Details detail(Integer clientId) {
    return getDetails(clientId);
  }

  @Override
  public Integer save(ClientToSave clientToSave) {
    return insertOrUpdateClient(clientToSave);
  }

  private Integer insertOrUpdateClient(ClientToSave clientToSave) {
    return jdbc.get().execute(connection -> {
      try(PreparedStatement ps = connection.prepareStatement(clientToSave.id==null?getClientInsertQuery():getClientUpdateQuery())) {
        setObjectsToPS(ps, clientToSave);
        Integer id = null;
        try(ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            id = rs.getInt("id");
            insertPhonesAndAddresses(id, clientToSave);
          }
          return id;
        }
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
    return "INSERT INTO client(surname, name, patronymic, gender, birth_date, charm) VALUES (?, ?, ?, ?, ?, ?) RETURNING id;";
  }

  private String getClientUpdateQuery() {
    return "UPDATE client SET surname=?, name=?, patronymic=?, gender=?, birth_date=?, charm=? WHERE id=? RETURNING id;";
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
    jdbc.get().execute(connection -> {
      try(PreparedStatement ps = connection.prepareStatement(getPhoneInsertQuery())) {
        setObjectsToPS(ps, clientPhone);
        ps.execute();
      }
      return null;
    });
  }

  private void setObjectsToPS(PreparedStatement ps, ClientPhone clientPhone) throws SQLException {
    setObjects(1, ps,
      clientPhone.client,
      clientPhone.type.name(),
      clientPhone.number,
      clientPhone.number);
  }

  private String getPhoneInsertQuery() {
    return "INSERT INTO client_phone(client, type, number) " +
      "VALUES (?, ?, ?) " +
      "ON CONFLICT(client, number) DO UPDATE SET " +
      "number=?";
  }

  private void insertOrUpdateClientAddress(ClientAddress clientAddress) {
    jdbc.get().execute(connection -> {
      try(PreparedStatement ps = connection.prepareStatement(getAddressInsertQuery())) {
        setObjectsToPS(ps, clientAddress);
        ps.execute();
      }
      return null;
    });
  }

  private void setObjectsToPS(PreparedStatement ps, ClientAddress clientAddress) throws SQLException {
    setObjects(1, ps,
      clientAddress.client,
      clientAddress.type.name(),
      clientAddress.street,
      clientAddress.house,
      clientAddress.flat,
      clientAddress.street,
      clientAddress.house,
      clientAddress.flat);
  }

  private String getAddressInsertQuery() {
    return "INSERT INTO client_address(client, type, street, house, flat) " +
      "VALUES (?, ?, ?, ?, ?) " +
      "ON CONFLICT(client, type) DO UPDATE SET " +
      "street=?, house=?, flat=?";
  }

  private Details getDetails(Integer clientId) {
    Client client = getClient(clientId);
    Details details = new Details();
    details.id = clientId;
    details.surname = client.surname;
    details.name = client.name;
    details.patronymic = client.patronymic;
    details.gender = client.gender;
    details.charm = clientDao.get().getCharm(client.charm);
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

  private Client getClient(Integer clientId) {
    return jdbc.get().execute(connection -> {
      Client client = null;
      try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM client WHERE id=? AND actual=1")) {
        ps.setObject(1, clientId);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) client = getClientFromResultSet(rs);
        }
      }
      return client;
    });
  }

  private Client getClientFromResultSet(ResultSet rs) throws SQLException {
    Client client = new Client();
    client.id = rs.getInt("id");
    client.surname = rs.getString("surname");
    client.name = rs.getString("name");
    client.patronymic = rs.getString("patronymic");
    client.gender = Gender.valueOf(rs.getString("gender"));
    client.birthDate = rs.getDate("birth_date");
    client.charm = rs.getInt("charm");
    return client;
  }

  private ClientAddress getClientAddress(Integer clientId, AddressTypeEnum type) {
    return jdbc.get().execute(connection -> {
      ClientAddress clAddr = null;
      try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM client_address WHERE client=? AND type=? AND actual=1")) {
        ps.setObject(1, clientId);
        ps.setObject(2, type.name());
        try(ResultSet rs = ps.executeQuery()) {
          if (rs.next()) clAddr = getClientAddressFromResultSet(rs);
        }
      }
      return clAddr;
    });
  }

  private ClientAddress getClientAddressFromResultSet(ResultSet rs) throws SQLException {
    ClientAddress clAddr = new ClientAddress();
    clAddr.client = rs.getInt("client");
    clAddr.type = AddressTypeEnum.valueOf(rs.getString("type"));
    clAddr.street = rs.getString("street");
    clAddr.house = rs.getString("house");
    clAddr.flat = rs.getString("flat");
    return clAddr;
  }

  private void appendParams(PreparedStatement ps, ClientAddress curAddr, ClientAddress editAddr) throws SQLException {
    setObjects(1, ps,
      editAddr.street,
      editAddr.house,
      editAddr.flat,
      curAddr.client,
      curAddr.type.name());
  }


  @Override
  public void delete(Integer clientId) {
    clientDao.get().updateField(clientId, "actual", 0);
  }

  @Override
  public List<ClientRecord> getRecords(ClientFilter filter) {
    if (filter == null) return new ArrayList<>();

    List<Object> params = new ArrayList<>();
    StringBuilder sqlQuery = new StringBuilder();

    appendRecordsSelect(sqlQuery);
    appendRecordsFrom(sqlQuery);
    appendWhere(sqlQuery, filter, params);
    appendGroupBy(sqlQuery);
    appendOrderBy(sqlQuery, filter, params);
    appendOffsetAndLimit(sqlQuery, filter, params);

    List<ClientRecord> result = new ArrayList<>();

    jdbc.get().execute(connection -> {
      try (PreparedStatement ps = connection.prepareStatement(sqlQuery.toString())) {
        appendParams(ps, params);
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            result.add(getClientRecordFromResultSet(rs));
          }
        }
      }
      return null;
    });

    return result;
  }

  private void appendParams(PreparedStatement ps, List<Object> params) throws SQLException {
    for (int i = 1; i <= params.size(); i++) ps.setObject(i, params.get(i-1));
  }

  private void appendRecordsSelect(StringBuilder sqlQuery) {
    sqlQuery.append("SELECT m.id, surname, name, patronymic, DATE_PART('year', '2012-01-01'::date) - DATE_PART('year', '2011-10-02'::date) AS age, AVG(money) AS middle_balance, MAX(money) AS max_balance, MIN(money) AS min_balance ");
  }

  private void appendRecordsFrom(StringBuilder sqlQuery) {
    sqlQuery.append("FROM client m ");
    sqlQuery.append("LEFT JOIN client_account x1 ON x1.client=m.id AND m.actual=1 ");
  }

  private void appendWhere(StringBuilder sqlQuery, ClientFilter filter, List<Object> params) {
    if (filter.fio != null) {
      if (!filter.fio.isEmpty()) {
        sqlQuery.append("WHERE (m.name LIKE ? OR m.surname LIKE ? OR m.patronymic LIKE ?) AND m.actual=1 ");
        params.add("%"+filter.fio+"%");
        params.add("%"+filter.fio+"%");
        params.add("%"+filter.fio+"%");
      }
    }
  }

  private void appendGroupBy(StringBuilder sqlQuery) {
    sqlQuery.append("GROUP BY m.id, m.surname, m.name, m.patronymic ");
  }

  private void appendOffsetAndLimit(StringBuilder sqlQuery, ClientFilter filter, List<Object> params) {
    if (filter.offset != null && filter.limit != null) {
      sqlQuery.append("LIMIT ? OFFSET ?");
      params.add(filter.limit);
      params.add(filter.offset);
    }
  }

  private void appendOrderBy(StringBuilder sqlQuery, ClientFilter filter, List<Object> params) {
    String direct = getSortDirection(filter);

    if (filter.sortByEnum != null)
      switch (filter.sortByEnum) {
        case FULL_NAME:
          sqlQuery.append(String.format("ORDER BY m.surname %s, m.name %s, m.patronymic %s ", direct, direct, direct));
          return;
        case AGE:
          sqlQuery.append(String.format("ORDER BY m.age %s ", direct));
          return;
        case MIDDLE_BALANCE:
          sqlQuery.append(String.format("ORDER BY middle_balance %s ", direct));
          return;
        case MAX_BALANCE:
          sqlQuery.append(String.format("ORDER BY max_balance %s ", direct));
          params.add(direct);
          return;
        case MIN_BALANCE:
          sqlQuery.append(String.format("ORDER BY min_balance %s ", direct));
          params.add(direct);
          return;
      }
    sqlQuery.append("ORDER BY m.id ");
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

  private String getSortDirection(ClientFilter clientFilter) {
    if (clientFilter.sortDirection != null) return clientFilter.sortDirection.toString();
    return "";
  }

  @Override
  public int getRecordsCount(ClientFilter filter) {
    if (filter == null) return 0;

    List<Object> params = new ArrayList<>();
    StringBuilder sqlQuery = new StringBuilder();

    appendRecordsCountSelect(sqlQuery);
    appendRecordsCountFrom(sqlQuery);
    appendWhere(sqlQuery, filter, params);

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

  private void appendRecordsCountFrom(StringBuilder sqlQuery) {
    sqlQuery.append("FROM client m ");
  }

  private void appendRecordsCountSelect(StringBuilder sqlQuery) {
    sqlQuery.append("SELECT COUNT(*) AS result ");
  }


  @Override
  public List<CharmRecord> getCharms() {
    List<CharmRecord> charmRecords = new ArrayList<>();

    jdbc.get().execute(connection -> {
      try(PreparedStatement ps = connection.prepareStatement("SELECT * FROM charm")) {
        try(ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            charmRecords.add(getCharmFromResultSet(rs));
          }
        }
      }
      return null;
    });

    return charmRecords;
  }

  @Override
  public void renderClientList(String name, String author, ClientRender render) {
    jdbc.get().execute(new ClientReportCallback(name, author, render));
  }

  private CharmRecord getCharmFromResultSet(ResultSet rs) throws SQLException {
    CharmRecord charmRecord = new CharmRecord();
    charmRecord.id = rs.getInt("id");
    charmRecord.name = rs.getString("name");
    charmRecord.description = rs.getString("description");
    charmRecord.energy = rs.getFloat("energy");
    return charmRecord;
  }

  private void setObjects(int from, PreparedStatement ps, Object... objects) throws SQLException {
    for (int i = from; i < objects.length+from; i++)
      ps.setObject(i, objects[i-from]);
  }
}
