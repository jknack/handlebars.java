package com.github.jknack.handlebars.bench;

import java.io.IOException;


public class Bench {

  public interface Unit {
    void run() throws IOException;
  }

  private long time;

  private int size;

  public Bench(final long time, final int size) {
    this.time = time;
    this.size = size;
  }

  public Bench() {
    this(2000, 5);
  }

  public void run(final Unit unit) throws IOException {
    System.out.printf("'%s' per second\n", unit);
    for (int b = 0; b < size; b++) {
      {
        long start = System.currentTimeMillis();
        int total = 0;
        while (true) {
          unit.run();
          total++;
          if (System.currentTimeMillis() - start > time) {
            break;
          }
        }
        System.out.printf("  (%s): %s\n", b, total * 1000 / time);
      }
    }
  }
}
