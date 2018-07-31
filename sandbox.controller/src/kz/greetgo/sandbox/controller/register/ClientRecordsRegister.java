package kz.greetgo.sandbox.controller.register;


import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.model.*;

public interface ClientRecordsRegister {
    ClientRecordsToSend getClientRecords(int skipNumber, int limit, String sortDirection, String sortType, String filterType, String filterText);

    Client getClientDetails(Integer clientId);

    Integer createClient(Client client);

    Charms getCharms();

    String changeClient(Client client);

    String deleteClient(Integer clientId);

    String makeReport(String sortDirection, String sortType, String filterType,
                      String filterText,String client, String reportType) throws Exception;

    void downloadReport(String filename,BinResponse response) throws Exception;

}
