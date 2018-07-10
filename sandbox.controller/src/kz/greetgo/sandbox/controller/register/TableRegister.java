package kz.greetgo.sandbox.controller.register;


import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.model.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public interface TableRegister {
    TableToSend getTableData(Integer skipNumber, Integer limit, String sortDirection, String sortType, String filterType, String filterText);

    User getExactUser(Integer userID);

    Integer createUser(User user);

    String[] getCharms();

    String changeUser(User user);

    String deleteUser(Integer userID);

    String makeReport(String sortDirection, String sortType, String filterType,
                      String filterText,String user, String reportType) throws Exception;

    void downloadReport(String filename,BinResponse response) throws Exception;


}
