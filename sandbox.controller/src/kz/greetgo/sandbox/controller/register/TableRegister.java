package kz.greetgo.sandbox.controller.register;


import kz.greetgo.sandbox.controller.model.User;

import java.util.List;

public interface TableRegister {
    List<User> getTableData(int skipNumber, int limit, char sortDirection, char sortType);
    int tableSize();
    User getExactUser(String userID);
    String createUser(User user);
    String changeUser(User user);
    String deleteUser(String userID);
    Boolean checkIfThereUser(String userID);

}
