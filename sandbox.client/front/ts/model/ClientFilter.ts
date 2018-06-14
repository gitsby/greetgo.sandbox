import {SortDirection} from "./SortDirection";
import {SortByEnum} from "./SortByEnum";

export class ClientFilter {
  public from: number;
  public to: number;
  public sortByEnum: SortByEnum | null;
  public sortDirection: SortDirection | null;
  public fio: string | null;
}