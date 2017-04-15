package com.example;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class AccurateMeter {
  private static final int _60_SECONDS = 60;
  private static final int _15_MINUTES = 15;
  private static final int DEFAULT_NUMBER_OF_RING_ELEMENTS = _15_MINUTES * _60_SECONDS;
  private static final int DURATION = 1;
  private static final long SECOND_IN_NANOS = TimeUnit.SECONDS.toNanos(DURATION);

  private final LongAdder[] ringBuffer;
  private final MetricsClock clock;
  private final LongAdder count = new LongAdder();
  private final long startTime ;

  private volatile long lastUpdated = 0;

  public AccurateMeter(MetricsClock clock) {
    this.clock = clock;
    ringBuffer = new LongAdder[DEFAULT_NUMBER_OF_RING_ELEMENTS];
    for (int i = 0; i < ringBuffer.length; i++) {
      ringBuffer[i] = new LongAdder();
    }
    startTime = clock.getTick();
  }

  public AccurateMeter() {
    this(DefaultMetricsClock.defaultClock());
  }

  public void mark() {
    mark(1);
  }

  public void mark(long numberOfObservations) {
    long currentTime = clock.getTick();
    tickIfNecessary(currentTime);
    long l = currentTime / SECOND_IN_NANOS;
    ringBuffer[(int) (l % DEFAULT_NUMBER_OF_RING_ELEMENTS)].add(numberOfObservations);
    count.add(numberOfObservations);
  }

  private void tickIfNecessary(long currentTime) {
    if (lastUpdated == 0) {
      lastUpdated = currentTime;
    }


    long currentSeconds = currentTime / SECOND_IN_NANOS;
    long lastSeconds = lastUpdated / SECOND_IN_NANOS;


    long timeDelta = currentSeconds - lastSeconds;
    if (timeDelta > 0) {
      if (timeDelta > DEFAULT_NUMBER_OF_RING_ELEMENTS) {
        for (int index = 0, longAdderRingLength = ringBuffer.length; index < longAdderRingLength; index++) {
          ringBuffer[index].reset();
        }
      } else {
        long startPosition = (lastSeconds + 1) % DEFAULT_NUMBER_OF_RING_ELEMENTS;
        for (long index = startPosition; index % DEFAULT_NUMBER_OF_RING_ELEMENTS < currentSeconds % DEFAULT_NUMBER_OF_RING_ELEMENTS; index++) {
          ringBuffer[(int) index % DEFAULT_NUMBER_OF_RING_ELEMENTS].reset();
        }
        ringBuffer[(int) currentSeconds % DEFAULT_NUMBER_OF_RING_ELEMENTS].reset();
      }
    }
    lastUpdated = currentTime;
  }

  public long getCount() {
    return count.sum();
  }

  public double getMeanRate() {
    if (getCount() == 0) {
      return 0.0;
    } else {
      final double elapsed = (clock.getTick() - startTime);
      return getCount() / elapsed * SECOND_IN_NANOS;
    }
  }

  public long rateOverInterval(int intervalInSeconds) {
    if (intervalInSeconds > DEFAULT_NUMBER_OF_RING_ELEMENTS) {
      throw new IllegalArgumentException("rate base [" + intervalInSeconds + "] must not exceed number of allocated elements [" + DEFAULT_NUMBER_OF_RING_ELEMENTS + "]");
    }
    long currentTime = clock.getTick();
    tickIfNecessary(currentTime);

    long currentSeconds = currentTime / SECOND_IN_NANOS;
    long currentPosition = currentSeconds % DEFAULT_NUMBER_OF_RING_ELEMENTS;

    long startPos = (currentPosition + DEFAULT_NUMBER_OF_RING_ELEMENTS - intervalInSeconds + 1) % DEFAULT_NUMBER_OF_RING_ELEMENTS;

    long sum = 0;
    for (long index = startPos; index < startPos + intervalInSeconds; index++) {
      sum += ringBuffer[(int) (index % DEFAULT_NUMBER_OF_RING_ELEMENTS)].sum();
    }
    System.out.println();
    return sum;
  }
}
