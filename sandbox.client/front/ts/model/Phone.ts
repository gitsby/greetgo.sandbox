export class Phone {

  public client_id: number;
  public number: string = '';
  public type: string = '';
  public editedTo: string;

  public static createNewPhone(phone: Phone):Phone{
    let newPhone:Phone = new Phone();
    newPhone.client_id = phone.client_id;
    newPhone.number = phone.number;
    newPhone.type = phone.type;
    return newPhone
  }
}
