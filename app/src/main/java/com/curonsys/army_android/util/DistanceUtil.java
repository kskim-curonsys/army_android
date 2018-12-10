package com.curonsys.army_android.util;

public class DistanceUtil {
    private static final double EARTH_RADIOUS = 3958.75;
    private static final double EARTH_RADIOUS_METER = 6371000;
    private static final int METER_CONVERSION = 1609;

    public static double distanceFrom(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = EARTH_RADIOUS * c;

        return new Double(dist * METER_CONVERSION).doubleValue();
    }

    public static double latitudeInDifference(int diff) {
        return (diff * 360.0) / (2 * Math.PI * EARTH_RADIOUS_METER);
    }

    public static double longitudeInDifference(double lat, int diff) {
        double ddd = Math.cos(0);
        double ddf = Math.cos(Math.toRadians(lat));

        return (diff * 360.0) / (2 * Math.PI * EARTH_RADIOUS_METER * Math.cos(Math.toRadians(lat)));
    }
}
