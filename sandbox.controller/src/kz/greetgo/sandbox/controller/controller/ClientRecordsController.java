package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.sandbox.controller.errors.NoCharmError;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRecordsRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import static kz.greetgo.mvc.core.RequestMethod.*;

@Bean
// TODO: Это не контроллер, который отвечает только за ClientRecord. Он еще делает много чего другого.
// назови и контроллер, и маппинг так, чтобы было обобщенно и понятно.
@Mapping("/client-records")
public class ClientRecordsController implements Controller{

    public BeanGetter<ClientRecordsRegister> clientRecordsRegister;

    // TODO: убери, если не используешь
    private String clientId;

    // TODO: не везде такое можно писать. Что - то должно оставаться приватным. Убери там, где лишнее.
    @NoSecurity
    @ToJson
    @Mapping("/get-client-records")
    // TODO: 2.03.1. Входные параметры должны быть в одном классе-аргументе;
    public ClientRecordsToSend getClientRecords(@Par("skipNumber") int skipNumber, @Par("limit") int limit,
                                            @Par("sortDirection") String sortDirection, @Par("sortType") String sortType,
                                            @Par("filterType") String  filterType, @Par("filterText") String filterText) {
        return clientRecordsRegister.get().getClientRecords(skipNumber,limit, sortDirection, sortType, filterType, filterText);
    }


    @NoSecurity
    @ToJson
    @Mapping("/get-charms")
    // TODO: Зачем List оборачиваешь целым классом. Не надо такой путанницы. Сделай вывод просто List<Charm>
    public Charms getCharms(){
        return clientRecordsRegister.get().getCharms();
    }


    @NoSecurity
    @ToJson
    @Mapping("/get-client-details")
    // TODO: я ведь показывал правильное наименование. Должен использоваться суффикс ...Details для данного случая.
    // Если забыл, подойди и спроси. Я покажу, не кусаюсь.
    // DONE
    public Client getClientDetails(@Par("clientId") Integer clientId){
        return clientRecordsRegister.get().getClientDetails(clientId);
    }


    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/create-client")
    // TODO: при добавлении надо возвращать ClientRecord !!!
    // поинтересуйся у меня, если возникут вопросы по этому поводу.
    // !!! DONE
    public Integer createClient(@Par("client") @Json Client client) {
        return clientRecordsRegister.get().createClient(client);
    }



    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/change-client")
    // TODO: при редактирование надо возвращать ClientRecord !!!
    // поинтересуйся у меня, если возникут вопросы по этому поводу.
    // !!! DONE
    public String changeClient(@Par("client") @Json Client client) {
        return clientRecordsRegister.get().changeClient(client);
    }


    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/delete-client")
    public String deleteClient(@Par("clientId") Integer clientId){
        return clientRecordsRegister.get().deleteClient(clientId);
    }


    // TODO: 2.03.1. Входные параметры должны быть в одном классе-аргументе;
    @ToJson
    @MethodFilter(POST)
    @Mapping("/make-report")
    public String makeReport(@Par("sortDirection") String sortDirection, @Par("sortType") String sortType,
                             @Par("filterType") String  filterType, @Par("filterText") String filterText,
                             @ParSession("personId") String client, @Par("reportType") String reportType) throws Exception {
        return clientRecordsRegister.get().makeReport(sortDirection,sortType, filterType, filterText, client, reportType);
    }

    @NoSecurity
    @Mapping("/download-report")
    public void downloadReport(@Par("filename") String filename,
                               BinResponse response)
                                   throws Exception {
        //System.out.println(response.toString());
        clientRecordsRegister.get().downloadReport(filename,response);
    }

    // TODO: для чего этот метод?
    // Лишнее удаляй
    @ToJson
    @NoSecurity
    @Mapping("/error-handling")
    public void errorHandling(){
        System.err.println("\nHEEEEYYYYAAAA\n");
        throw new NoCharmError();
    }
}
