import {SortBy} from "./SortBy";
import {SortDirection} from "./SortDirection";

export class ClientFilter {
  public from: number;
  public to: number;
  public sortBy: SortBy | null;
  public sortDirection: SortDirection | null;
  public fio: string | null;
}