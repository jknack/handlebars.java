package com.github.edgarespina.handlebars;

public class StopWatch {
  private long start;

  private String name;

  public StopWatch(final String name) {
    this.name = name;
    this.start = System.currentTimeMillis();
  }

  public void done(final String result) {
    long end = System.currentTimeMillis();
    System.out
        .printf("%s took: %sms\n  %s\n", name, end - start, result);
  }

}
