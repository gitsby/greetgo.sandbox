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

// TODO: Все переменные и названия любых классох, директорий должны быть понятными !!!
// DONE
@Bean
// TODO: маппинг сделать понятным. О каком table идёт речь и что он делает
// DONE
@Mapping("/client-records")
// TODO: контроль соответственно назвать так, чтобы было понятно
// DONE
public class ClientRecordsController implements Controller{


    // TODO: переменную и класс регистра тоже переименуй
    // DONE
    public BeanGetter<ClientRecordsRegister> clientRecordsRegister;
    private String clientId;

    @NoSecurity
    @ToJson
    @Mapping("/get-client-records")
    // TODO: я ведь показывал правильное наименование. Должен использоваться суффикс ...Record для данного случая.
    // Если забыл, подойди и спроси. Я покажу, не кусаюсь.
    // DONE
    public ClientRecordsToSend getClientRecords(@Par("skipNumber") int skipNumber, @Par("limit") int limit,
                                            @Par("sortDirection") String sortDirection, @Par("sortType") String sortType,
                                            @Par("filterType") String  filterType, @Par("filterText") String filterText) {
        return clientRecordsRegister.get().getClientRecords(skipNumber,limit, sortDirection, sortType, filterType, filterText);
    }


    @NoSecurity
    @ToJson
    @Mapping("/get-charms")
    // TODO: неверный вывод ответа для данного случая. Нельзя выдавать просто массив строк для характера.
    // TODO: и где реализация для RegisterImpl ?
    // DONE
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
    // TODO: Почему статус ошибки возвращаешь в виде integer?
    // TODO: Нельзя так делать, для этого есть специальные классы и методы. переделай!
    // TODO: при добавлении надо возвращать ClientRecord
    // поинтересуйся у меня, если возникут вопросы по этому поводу.
    // DONE
    public Integer createClient(@Par("client") @Json Client client) {
        return clientRecordsRegister.get().createClient(client);
    }



    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/change-client")
    // TODO: Почему статусы возвращаешь в виде строки?
    // TODO: Нельзя так делать, для этого есть специальные классы и методы. переделай!
    // TODO: при редактирование надо возвращать ClientRecord
    // поинтересуйся у меня, если возникут вопросы по этому поводу.
    // DONE
    public String changeClient(@Par("client") @Json Client client) {
        return clientRecordsRegister.get().changeClient(client);
    }


    @NoSecurity
    @ToJson
    @MethodFilter(POST)
    @Mapping("/delete-client")
    // TODO: Почему статусы возвращаешь в виде строки?
    // TODO: Нельзя так делать, для этого есть специальные классы и методы. переделай!
    // DONE
    public String deleteClient(@Par("clientId") Integer clientId){
        return clientRecordsRegister.get().deleteClient(clientId);
    }


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

    @ToJson
    @NoSecurity
    @Mapping("/error-handling")
    public void errorHandling(){
        System.err.println("\nHEEEEYYYYAAAA\n");
        throw new NoCharmError();
    }



}
