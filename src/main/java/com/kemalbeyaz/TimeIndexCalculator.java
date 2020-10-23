package com.kemalbeyaz;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author kemal.beyaz
 */
public class TimeIndexCalculator {

    private TimeIndexCalculator() {
    }

    public static long calculateCurrentTimeIndex() {
        return calculateTimeIndexFor(new Date().getTime());
    }

    public static boolean isThisIndexInCurrentIndex(long givenTimeIndex) {
        long currentTimeIndex = calculateCurrentTimeIndex();
        int compare = Long.compare(currentTimeIndex, givenTimeIndex);
        return compare == 0;
    }

    public static boolean isThisTimeInCurrentIndex(long timeInMillis) {
        long currentTimeIndex = calculateCurrentTimeIndex();
        long givenTimeIndex = calculateTimeIndexFor(timeInMillis);

        int compare = Long.compare(currentTimeIndex, givenTimeIndex);
        return compare == 0;
    }

    public static long calculateTimeIndexFor(long timeInMillis) {
        Calendar instance = Calendar.getInstance(TimeZone.getDefault());
        instance.setTimeInMillis(timeInMillis);
        int hour = instance.get(Calendar.HOUR_OF_DAY);
        int gap = hour % 2;
        int indexHour = hour - gap;

        instance.set(Calendar.HOUR_OF_DAY, indexHour);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);

        return instance.getTimeInMillis();
    }
}
