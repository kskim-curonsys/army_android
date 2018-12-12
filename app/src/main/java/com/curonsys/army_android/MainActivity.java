package com.curonsys.army_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.curonsys.army_android.activity.LoginActivity;
import com.curonsys.army_android.activity.SignupActivity;
import com.curonsys.army_android.activity.TrivialDriveActivity;
import com.curonsys.army_android.arcore.AugmentedImageActivity;
import com.curonsys.army_android.model.TransferModel;
import com.curonsys.army_android.model.UserModel;
import com.curonsys.army_android.service.FetchAddressIntentService;
import com.curonsys.army_android.util.Constants;
import com.curonsys.army_android.util.PermissionManager;
import com.curonsys.army_android.util.RequestManager;
import com.curonsys.army_android.util.SharedDataManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.curonsys.army_android.util.DistanceUtil.distanceFrom;
import static com.curonsys.army_android.util.DistanceUtil.latitudeInDifference;
import static com.curonsys.army_android.util.DistanceUtil.longitudeInDifference;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MY_PERMISSION_STORAGE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_TAKE_ALBUM = 3;
    private static final int REQUEST_IMAGE_CROP = 4;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 5;
    private static final int REQUEST_PLACE_PICKER = 6;

    private ImageView mProfileImage;
    private TextView mProfileName;
    private TextView mProfileEmail;
    private TextView mCurrentLocation;
    private TextView mCurrentAddress;
    private Switch mLocationSwitch;

    private FirebaseAuth mAuth;
    private RequestManager mRequestManager;
    private SharedDataManager mSharedDataManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationManager mLocationManager;

    protected boolean mUserProfileStatus = false;
    protected Location mLastLocation = null;
    protected String mFindPlace = "";
    protected boolean mLocationUpdateState = false;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ARAPIKey key = ARAPIKey.getInstance();
        //key.setAPIKey(KUDAN_API_KEY_DEV);

        PermissionManager permissionManager = new PermissionManager(this);
        permissionManager.permissionCheck();

        mAuth = FirebaseAuth.getInstance();
        mRequestManager = RequestManager.getInstance();
        mSharedDataManager = SharedDataManager.getInstance();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.not_yet_implemented, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                updateUI();
            }
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                updateUI();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mProfileImage = (ImageView) header.findViewById(R.id.nav_profile_image);
        mProfileImage.setOnClickListener(new ProfileImageClick());
        mProfileName = (TextView) header.findViewById(R.id.nav_profile_name);
        mProfileEmail = (TextView) header.findViewById(R.id.nav_profile_email);

        mCurrentLocation = (TextView) findViewById(R.id.current_location);
        mCurrentAddress = (TextView) findViewById(R.id.current_address);

        mLocationSwitch = (Switch) findViewById(R.id.switch_location);
        mLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startLocationUpdate();
                } else {
                    stopLocationUpdate();
                }
            }
        });

        updateUI();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_armaker) {

        } else if (id == R.id.nav_arcontents) {
            // test
            goBillingTest();

        } else if (id == R.id.nav_arviewer) {
            goARViewer();
        } else if (id == R.id.nav_arservice) {

        } else if (id == R.id.nav_location) {
            //goSignUp();
            goFindPlace();

        } else if (id == R.id.nav_instore) {
            //doVibration(2000);
            doSignOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_PERMISSION_STORAGE) {
        } else if (requestCode == REQUEST_TAKE_PHOTO) {
        } else if (requestCode == REQUEST_TAKE_ALBUM) {
        } else if (requestCode == REQUEST_IMAGE_CROP) {
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
        } else if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                final Place place = PlacePicker.getPlace(data, this);
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                String attributions = PlacePicker.getAttributions(data);
                if (attributions == null) {
                    attributions = "";
                }

                String info = "Find Place: " + "\n" + place.getName() + "\n" + place.getAddress() +
                        "\n" + place.getLatLng() + "\n" + place.getViewport();

                mFindPlace = info;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private boolean checkLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            return false;
        }
        return true;
    }

    private void profileImageClicked(View v) {
        if (checkLogin()) {
            //goAccount();
            closeDrawer();
        } else {
            goLogin();
            closeDrawer();
        }
    }

    class ProfileImageClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            profileImageClicked(v);
        }
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra("ParentClassSource", MainActivity.class.getName());
            startActivity(intent);
        }
    }

    private void goARViewer() {
        Intent intent = new Intent(this, AugmentedImageActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra("ParentClassSource", MainActivity.class.getName());
            startActivity(intent);
        }
    }

    // test
    private void goBillingTest() {
        Intent intent = new Intent(this, TrivialDriveActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra("ParentClassSource", MainActivity.class.getName());
            startActivity(intent);
        }
    }

    private void doVibration(long milliseconds) {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(milliseconds);
        }
    }

    private void doSignOut() {
        if (checkLogin()) {
            mAuth.signOut();
            Snackbar.make(mProfileImage, getString(R.string.logout_success), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            updateUI();
        } else {
            Snackbar.make(mProfileImage, getString(R.string.not_yet_login), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private void goFindPlace() {
        // findPlaceByPicker
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }

    // test
    private void goSignUp() {
        Intent intent = new Intent(this, SignupActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra("ParentClassSource", LoginActivity.class.getName());
            startActivity(intent);
        }
    }

    private void updateUI() {
        getLastLocation();
        if (checkLogin()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userid = currentUser.getUid();
            if (mUserProfileStatus == false) {
                mProfileName.setText(userid);
                mProfileEmail.setText(currentUser.getEmail());
                getUserProfileInfo(userid);
            }
        } else {
            mProfileImage.setImageResource(R.mipmap.ic_launcher_round);
            mProfileName.setText("Android Studio");
            mProfileEmail.setText("android.studio@android.com");
            mUserProfileStatus = false;
        }
    }

    private void getUserProfileInfo(String userid) {
        mRequestManager.requestGetUserInfo(userid, new RequestManager.UserCallback() {
            @Override
            public void onResponse(UserModel response) {
                Log.d(TAG, "onResponse: UserInfo (" +
                        response.getUserId() + ", " + response.getName() + ", " + response.getImageUrl() + ")");

                String username = response.getName();
                mProfileName.setText(username);

                String imagename = "profile";
                String imageurl = response.getImageUrl();
                if (imageurl != null && !imageurl.isEmpty()) {
                    // profile image
                    String imagesuffix = imageurl.substring(imageurl.indexOf('.'), imageurl.length());
                    mRequestManager.requestDownloadFileFromStorage(imagename, imageurl, imagesuffix, new RequestManager.TransferCallback() {
                        @Override
                        public void onResponse(TransferModel download) {
                            String imgurl = download.getPath();
                            File imgFile = new File(imgurl);
                            if (imgFile.exists()) {
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                if (myBitmap != null) {
                                    mProfileImage.setImageBitmap(myBitmap);
                                    mUserProfileStatus = true;
                                }
                            }
                        }
                    });
                } else {
                    // default image
                    try {
                        AssetManager am = getResources().getAssets();
                        InputStream is = null;
                        is = am.open("lake.png");
                        if (is != null) {
                            Bitmap bm = BitmapFactory.decodeStream(is);
                            mProfileImage.setImageBitmap(bm);
                            is.close();
                            mUserProfileStatus = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getLastLocation() {
        try {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;

                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                float speed = location.getSpeed();
                                mSharedDataManager.currentLatitude = latitude;
                                mSharedDataManager.currentLongtitude = longitude;

                                String output = getString(R.string.latitude) + " : " + latitude + "\n" + getString(R.string.longitude) + " : " + longitude + "\n" + getString(R.string.speed) + " : " + speed + " m/s" + "\n\n";
                                mCurrentLocation.setText(output);

                                startFetchAddressIntentService();
                            }
                        }
                    });

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }
            String result = resultData.getString(Constants.RESULT_DATA_KEY);

            mCurrentAddress.setText(result);
        }
    }

    protected void startFetchAddressIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String output = getString(R.string.latitude) + " : " + location.getLatitude() + "\n" + getString(R.string.longitude) + " : " + location.getLongitude() + "\n" + getString(R.string.speed) + " : " + location.getSpeed() + " m/s" + "\n\n";
            mCurrentLocation.setText(output);
            mLastLocation = location;

            startFetchAddressIntentService();
        }

        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }

        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Log.d(TAG, "onStatusChanged: " + provider + ", status: " + status + " ,Bundle: " + extras);
        }
    };

    private void startLocationUpdate() {
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    100, 1, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    100, 1, mLocationListener);
            mLocationUpdateState = true;

        } catch (SecurityException e) {
            mLocationUpdateState = false;
            e.printStackTrace();
        }
    }

    private void stopLocationUpdate() {
        if (mLocationUpdateState) {
            mLocationManager.removeUpdates(mLocationListener);
            mLocationUpdateState = false;
        }
    }
}
