package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.model.dbmodels.DbCharm;
import kz.greetgo.util.RND;

import java.util.Random;


public class TestDataGenerator {
    Client generateClient(){
        Client client = new Client();
        client.id = -1;
        client.name= RND.str(10);
        client.surname= RND.str(10);
        client.patronymic= RND.str(10);
        client.birthDate=RND.plusLong((long) Math.pow(10,14));
        client.genderType= GenderType.values()[new Random().nextInt(GenderType.values().length)];
        client.charmId=0;
        client.registeredAddress= generateRandomAddress();
        client.factualAddress= generateRandomAddress();
        client.validity=true;
        int pl=1+RND.plusInt(4);
        Phone[] notFixedArray = new Phone[pl];
        notFixedArray[0]=generateRandomPhone();
        notFixedArray[0].validity=true;
        for(int i=1; i<pl; i++){
            notFixedArray[i]=generateRandomPhone(RND.someEnum(PhoneType.values()));
            notFixedArray[i].validity=true;
        }
        client.phones=notFixedArray;
        return client;
    }
    Address generateRandomAddress(){
        Address address= new Address();
        address.street=RND.str(10);
        address.flat=RND.str(10);
        address.house=RND.str(10);
        return address;
    }

    Phone generateRandomPhone(){
        Phone phone = new Phone();
        phone.number=RND.intStr(11);
        phone.phoneType=PhoneType.MOBILE;
        return phone;
    }

    Phone generateRandomPhone(PhoneType phoneType){
        Phone phone = new Phone();
        phone.number=RND.intStr(11);
        phone.phoneType=RND.someEnum(PhoneType.values());
        return phone;
    }

    DbCharm generateRandomCharm(String name){
        DbCharm dbCharm = new DbCharm();
        dbCharm.name = name;
        dbCharm.description = RND.str(10);
        dbCharm.energy = RND.plusInt(1000)+0f;
        return dbCharm;
    }

}
