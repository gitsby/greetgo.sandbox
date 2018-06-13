export class ClientRecord {

  public id: number;
  public name: string;
  public surname: string;
  public patronymic: string;
  public character: string = "";

  public age: number = 0;
  public accBalance: number = 10;
  public maxBalance: number = 0;
  public minBalance: number = 0;
}