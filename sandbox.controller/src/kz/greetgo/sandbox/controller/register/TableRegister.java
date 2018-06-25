package kz.greetgo.sandbox.controller.register;


import kz.greetgo.sandbox.controller.model.*;

import java.util.ArrayList;
import java.util.List;

public interface TableRegister {
    TableToSend getTableData(int skipNumber, int limit, String sortDirection, String sortType);
    int tableSize();
    User getExactUser(String userID);
    String getLastId();
    String createUser(User user);
    String changeUser(User user);
    String deleteUser(String userID);
    Boolean checkIfThereUser(String userID);

}
