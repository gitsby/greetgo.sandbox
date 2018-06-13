package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.Character;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientDao {

  @Insert("insert into client (   name,    surname,    patronymic,    gender, charm, birth_date) " +
    "                  values ( #{name}, #{surname}, #{patronymic}, 'MALE',1,#{birthDate})")
  void insertIntoClient(ClientToSave personDot);

  @Insert("insert into client_address (   clientid,    type,    street,    house, flat) " +
    "                  values ( #{clientId}, #{type}, #{street}, #{house},#{flat})")
  void insertIntoAddress(Address address);


  @Insert("insert into client_phone (   clientid,    number,    type) " +
    "                  values ( #{client}, #{number}, #{type})")
  void insertPhones(Phone phone);

  @Select("select id, name from characters")
  List<Character> getCharacters();

  @Select("select\n" +
    "  client.id,\n" +
    "  client.name,\n" +
    "  client.surname,\n" +
    "  client.patronymic,\n" +
    "  gender,\n" +
    "  c2.name as character\n" +
    "  from client\n" +
    "  join characters c2 on client.charm = c2.id\n" +
    "  where concat(Lower(client.name),Lower(client.surname), Lower(client.patronymic)) like '%' || #{searchName} ||'%'" +
    "  limit #{sliceNum} * #{paginationPage} + #{sliceNum} offset #{sliceNum}*#{paginationPage}")
  List<ClientRecord> getClientRecords(ClientRecordFilter clientRecordFilter);

  @Select("(select\n" +
    "  client.id,\n" +
    "  client.name,\n" +
    "  client.surname,\n" +
    "  client.patronymic,\n" +
    "  gender,\n" +
    "  c2.name as character\n" +
    "  from client\n" +
    "  join characters c2 on client.charm = c2.id\n" +
    "  where concat(Lower(client.name),Lower(client.surname), Lower(client.patronymic)) like '%' || #{searchName} ||'%'" +
    "  limit #{sliceNum} * #{paginationPage} + #{sliceNum} offset #{sliceNum}*#{paginationPage})" +
    "  order by #{columnName}")
  List<ClientRecord> getClientRecordsAsc(ClientRecordFilter clientRecordFilter);


  @Select("")
  List<ClientRecord> getClientRecordsDesc(ClientRecordFilter clientRecordFilter);

  @Select("select " +
    "client.id, " +
    "client.name, " +
    "client.surname, " +
    "client.patronymic, " +
    "gender, " +
    "birth_date, " +
    "c2.name as character\n" +
    "from client\n" +
    "join characters c2 on client.charm = c2.id\n")
  ClientDetails getClientById(int id);
}
