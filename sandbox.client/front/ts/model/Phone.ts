export class Phone {

  public clientid: number;
  public number: string = '';
  public type: string = '';
  public editedTo: string;

  public static createNewPhone(phone: Phone):Phone{
    let newPhone:Phone = new Phone();
    newPhone.clientid = phone.clientid;
    newPhone.number = phone.number;
    newPhone.type = phone.type;
    return newPhone
  }
}
