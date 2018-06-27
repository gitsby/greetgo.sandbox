package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.User;
import kz.greetgo.sandbox.controller.register.model.UserParamName;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import org.apache.ibatis.annotations.*;

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

    @Select("select * from client where id = #{id}")
    User getExactClient(@Param("id") int id);

    @Select("select function(#{},#{},#{},#{},#{},#{})")
    String getTableWithFilters(@Param("") short smth);

    @Select("select last_value from client_id_seq")
    int getLastClientID();


    @Select("select count(id) from client")
    String getTableSize();





    @Update("")
    String updateClients();
}

