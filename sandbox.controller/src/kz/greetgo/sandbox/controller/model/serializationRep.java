package kz.greetgo.sandbox.controller.model;

import com.google.gson.Gson;

import static kz.greetgo.sandbox.controller.model.AddressType.*;
import static kz.greetgo.sandbox.controller.model.CharmType.BOI;
import static kz.greetgo.sandbox.controller.model.PhoneType.*;

public class serializationRep {
    public static void main(String[] args){
        ArrayUsers Users = new ArrayUsers();
        Gson gson =new Gson();
        for(int i=0; i<5; i++){
            Address fact = new Address();
            fact.addressType=FACT;
            fact.flat="12";
            fact.house="12";
            fact.street="12";
            Address reg = new Address();
            reg.addressType=REG;
            reg.flat="12";
            reg.house="12";
            reg.street="12";
            Phone mobile = new Phone();
            mobile.number="123456789";
            mobile.phoneType=MOBILE;
            Phone home = new Phone();
            home.number="123456789";
            home.phoneType=HOME;
            Phone[] phones = {home, mobile};
            User user = new User();
            user.name="oo";
            user.surname="oo1";
            user.birthDate=123456;
            user.factualAddress=fact;
            user.registeredAddress=reg;
            user.charm=BOI;
            user.phones=phones;
            user.id=Integer.toString(Users.data.size());
            Users.data.add(user);
        }
        System.out.println(gson.toJson(Users));
    }

}
