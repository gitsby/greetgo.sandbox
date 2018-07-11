package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.dbmodels.*;
import org.apache.ibatis.annotations.*;
import kz.greetgo.sandbox.controller.model.*;

import java.util.ArrayList;


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
    Integer getLastClientID();

    @Select("select count(id) from filtered_names(#{filterType},(#{filterText})")
    Integer getTableSize(@Param("filterType") String filterType,
                            @Param("filterText") String filterText);

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

    @Insert("insert into client_account(client,money,number,registered_at, validity) values(" +
            "#{client},#{money},#{number},#{registered_at}, #{validity})")
    void insertAccount(DbClientAccount dbClientAccount);



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
            "validity=false " +
            "where id=#{userID}")
    void deleteClient(@Param("userID") int userID);

    @Update("update client_phone set " +
            "validity=false " +
            "where client=#{userID}")
    void deletePhone(@Param("userID") int userID);

    @Update("update client_account set " +
            "validity=false " +
            "where client=#{userID}")
    void deleteAccount(@Param("userID") int userID);


    @Update("update client_account set " +
            "money=#{money}" +
            "where client_account.id=#{id}")
    void updateAccount(DbClientAccount dbClientAccount);


    @Select("select id from client_account "+
            "where client=#{userID}")
    Integer[] getAccount(@Param("userID") int userID);

    @Select("select processed.id, " +
            "processed.fullName, " +
            "processed.age, " +
            "processed.charm, " +
            "processed.minBalance, " +
            "processed.maxBalance, " +
            "processed.totalBalance from " +
            "sortedFullNameDesc( "+
            "#{skip_number}, " +
            "#{limit_number}, " +
            "#{filter_type}, " +
            "#{filter_text})" +
            "as processed")
    ArrayList<TableModel> getFullNameDesc(
            @Param("skip_number") Integer skipNumber,
            @Param("limit_number") Integer limitNumber,
            @Param("filter_type") String filterType,
            @Param("filter_text") String filterText
    );
    @Select("select processed.id, " +
            "processed.fullName, " +
            "processed.age, " +
            "processed.charm, " +
            "processed.minBalance, " +
            "processed.maxBalance, " +
            "processed.totalBalance from " +
            "sortedFullNameAsc( "+
            "#{skip_number}, " +
            "#{limit_number}, " +
            "#{filter_type}, " +
            "#{filter_text})" +
            "as processed")
    ArrayList<TableModel> getFullNameAsc(
            @Param("skip_number") Integer skipNumber,
            @Param("limit_number") Integer limitNumber,
            @Param("filter_type") String filterType,
            @Param("filter_text") String filterText
    );

    @Select("select processed.id, " +
            "processed.fullName, " +
            "processed.age, " +
            "processed.charm, " +
            "processed.minBalance, " +
            "processed.maxBalance, " +
            "processed.totalBalance from " +
            "sortedMaxBalanceDesc( "+
            "#{skip_number}, " +
            "#{limit_number}, " +
            "#{filter_type}, " +
            "#{filter_text})" +
            "as processed")
    ArrayList<TableModel> getMaxBalanceDesc(
            @Param("skip_number") Integer skipNumber,
            @Param("limit_number") Integer limitNumber,
            @Param("filter_type") String filterType,
            @Param("filter_text") String filterText
    );

    @Select("select processed.id, " +
            "processed.fullName, " +
            "processed.age, " +
            "processed.charm, " +
            "processed.minBalance, " +
            "processed.maxBalance, " +
            "processed.totalBalance from " +
            "sortedMaxBalanceAsc( "+
            "#{skip_number}, " +
            "#{limit_number}, " +
            "#{filter_type}, " +
            "#{filter_text})" +
            "as processed")
    ArrayList<TableModel> getMaxBalanceAsc(
            @Param("skip_number") Integer skipNumber,
            @Param("limit_number") Integer limitNumber,
            @Param("filter_type") String filterType,
            @Param("filter_text") String filterText
    );

    @Select("select processed.id, " +
            "processed.fullName, " +
            "processed.age, " +
            "processed.charm, " +
            "processed.minBalance, " +
            "processed.maxBalance, " +
            "processed.totalBalance from " +
            "sortedMinBalanceAsc( "+
            "#{skip_number}, " +
            "#{limit_number}, " +
            "#{filter_type}, " +
            "#{filter_text})" +
            "as processed")
    ArrayList<TableModel> getMinBalanceAsc(
            @Param("skip_number") Integer skipNumber,
            @Param("limit_number") Integer limitNumber,
            @Param("filter_type") String filterType,
            @Param("filter_text") String filterText
    );

    @Select("select processed.id, " +
            "processed.fullName, " +
            "processed.age, " +
            "processed.charm, " +
            "processed.minBalance, " +
            "processed.maxBalance, " +
            "processed.totalBalance from " +
            "sortedMinBalanceDesc( "+
            "#{skip_number}, " +
            "#{limit_number}, " +
            "#{filter_type}, " +
            "#{filter_text})" +
            "as processed")
    ArrayList<TableModel> getMinBalanceDesc(
            @Param("skip_number") Integer skipNumber,
            @Param("limit_number") Integer limitNumber,
            @Param("filter_type") String filterType,
            @Param("filter_text") String filterText
    );

    @Select("select processed.id, " +
            "processed.fullName, " +
            "processed.age, " +
            "processed.charm, " +
            "processed.minBalance, " +
            "processed.maxBalance, " +
            "processed.totalBalance from " +
            "sortedTotalBalanceAsc( "+
            "#{skip_number}, " +
            "#{limit_number}, " +
            "#{filter_type}, " +
            "#{filter_text})" +
            "as processed")
    ArrayList<TableModel> getTotalBalanceAsc(
            @Param("skip_number") Integer skipNumber,
            @Param("limit_number") Integer limitNumber,
            @Param("filter_type") String filterType,
            @Param("filter_text") String filterText
    );

    @Select("select processed.id, " +
            "processed.fullName, " +
            "processed.age, " +
            "processed.charm, " +
            "processed.minBalance, " +
            "processed.maxBalance, " +
            "processed.totalBalance from " +
            "sortedTotalBalanceDesc( "+
            "#{skip_number}, " +
            "#{limit_number}, " +
            "#{filter_type}, " +
            "#{filter_text})" +
            "as processed")
    ArrayList<TableModel> getTotalBalanceDesc(
            @Param("skip_number") Integer skipNumber,
            @Param("limit_number") Integer limitNumber,
            @Param("filter_type") String filterType,
            @Param("filter_text") String filterText
    );

    @Select("select processed.id, " +
            "processed.fullName, " +
            "processed.age, " +
            "processed.charm, " +
            "processed.minBalance, " +
            "processed.maxBalance, " +
            "processed.totalBalance from " +
            "sortedAgeAsc( "+
            "#{skip_number}, " +
            "#{limit_number}, " +
            "#{filter_type}, " +
            "#{filter_text})" +
            "as processed")
    ArrayList<TableModel> getAgeAsc(
            @Param("skip_number") Integer skipNumber,
            @Param("limit_number") Integer limitNumber,
            @Param("filter_type") String filterType,
            @Param("filter_text") String filterText
    );
    @Select("select processed.id, " +
            "processed.fullName, " +
            "processed.age, " +
            "processed.charm, " +
            "processed.minBalance, " +
            "processed.maxBalance, " +
            "processed.totalBalance from " +
            "sortedAgeDesc( "+
            "#{skip_number}, " +
            "#{limit_number}, " +
            "#{filter_type}, " +
            "#{filter_text})" +
            "as processed")
    ArrayList<TableModel> getAgeDesc(
            @Param("skip_number") Integer skipNumber,
            @Param("limit_number") Integer limitNumber,
            @Param("filter_type") String filterType,
            @Param("filter_text") String filterText
    );

    @Select("select name from charm")
    String[] getCharms();
}
