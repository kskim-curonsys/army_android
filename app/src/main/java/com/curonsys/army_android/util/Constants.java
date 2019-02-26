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

    //public static final String KUDAN_API_KEY_DEV = "JvTam0rZCqbJkMEJZuB4KevXbV0/CquncYpJU0FKx5PA+3miHVsmmBeUEepbEpH+RzFK4VPDJ4DzYOXixEO3ZGDiZkVR/AH2XegPgwIqMJlsSAtAvVErFXsQaOEYlq8SF4kvkEQgrlgXrJY/t6cDuxdBp5ecgf68eI+1KU8ObwjK8YQbXv+6s/3GSL6lVyVo62o2Sv2QkUfZEpjrl5hI1rzjJ70aNfpy0ddZgJSMNUF5gbsk+dtoFETdvmCDhlC58w2E23r8h+XpEvMZslkMFKlY/5zq7x4YSkYcEbAw4KTDsOj63dbO2lld49TOG2Bo3JHoIRgf5kFa03xj0JrQBsxG/gHhNQwYJGqMAhVSNvZEIQKRsS9UTmXOzsjysU8zpPoRuAbhXQAyUnkc7jdIGO49cSoVjR+QGx8bmpLlpxphNu90b1up75IJvrY/fX/EF7LgTfk5tXMRXhpdX90dAdtFiwSZYlcmY6bc/uxC9IqbwGeEQuw7508fte/7h3wBrvoRqS5948giz6VfR6Hxz7lPcwG4sYVPRKc4QsQm9DK8Ac5+QJWUwRUjClfJ0y59kDPoB/M4t2kOBlaJAHeSgijQor1IgXFGEIlLxRbf5qgWfjdGie2yeb84CrcGvCzt6IwltqrD4WbcI+HAJAJogY82hKhMEgJaulqpSIvlKQY=";
    //public static final String KUDAN_API_KEY_DEV = "WZrB9tFmFBTfC6xstL99HTUMLV77Oz01Afqn2CFHBXrz9deORP3F7RAdam5M9uiaSa9A4kqKu6kBZJZy61gg/85cl8jOB5tg3RMZMhtF6Z+DsijLR55zlnhbHMdJqqAHSDGvn6ApJgwqIxXmcTe5MaOmRTL8L5E60UfrnqHKTSOveSMSRaelxBs8R+nTQLOjbCfjR9UKR0YYlq5SmwotTJIcCxcb8BWZsMcMUwYxImVE9z68TU2ZaRM6sKCngiSihsI5dcaTxZdycXOCCbtv6J20dhhaEOfou1/Uuhagtxki5Tui0WJIDVZbwGgRKLpMxyyl1YyLfl9/jh/xojqFMdsBcyg4seH5wM0daYlkskfPQHW49uI77SbU14TPOj/UjdJ80veamvT8O1tLNFQVgFHG93roYqkJbAnTuXFekk+SgSlZfVzwMJJAVi+Sq2b2NriteVtz11ltL5RfcCWiOCi9e1DXdIUp2noryyuPfuopdxCYj7BPG13sZ3Yrvnboi09Pc/jkIsWIxSDvfuMWntj8rTcNgQiVks2mEL5/m5aasqScGVqgkzs91GYPDlcUIlOeNHVvxo8lhGxlWRylf6+4mX20toDoKDCtiHilEG8agVgj+C6uknfBoxcHb9wmI0QAY888O/+pNI0Or3CyyKg4cNtGMmvygRZyXvJJi/A=";
    public static final String KUDAN_API_KEY_DEV = "vc2v1sE3X4YckxZZ+6EeuWYZ6X5YY0Io93jF5JXvRIilyvw766SvFpdMWok/T81Nuwy6uW9cFv8QoSjX+KI9TM1qWf3p7o6ezeaSIEqzY8lqe4Dz0y8SUNlR5GkPUvYzkE/b3AJ1t2ms7mE8wm0LNwIES3uOe5HPgHfi9n3Q0IitUq8VC44m4t2zqE0hlqUSwlOz9v0pWmY7D1XIjgd3l3uIw66ajbtfqNf3O2NwApgJfRL4FwcZ9kvtESCoRoQvJfcnPA6MJuYY3lAtFwfBw/VwBn/zVaDL8LSk9Cz6BHWGZK1RI2KFFrlzML/cIMp2wyhN7sFyenv4RK22p7JY76nj034BKqfpEpcocu8ChrREQmL6+p+ZpBFhqpYYWHWnVc1bRetip03JYUvtAF9UFyl0xvl2YB3mO/GhP31WiSb3XO6kLZY2Y+lTPdOjPTHQSKyRkqiEIrkYCxEu0JPfzr1waraGT7o44koJ93A52th2Z5FrrMSkEAgbCuBgIcI6H8WlkkeuGumaVMBT8BLPAbvmzzb7vad8xGmiP5ZcNWpj8sepF+K2ORlxJ/gemhA3CMbHtlIru3iNmKMuOH8qc4PPcOqCulzzfzhB0ad77jBVhzgyHQvDEiLd7UIMKuQOD4KRnx8nj53ThqEfCvAjCGqYUe0WlDGSsI90wp4RGuM=";      // 2020-02-21

    public static final String DEFAULT_CONTENTS_ID1 = "2dw2AS9055o5SqaQEzec";
    public static final String DEFAULT_CONTENTS_ID2 = "P5fYGrTyHKnF1ZjeKnG7";
    public static final String DEFAULT_CONTENTS_ID3 = "ZUZKrsGuGA9DdOwVBiWA";
    public static final String DEFAULT_CONTENTS_ID4 = "dg74hArOtk417Q4CbQQR";

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

        // San Francisco International Airport.
        //BAY_AREA_LANDMARKS.put("SFO", new LatLng(37.621313, -122.378955));

        // Googleplex.
        //BAY_AREA_LANDMARKS.put("GOOGLE", new LatLng(37.422611,-122.0840577));
    }
}

