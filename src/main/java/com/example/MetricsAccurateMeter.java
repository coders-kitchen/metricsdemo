package com.example;


import com.codahale.metrics.Clock;
import com.codahale.metrics.Meter;


public class MetricsAccurateMeter extends Meter {

  private final AccurateMeter wrappedMeter;

  public MetricsAccurateMeter(Clock clock) {
    this(() -> clock.getTick());
  }

  public MetricsAccurateMeter(MetricsClock clock) {
    wrappedMeter = new AccurateMeter(clock);
  }

  public MetricsAccurateMeter() {
    this(Clock.defaultClock());
  }

  public void mark() {
    wrappedMeter.mark();
  }

  @Override
  public long getCount() {
    return wrappedMeter.getCount();
  }

  @Override
  public double getFifteenMinuteRate() {
    return wrappedMeter.rateOverInterval(900);
  }

  @Override
  public double getFiveMinuteRate() {
    return wrappedMeter.rateOverInterval(600);
  }

  @Override
  public double getMeanRate() {
    return wrappedMeter.getMeanRate();
  }

  @Override
  public double getOneMinuteRate() {
    return wrappedMeter.rateOverInterval(60);
  }
}
