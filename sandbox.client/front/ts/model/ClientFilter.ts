import {SortDirection} from "./SortDirection";
import {SortByEnum} from "./SortByEnum";

export class ClientFilter {
  public offset: number;
  public limit: number;
  public sortByEnum: SortByEnum | null;
  public sortDirection: SortDirection | null;
  public fio: string | null;
}