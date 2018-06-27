package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.util.RND;

import static kz.greetgo.sandbox.controller.model.PhoneType.MOBILE;

public class TestDataGenerator {
    User generateUser(){
        User user = new User();
        user.name= RND.str(10);
        user.surname= RND.str(10);
        user.patronymic= RND.str(10);
        user.birthDate=RND.plusLong((long) Math.pow(10,14));
        user.genderType= RND.someEnum(GenderType.values());
        user.charm=RND.someEnum(CharmType.values());
        user.registeredAddress= generateRandomAddress();
        user.factualAddress= generateRandomAddress();
        int pl=1+RND.plusInt(4);
        Phone[] notFixedArray = new Phone[pl];
        notFixedArray[0]=generateRandomPhone();
        for(int i=1; i<pl; i++){
            notFixedArray[i]=generateRandomPhone(RND.someEnum(PhoneType.values()));
        }
        user.phones=notFixedArray;
        return user;
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
        phone.number=RND.str(11);
        phone.phoneType=PhoneType.MOBILE;
        return phone;
    }

    Phone generateRandomPhone(PhoneType phoneType){
        Phone phone = new Phone();
        phone.number=RND.str(11);
        phone.phoneType=RND.someEnum(PhoneType.values());
        return phone;
    }

}
