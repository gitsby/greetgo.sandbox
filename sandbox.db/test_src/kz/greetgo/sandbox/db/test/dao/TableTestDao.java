package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.User;
import kz.greetgo.sandbox.controller.model.dbmodels.DbCharm;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClient;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClientAddress;
import kz.greetgo.sandbox.controller.model.dbmodels.DbClientPhone;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface TableTestDao {

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

    @Update("alter sequence charm_id_seq restart with 1")
    void charmSerialToStart();

    @Update("alter sequence client_id_seq restart with 1")
    void clientSerialToStart();

    @Update("alter sequence client_account_id_seq restart with 1")
    void clientAccountSerialToStart();

//    @Select("select * from client where id = #{id}")
//    User getExactClient(@Param("id") int id);
//
//    @Select("select function(#{},#{},#{},#{},#{},#{})")
//    String getTableWithFilters(@Param("") short smth);
//
     @Select("select last_value from client_id_seq")
     int getLastClientID();
//

//    @Select("select count(id) from client")
//    String getTableSize();

    @Insert("insert into charm(name, description, energy) values(" +
            "#{name},#{description},#{energy})")
    void insertCharm(DbCharm dbCharm);

    @Select("select id from charm where name=#{name}")
    Integer getCharmId(@Param("name") String name);

    @Insert("insert into client(name,surname,patronymic,gender,charm,validity,birthDate) values(" +
            "#{name},#{surname},#{patronymic},#{gender},#{charm},"+
            "#{validity},#{birthDate})")
    void insertClient(DbClient dbClient);

    @Insert("insert into client_addr(client, type, street, house, flat)" +
            "values(#{client},#{type},#{street},#{house},#{flat})")
    void insertAddress(DbClientAddress dbClientAddress);

    @Insert("insert into client_phone(client,number,type, validity)" +
            "values(#{client},#{number},#{type},#{validity})")
    void insertPhone(DbClientPhone dbPhone);
}

