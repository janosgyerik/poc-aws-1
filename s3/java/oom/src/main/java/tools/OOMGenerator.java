package tools;

import java.util.ArrayList;
import java.util.List;

public final class OOMGenerator {
  public static void main(String[] args) {
    List<Object> holder = new ArrayList<>();
    while (true) {
      holder.add(new byte[128 * 1024]);
    }
  }
}
