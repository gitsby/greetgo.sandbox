package kz.greetgo.sandbox.controller.register;


import kz.greetgo.sandbox.controller.model.ArrayUsers;
import kz.greetgo.sandbox.controller.model.Table;
import kz.greetgo.sandbox.controller.model.TableModel;
import kz.greetgo.sandbox.controller.model.User;

import java.util.ArrayList;
import java.util.List;

public interface TableRegister {
    ArrayList<TableModel> getTableData(int skipNumber, int limit, String sortDirection, String sortType);
    int tableSize();
    User getExactUser(String userID);
    String createUser(User user);
    String changeUser(User user);
    String deleteUser(String userID);
    Boolean checkIfThereUser(String userID);

}
