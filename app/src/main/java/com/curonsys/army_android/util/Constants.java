package com.curonsys.army_android.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME = "com.curonsys.army_android";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RESULT_COUNTRY_KEY = PACKAGE_NAME + ".RESULT_COUNTRY_KEY";
    public static final String RESULT_LOCALITY_KEY = PACKAGE_NAME + ".RESULT_LOCALITY_KEY";
    public static final String RESULT_THOROUGHFARE_KEY = PACKAGE_NAME + ".RESULT_THOROUGHFARE_KEY";
    public static final String RESULT_ADDRESS_KEY = PACKAGE_NAME + ".RESULT_ADDRESS_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final String STORAGE_BASE_URL = "gs://my-first-project-7e28c.appspot.com";

    public static final String KUDAN_API_KEY_DEV = "JvTam0rZCqbJkMEJZuB4KevXbV0/CquncYpJU0FKx5PA+3miHVsmmBeUEepbEpH+RzFK4VPDJ4DzYOXixEO3ZGDiZkVR/AH2XegPgwIqMJlsSAtAvVErFXsQaOEYlq8SF4kvkEQgrlgXrJY/t6cDuxdBp5ecgf68eI+1KU8ObwjK8YQbXv+6s/3GSL6lVyVo62o2Sv2QkUfZEpjrl5hI1rzjJ70aNfpy0ddZgJSMNUF5gbsk+dtoFETdvmCDhlC58w2E23r8h+XpEvMZslkMFKlY/5zq7x4YSkYcEbAw4KTDsOj63dbO2lld49TOG2Bo3JHoIRgf5kFa03xj0JrQBsxG/gHhNQwYJGqMAhVSNvZEIQKRsS9UTmXOzsjysU8zpPoRuAbhXQAyUnkc7jdIGO49cSoVjR+QGx8bmpLlpxphNu90b1up75IJvrY/fX/EF7LgTfk5tXMRXhpdX90dAdtFiwSZYlcmY6bc/uxC9IqbwGeEQuw7508fte/7h3wBrvoRqS5948giz6VfR6Hxz7lPcwG4sYVPRKc4QsQm9DK8Ac5+QJWUwRUjClfJ0y59kDPoB/M4t2kOBlaJAHeSgijQor1IgXFGEIlLxRbf5qgWfjdGie2yeb84CrcGvCzt6IwltqrD4WbcI+HAJAJogY82hKhMEgJaulqpSIvlKQY=";

    public static final long GEOFENCE_EXPIRATION_IN_HOUR = 1;
    public static final float GEOFENCE_USER_RADIUS_IN_METERS = 100;
    public static final float GEOFENCE_SERVICE_RADIUS_IN_METERS = 200;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 60 * 60 * 1000 * GEOFENCE_EXPIRATION_IN_HOUR;

    public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<>();

    static {
        // SCNU_MUSEUM
        BAY_AREA_LANDMARKS.put("SCNU_MUSEUM", new LatLng(34.9691898, 127.48182419));

        // Curonsys
        BAY_AREA_LANDMARKS.put("CURONSYS", new LatLng(34.9471727, 127.6876304));

        // Home_APT
        //BAY_AREA_LANDMARKS.put("HOME_APT", new LatLng(34.951302, 127.689964));

        // San Francisco International Airport.
        //BAY_AREA_LANDMARKS.put("SFO", new LatLng(37.621313, -122.378955));

        // Googleplex.
        //BAY_AREA_LANDMARKS.put("GOOGLE", new LatLng(37.422611,-122.0840577));
    }
}

