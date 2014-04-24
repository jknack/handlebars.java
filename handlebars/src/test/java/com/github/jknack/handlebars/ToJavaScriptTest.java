package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

//TODO: Re-enabled me later
@Ignore
public class ToJavaScriptTest extends AbstractTest {

  /**
   * TravisCI fails with threads while running tests.
   */
  @Before
  public void setup() {
    boolean travisCI = Boolean.valueOf(System.getenv("TRAVIS"));
    Assume.assumeTrue(!travisCI);
  }

  public static void assertConcurrent(final String message,
      final Runnable test, final int numThreads, final int maxTimeoutSeconds)
      throws InterruptedException {
    final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
    final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
    try {
      final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
      final CountDownLatch afterInitBlocker = new CountDownLatch(1);
      final CountDownLatch allDone = new CountDownLatch(numThreads);
      for (int i = 0; i < numThreads; i++) {
        threadPool.submit(new Runnable() {
          @Override
          public void run() {
            allExecutorThreadsReady.countDown();
            try {
              afterInitBlocker.await();
              test.run();
            } catch (final Throwable e) {
              exceptions.add(e);
            } finally {
              allDone.countDown();
            }
          }
        });
      }
      // wait until all threads are ready
      assertTrue(
          "Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent",
          allExecutorThreadsReady.await(numThreads * 10, TimeUnit.MILLISECONDS));
      // start all test runners
      afterInitBlocker.countDown();
      assertTrue(message + " timeout! More than" + maxTimeoutSeconds + "seconds",
          allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
    } finally {
      threadPool.shutdownNow();
    }
    assertTrue(message + "failed with exception(s)" + exceptions, exceptions.isEmpty());
  }

  @Test
  public void toSingleJavaScript() throws InterruptedException {
    assertConcurrent("toJavaScript", new Runnable() {
      int execution = 0;

      @Override
      public void run() {
        try {
          Template template = compile("<ul>{{#list}}<li>{{name}}</li>{{/list}}</ul>");
          long start = System.currentTimeMillis();
          String js = template.toJavaScript();
          long end = System.currentTimeMillis();
          assertEquals(1258, js.length());
          System.out.printf("Single execution: %s took: %sms\n", execution++, end - start);
        } catch (IOException e) {
          throw new IllegalArgumentException(e);
        }
      }
    }, 10, 1000);
  }

  @Test
  public void toSharedJavaScript() throws InterruptedException, IOException {
    final Template template = compile("<ul>{{#list}}<li>{{name}}</li>{{/list}}</ul>");
    assertConcurrent("toJavaScript", new Runnable() {
      int execution = 0;

      @Override
      public void run() {
        long start = System.currentTimeMillis();
        String js = template.toJavaScript();
        long end = System.currentTimeMillis();
        assertEquals(1258, js.length());
        System.out.printf("Shared execution: %s took: %sms\n", execution++, end - start);
      }
    }, 10, 2000);
  }
}
