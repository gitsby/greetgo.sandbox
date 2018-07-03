package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.dbmodels.DbCharm;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClient;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClientAddress;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClientPhone;
import org.apache.ibatis.annotations.*;

public interface TableDao {

    @Delete("delete from client")
    void deleteClients();

    @Delete("delete from client_addr")
    void deleteClientAddrs();

    @Delete("delete from charm")
    void deleteCharms();

    @Delete("delete from client_account")
    void deleteClientAccounts();

    @Delete("delete from client_phone")
    void deletePhones();


//    @Delete("delete from client" +
//            "where id=#{id}")
//    void deleteClient(@Param("id") int id);
//
//    @Delete("delete from client_phone" +
//            "where client=#{id}")
//    void deletePhone(@Param("id") int id);
//
//    @Delete("delete from client_addr" +
//            "where client=#{id}")
//    void deleteAddress(@Param("id") int id);
//
//    @Delete("delete from client_account" +
//            "where client=#{id}")
//    void deleteAccount(@Param("id") int id);




    @Update("alter sequence charm_id_seq restart with 1")
    void charmSerialToStart();

    @Update("alter sequence client_id_seq restart with 1")
    void clientSerialToStart();

    @Update("alter sequence client_account_id_seq restart with 1")
    void clientAccountSerialToStart();

    @Select("select client.id, client.surname, client.charm, client.name, client.patronymic,client.birthDate,client.gender, client.validity from client where id = #{userID}")
    @Results({
            @Result(property = "id",column = "client.id"),
            @Result(property = "surname",column = "client.surname"),
            @Result(property = "name",column = "client.name"),
            @Result(property = "patronymic",column = "client.patronymic"),
            @Result(property = "birthDate",column = "client.birthDate"),
            @Result(property = "charm",column = "client.charm"),
            @Result(property = "gender",column = "client.gender"),
            @Result(property = "validity", column = "client.validity")
    })
    DbClient getExactClient(@Param("userID") int userID);

//    @Select("select function(#{},#{},#{},#{},#{},#{})")
//    String getTableWithFilters(@Param("") short smth);

    @Select("select last_value from client_id_seq")
    int getLastClientID();

    @Select("select count(id) from client")
    String getTableSize();

    @Select("select charm.id from charm where charm.name=#{charm}")
    Integer getCharmId(String charm);


    @Select("select case when count(client.id) >= 1 then" +
            "cast( 1 as BIT) else cast( 0 as BIT)" +
            "end as checkForExistence from client" +
            "where client.id = #{userID} and client.validity=1")
    Boolean checkForExistence(@Param("userID") int userID);

    @Select("select count(1) from client where id=#{userID} and validity=true")
    Integer countClientsWithUserID(@Param("userID") int userID);

    @Select("select charm.id, charm.name, charm.description, charm.energy " +
            "from charm where charm.id=#{charmID}")
    @Results({
                    @Result(property = "id", column = "id"),
                    @Result(property = "name", column = "name"),
                    @Result(property = "description", column = "description"),
                    @Result(property = "energy", column = "energy"),
            })
    DbCharm getCharm(@Param("charmID") int charmID);

    @Select("select client_phone.client, client_phone.number, client_phone.type, client_phone.validity from client_phone where client_phone.client=#{userID} and client_phone.validity=true")
    @Results({
            @Result(property = "client",column = "client_phone.client"),
            @Result(property = "number", column = "client_phone.number"),
            @Result(property = "type", column = "client_phone.type"),
            @Result(property = "validity", column = "client_phone.validity")

    })
    DbClientPhone[] getPhones(@Param("userID") int userID);

    @Select("select client_addr.client, client_addr.type,client_addr.street, client_addr.house, client_addr.flat  from" +
            " client_addr where client=#{userID} and type=#{type}")
    @Results({
            @Result(property = "client", column = "client"),
            @Result(property = "type",column = "type"),
            @Result(property = "street", column = "street"),
            @Result(property = "house", column = "house"),
            @Result(property = "flat", column = "flat"),
    })
    DbClientAddress getClientAddress(@Param("userID") int userID,@Param("type") String type);

    @Insert("insert into client(name,surname,patronymic,gender,charm,validity,birthDate) values(" +
            "#{name},#{surname},#{patronymic},#{gender},#{charm},"+
            "#{validity},#{birthDate})")
    void insertClient(DbClient dbClient);



//    @Insert("insert into charm(name, description, energy) values(" +
//            "#{name},#{description},#{energy})")
//    void insertCharm(String name,String description, float energy);

    @Insert("insert into charm(name, description, energy) values(" +
            "#{name},#{description},#{energy})")
    void insertCharm(DbCharm dbCharm);

    @Insert("insert into client_addr(client, type, street, house, flat)" +
            "values(#{client},#{type},#{street},#{house},#{flat})")
    void insertAddress(DbClientAddress dbClientAddress);



    @Insert("insert into client_phone(client,number,type, validity)" +
            "values(#{client},#{number},#{type},#{validity})")
    void insertPhone(DbClientPhone dbPhone);


    @Insert("update client " +
            "set " +
            "name=#{name}, " +
            "surname=#{surname}, " +
            "charm=#{charm}, " +
            "gender=#{gender}, " +
            "patronymic=#{patronymic}, " +
            "birthDate=#{birthDate}, "+
            "validity=#{validity} "+
            "where id = #{id}")
    void updateClient(DbClient dbClient);

    @Insert("update client_addr set " +
            "street=#{street}," +
            "house=#{house} " +
            "where client = #{client} and type=#{type}")
    void updateAddress(DbClientAddress dbClientAddress);

    @Insert("update client_phone set " +
            "number = #{number},"+
            "type=#{type}," +
            "validity=#{validity}" +
            "where client = #{client}")
    void updatePhone(DbClientPhone dbClientPhone);

    @Update("update client set " +
            "validity=0 " +
            "where id=#{userdID}")
    void deleteClient(@Param("userID") int userID);

    @Update("update client_phone set" +
            "validity=0" +
            "where client=#{userID}")
    void deletePhone(@Param("userID") int userID);

    @Update("update client_account set" +
            "validity=0" +
            "where client=#{userID}")
    void deleteAccount(@Param("userID") int userID);

/////////////////////////////

//

//    @Select("select count(id) from client")
//    String getTableSize();

}
