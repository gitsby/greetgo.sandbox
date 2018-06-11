import {Address} from "./Address";
import {Phone} from "./Phone";

<<<<<<< HEAD:sandbox.client/front/ts/model/ClientToSave.ts
export class ClientToSave {
    public id: number;
    public name: string;
    public surname: string;
    public patronymic: string;
    public gender: string;
    public birthDate: Date;
=======
//fixme Название не правильное. Нужно исправить названия всех моделек
export class EditClient {
  public id: number;
  public name: string;
  public surname: string;
  public patronymic: string;
  public gender: string;
  public birthDate: Date;
>>>>>>> e756ccac7260f5413d32b35156d4bb452a94938f:sandbox.client/front/ts/model/EditClient.ts

  public charm: number;

  public addedAddresses: Address[] = [];
  public editedAddresses: Address[] = [];
  public deletedAddresses: Address[] = [];

  public addedPhones: Phone[] = [];
  public deletedPhones: Phone[] = [];
  public editedPhones: Phone[] = [];

  public toString = (): string => {
    return JSON.stringify(this);
  }
}