package com.example;

public class DefaultMetricsClock implements MetricsClock {

  private static final DefaultMetricsClock DEFAULT = new DefaultMetricsClock();

  public static final DefaultMetricsClock defaultClock() {
    return DEFAULT;
  }

  @Override
  public long getTick() {
    return System.nanoTime();
  }

}
