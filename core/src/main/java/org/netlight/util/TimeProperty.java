package org.netlight.util;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

/**
 * @author ahmad
 */
public final class TimeProperty {

    private final long time;
    private final TimeUnit unit;

    public TimeProperty(long time, TimeUnit unit) {
        this.time = time;
        this.unit = unit;
    }

    public long getTime() {
        return time;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void sleep() {
        try {
            unit.sleep(time);
        } catch (InterruptedException ignored) {
        }
    }

    public long convert(TimeUnit destUnit) {
        return destUnit.convert(time, unit);
    }

    public TimeProperty to(TimeUnit destUnit) {
        return new TimeProperty(destUnit.convert(time, unit), destUnit);
    }

    public static TimeProperty to(TimeProperty property, TimeUnit destUnit) {
        return new TimeProperty(destUnit.convert(property.getTime(), property.getUnit()), destUnit);
    }

    @Override
    public String toString() {
        return "{" +
                "time=" + time +
                ", unit=" + unit +
                '}';
    }

    public static TimeProperty nanos(long time) {
        return new TimeProperty(time, NANOSECONDS);
    }

    public static TimeProperty micros(long time) {
        return new TimeProperty(time, MICROSECONDS);
    }

    public static TimeProperty millis(long time) {
        return new TimeProperty(time, MILLISECONDS);
    }

    public static TimeProperty seconds(long time) {
        return new TimeProperty(time, SECONDS);
    }

    public static TimeProperty minutes(long time) {
        return new TimeProperty(time, MINUTES);
    }

    public static TimeProperty hours(long time) {
        return new TimeProperty(time, HOURS);
    }

    public static TimeProperty days(long time) {
        return new TimeProperty(time, DAYS);
    }

}
