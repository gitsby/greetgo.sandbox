package kz.greetgo.sandbox.db.register_impl;


import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.TableRegister;

@Bean
public class TableRegisterImpl implements TableRegister{

    @Override
    public TableToSend getTableData(int skipNumber, int limit, String sortDirection, String sortType){
        return null;
    }
    @Override
    public User getExactUser(int userID){
        return null;
    }

    @Override
    public int createUser(User user){
        return -1;
    }
    @Override
    public String changeUser(User user){
        return null;
    }
    @Override
    public String deleteUser(int userID){
        return null;
    }

}


