package kz.greetgo.sandbox.controller.register;


import kz.greetgo.sandbox.controller.model.*;

import java.util.ArrayList;
import java.util.List;

public interface TableRegister {
    TableToSend getTableData(Integer skipNumber, Integer limit, String sortDirection, String sortType, String filterType, String filterText);

    User getExactUser(Integer userID);

    Integer createUser(User user);

    String changeUser(User user);

    String deleteUser(Integer userID);


}
