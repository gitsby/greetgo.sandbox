package kz.greetgo.sandbox.controller.model;

import java.util.Arrays;
import java.util.Date;

public class ClientToSave {

  public Integer id;
  public String name;
  public String surname;
  public String patronymic;
  public String gender;
  public Date birthDate;

  public Integer charm;

  public Address[] addedAddresses;
  public Address[] editedAddresses;
  public Address[] deletedAddresses;

  public Phone[] addedPhones;
  public Phone[] deletedPhones;
  public Phone[] editedPhones;

  @Override
  public String toString() {
    return "ClientToSave{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", surname='" + surname + '\'' +
      ", patronymic='" + patronymic + '\'' +
      ", gender='" + gender + '\'' +
      ", birthDate=" + birthDate +
      ", charm=" + charm +
      ", addedAddresses=" + Arrays.toString(addedAddresses) +
      ", editedAddresses=" + Arrays.toString(editedAddresses) +
      ", deletedAddresses=" + Arrays.toString(deletedAddresses) +
      ", addedPhones=" + Arrays.toString(addedPhones) +
      ", deletedPhones=" + Arrays.toString(deletedPhones) +
      ", editedPhones=" + Arrays.toString(editedPhones) +
      '}';
  }
}
