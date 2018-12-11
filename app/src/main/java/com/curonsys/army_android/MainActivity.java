package com.curonsys.army_android;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.curonsys.army_android.activity.LoginActivity;
import com.curonsys.army_android.activity.TrivialDriveActivity;
import com.curonsys.army_android.arcore.AugmentedImageActivity;
import com.curonsys.army_android.model.TransferModel;
import com.curonsys.army_android.model.UserModel;
import com.curonsys.army_android.util.PermissionManager;
import com.curonsys.army_android.util.RequestManager;
import com.curonsys.army_android.util.SharedDataManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView mProfileImage;
    private TextView mProfileName;
    private TextView mProfileEmail;
    private TextView mMainMessage;

    private FirebaseAuth mAuth;
    private RequestManager mRequestManager;
    private SharedDataManager mSharedDataManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    protected boolean mUserProfileStatus = false;
    protected Location mLastLocation = null;

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

        mMainMessage = (TextView) findViewById(R.id.main_message);

        getLastLocation();
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

        } else if (id == R.id.nav_instore) {
            //doVibration(2000);
            doSignOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            //updateUI();
        } else {
            Snackbar.make(mProfileImage, getString(R.string.logout_failed), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private void updateUI() {
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

                String imagename = "profile";
                String imageurl = response.getImageUrl();
                String imagesuffix = imageurl.substring(imageurl.indexOf('.'), imageurl.length());
                String username = response.getName();
                mProfileName.setText(username);

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

                                String output = "last lat : " + latitude + "\n" + "last lon : " + longitude + "\n" + "speed : " + speed + "m/s" + "\n\n";
                                mMainMessage.setText(output);
                            }
                        }
                    });

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
