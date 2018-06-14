import {AddressTypeEnum} from "./AddressTypeEnum";

export class ClientAddress {
  public client: number;
  public type: AddressTypeEnum;
  public street: string;
  public house: string;
  public flat: string;
}
