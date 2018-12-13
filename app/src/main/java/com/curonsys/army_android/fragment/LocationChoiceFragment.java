package com.curonsys.army_android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.curonsys.army_android.R;
import com.curonsys.army_android.util.SharedDataManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by ijin-yeong on 2018. 5. 21..
 */

public class LocationChoiceFragment extends Fragment implements OnMapReadyCallback {
    Context thisContext;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap mMap;
    LocationManager lm;
    SharedDataManager dbManager = SharedDataManager.getInstance();

    TextView tv;
    MaterialDialog.Builder builder = null;
    MaterialDialog materialDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marker_generation2, container, false);
        FragmentManager fragmentManager = this.getChildFragmentManager();

        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lm = (LocationManager) thisContext.getSystemService(Context.LOCATION_SERVICE);
        tv = view.findViewById(R.id.locationTestTv);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(thisContext);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
        }catch (SecurityException e){
            e.printStackTrace();
        }

        builder = new MaterialDialog.Builder(thisContext)
                .title("위치 수신중")
                .content("현재 위치를 확인중입니다...")
                .progress(true,0);
        materialDialog = builder.build();
        materialDialog.show();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        thisContext=activity;

    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
    //        double altitude = location.getAltitude();   //고도//          float accuracy = location.getAccuracy();    //정확도//            String provider = location.getProvider();   //위치제공자
            materialDialog.dismiss();

            LatLng currentLocation = new LatLng(latitude,longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title("현재 위치");
            markerOptions.snippet("ARZone");
            mMap.addMarker(markerOptions);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
            lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
            dbManager.currentLongtitude = longitude;
            dbManager.currentLatitude = latitude;
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };
    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
    }
}
