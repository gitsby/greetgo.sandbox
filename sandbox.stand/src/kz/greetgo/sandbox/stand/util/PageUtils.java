package kz.greetgo.sandbox.stand.util;

import java.util.ArrayList;
import java.util.List;

public class PageUtils {
  public static <T> boolean cutPage(List<T> list, int offset, int pageSize) {

    int sizeAtTheBeginning = list.size();

    List<T> newList = new ArrayList<T>();
    for (int i = 0, c = list.size(); i < c && newList.size() < pageSize; i++) {
      if (i >= offset) newList.add(list.get(i));
    }

    list.clear();
    list.addAll(newList);


    return offset + pageSize < sizeAtTheBeginning;
  }
}
