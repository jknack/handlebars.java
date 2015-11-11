package com.github.jknack.handlebars.bench;

import java.io.IOException;

public class Bench {

  public interface Unit {
    void run() throws IOException;
  }

  private long time;

  private int size;

  private int warmup;

  public Bench(final long time, final int warmup, final int size) {
    this.time = time;
    this.warmup = warmup;
    this.size = size;
  }

  public Bench() {
    this(2000, 1, 5);
  }

  public void run(final Unit unit) throws IOException {
    iteration("Warmup Iteration", time, warmup, unit);
    System.out.printf("AVG: %s per second\n\n", iteration("Iteration", time, size, unit));
  }

  private long iteration(final String label, final long time, final int size, final Unit unit)
      throws IOException {
    System.out.printf("%s\n", unit);
    long avg = 0;
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
        long unitPerSecond = total * 1000 / time;
        avg += unitPerSecond;
        System.out.printf("  %s #%s: %s per second\n", label, b, unitPerSecond);
      }
    }
    return avg / size;
  }
}
