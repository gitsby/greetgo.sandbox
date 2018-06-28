package kz.greetgo.sandbox.controller.model;

import java.util.Date;
import java.util.List;

public class ClientToSave {

  public Integer id;
  public String name;
  public String surname;
  public String patronymic;
  public String gender;
  public Date birthDate;

  public Integer charm;

  public List<Address> addedAddresses;
  public List<Address> editedAddresses;
  public List<Address> deletedAddresses;

  public List<Phone> addedPhones;
  public List<Phone> deletedPhones;
  public List<Phone> editedPhones;

}
