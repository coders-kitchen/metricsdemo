package com.example;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;
import io.prometheus.client.SimpleCollector;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrometheusAccurateMeter extends SimpleCollector<PrometheusAccurateMeter.Child> implements Collector.Describable {


  private final int ratingInterval;

  protected PrometheusAccurateMeter(Builder b) {
    super(b);
    this.ratingInterval = b.ratingInterval;
    noLabelsChild = newChild();
  }

  @Override
  public List<MetricFamilySamples> describe() {
    List<MetricFamilySamples> mfsList = new ArrayList<MetricFamilySamples>();
    mfsList.add(new GaugeMetricFamily(fullname, help, labelNames));
    return mfsList;
  }

  public static class Builder extends SimpleCollector.Builder<Builder, PrometheusAccurateMeter> {
    private int ratingInterval = 60;


    public Builder ratingInterval(int ratingInterval) {
      this.ratingInterval = ratingInterval;
      return this;
    }


    @Override
    public PrometheusAccurateMeter create() {
      return new PrometheusAccurateMeter(this);
    }
  }

  @Override
  protected Child newChild() {
    return new Child(ratingInterval);
  }

  public void mark() {
    noLabelsChild.mark();
  }

  /**
   *  Return a Builder to allow configuration of a new Gauge.
   */
  public static PrometheusAccurateMeter.Builder build() {
    return new PrometheusAccurateMeter.Builder();
  }

  @Override
  public List<MetricFamilySamples> collect() {
    List<MetricFamilySamples.Sample> samples = new ArrayList<MetricFamilySamples.Sample>();
    for(Map.Entry<List<String>, Child> c: children.entrySet()) {
      samples.add(new MetricFamilySamples.Sample(fullname, labelNames, c.getKey(), c.getValue().getRate()));
    }
    MetricFamilySamples mfs = new MetricFamilySamples(fullname, Type.GAUGE, help, samples);

    List<MetricFamilySamples> mfsList = new ArrayList<MetricFamilySamples>();
    mfsList.add(mfs);
    return mfsList;
  }

  public static class Child {
    private static final int DEFAULT_RATING_INTERVAL = 60;
    private final AccurateMeter accurateMeter = new AccurateMeter();

    private int ratingInterval = DEFAULT_RATING_INTERVAL;

    public Child(int ratingInterval) {
      if(ratingInterval > 0) {
        this.ratingInterval = ratingInterval;
      }
      System.out.println(this.ratingInterval);
    }

    public void mark() {
      accurateMeter.mark();
    }

    public long getRate() {
      return accurateMeter.rateOverInterval(ratingInterval);
    }
  }
}
