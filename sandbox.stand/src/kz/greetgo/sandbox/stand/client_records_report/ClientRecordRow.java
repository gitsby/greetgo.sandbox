package kz.greetgo.sandbox.stand.client_records_report;

public class ClientRecordRow {

  public int id;
  public String name;
  public String surname;
  public String patronymic;
  public String charm;

  public int age = 0;
  public double accBalance = 0;
  public double maxBalance = 0;
  public double minBalance = 0;

  public String[] toStringArray() {
    return new String[]{id + "", surname, name, patronymic, age + "", charm, accBalance + "", minBalance + "", maxBalance + ""};
  }
//
//  public static ClientRecordRow toClientRecordRow(ClientRecord clientRecord){
//    ClientRecordRow recordRow = new ClientRecordRow();
//    recordRow.id = clientRecord.id;
//    recordRow.surname = clientRecord.surname;
//    recordRow.name = clientRecord.name;
//    recordRow.patronymic = clientRecord.patronymic;
//    recordRow.accBalance
//  }
}
