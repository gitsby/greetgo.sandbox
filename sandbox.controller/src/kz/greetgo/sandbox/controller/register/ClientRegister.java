package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.RequestOptions;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;

import java.util.List;

public interface ClientRegister {

    List<ClientRecord> getClientList(RequestOptions options);

    int getClientListCount(String filter);

    void deleteClient(int clientId);

    ClientRecord addClient(ClientDetails details);

    ClientRecord editClient(ClientDetails details);

    ClientDetails getClientDetails(int clientId);

    List<Charm> getCharms();

    void renderClientList(RequestOptions options,
                          ClientRecordsReportView view,
                          String username, String link);

}
