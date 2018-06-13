export class Phone {

  public client: number;
  public number: string = '';
  public type: string = '';
  public editedTo: string;

  public static createNewPhone(phone: Phone):Phone{
    let newPhone:Phone = new Phone();
    newPhone.client = phone.client;
    newPhone.number = phone.number;
    newPhone.type = phone.type;
    return newPhone
  }
}
