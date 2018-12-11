package com.curonsys.army_android.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.curonsys.army_android.R;
import com.curonsys.army_android.model.UserModel;
import com.curonsys.army_android.util.RequestManager;
import com.curonsys.army_android.util.SharedDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;


public class SignupActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_READ_CONTACTS = 0;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordView2;
    private EditText mNameView;
    private View mProgressView;
    private View mSingUpFormView;
    private TextView mTitleView;

    private FirebaseAuth mAuth;
    private RequestManager mRequestManager;
    private SharedDataManager mSharedDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        setupActionBar();

        mTitleView = findViewById(R.id.signup_title);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.signup_email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.signup_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    mPasswordView2.setCursorVisible(true);
                    return true;
                }
                return false;
            }
        });

        mPasswordView2 = (EditText) findViewById(R.id.signup_password2);
        mPasswordView2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    doSignUp();
                    return true;
                }
                return false;
            }
        });

        mNameView = (EditText) findViewById(R.id.signup_name);
        mNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    doSignUp();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_up);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                doSignUp();
            }
        });

        mSingUpFormView = findViewById(R.id.signup_form);
        mProgressView = findViewById(R.id.signup_progress);

        mAuth = FirebaseAuth.getInstance();
        mRequestManager = RequestManager.getInstance();
        mSharedDataManager = SharedDataManager.getInstance();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        View focusView = null;

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String password2 = mPasswordView2.getText().toString();

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            valid = false;
        }
        if (!TextUtils.equals(password, password2)) {
            mPasswordView2.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView2;
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            valid = false;

        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            valid = false;
        }

        if (!valid) {
            focusView.requestFocus();
        }

        return valid;
    }

    private void doSignUp() {
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordView2.setError(null);

        if (!validateForm()) {
            return;
        }

        showProgress(true);
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signUpWithEmail:success");
                            //Toast.makeText(SignupActivity.this, getString(R.string.authentication_success), Toast.LENGTH_SHORT).show();
                            sendEmailVerification();
                        } else {
                            Log.w(TAG, "signUpWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, getString(R.string.create_user_failed), Toast.LENGTH_SHORT).show();
                        }
                        showProgress(false);
                    }
                });
        //Snackbar.make(mEmailView, "SignUp Try", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, getString(R.string.verification_email_sent) + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            setUserInfo();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(SignupActivity.this, getString(R.string.verification_email_sent_failed), Toast.LENGTH_SHORT).show();
                            // TODO: handle exception (re-send)
                        }
                    }
                });
    }

    private void setUserInfo() {
        // set user info. to database
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userid = currentUser.getUid();
        String email = currentUser.getEmail();
        String name = mNameView.getText().toString();
        if (name == null || name.isEmpty()) {
            name = email.substring(0, email.indexOf('@'));
        }

        Map<String, Object> values = new HashMap<>();
        values.put("user_id", userid);
        values.put("email", email);
        values.put("name", name);
        values.put("notification", true);
        ArrayList<GeoPoint> locations = new ArrayList<GeoPoint>();
        GeoPoint currentLocation = new GeoPoint(mSharedDataManager.currentLatitude, mSharedDataManager.currentLongtitude);
        locations.add(currentLocation);
        values.put("locations", locations);

        UserModel model = new UserModel(values);
        mRequestManager.requestSetUserInfo(model, new RequestManager.SuccessCallback() {
            @Override
            public void onResponse(boolean success) {
                if (success) {
                    // finish activity
                    goFinish();
                } else {
                    // delete auth user ?
                }
            }
        });
    }

    private void goFinish() {
        finish();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSingUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSingUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSingUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSingUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SignupActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}

