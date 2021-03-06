package com.wisdompark.minichoucreme.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.engin.MiniChouContext;
import com.wisdompark.minichoucreme.storage.UserInfo;
import com.wisdompark.minichoucreme.ui.MainActivity;
import com.wisdompark.minichoucreme.utils.Constraints;

import java.io.InputStream;
import java.net.URL;

public class EmailPasswordActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "EmailPasswordActivity";
    private EditText mEdtEmail, mEdtPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageView mImageView;
    private TextView mTextViewProfile;
    private TextInputLayout mLayoutEmail, mLayoutPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        mTextViewProfile = findViewById(R.id.profile);
        mEdtEmail = findViewById(R.id.edt_email);
        mEdtPassword = findViewById(R.id.edt_password);
        mImageView = findViewById(R.id.logo);
        mLayoutEmail = findViewById(R.id.layout_email);
        mLayoutPassword = findViewById(R.id.layout_password);

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                setPreferences();
                updateUI(user);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_create_account_button:
                createAccount(mEdtEmail.getText().toString(), mEdtPassword.getText().toString());
                break;
            case R.id.email_sign_in_button:
                signIn(mEdtEmail.getText().toString(), mEdtPassword.getText().toString());
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.verify_button:
                findViewById(R.id.verify_button).setEnabled(false);
                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                firebaseUser.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    EmailPasswordActivity.this, "Verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_LONG
                            ).show();
                        } else {
                            Toast.makeText(EmailPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        findViewById(R.id.verify_button).setEnabled(true);
                    }
                });
                break;
        }
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    mTextViewProfile.setTextColor(Color.RED);
                    mTextViewProfile.setText(task.getException().getMessage());
                } else {
                    mTextViewProfile.setTextColor(Color.DKGRAY);
                }
                hideProgressDialog();
            }
        });
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    mTextViewProfile.setTextColor(Color.RED);
                    mTextViewProfile.setText(task.getException().getMessage());
                } else {
                    mTextViewProfile.setTextColor(Color.DKGRAY);
                }
                hideProgressDialog();
            }
        });
    }

    private void signOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.logout);
        alert.setCancelable(false);
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAuth.signOut();
                updateUI(null);
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(mEdtEmail.getText().toString())) {
            mLayoutEmail.setError("Required.");
            return false;
        } else if (TextUtils.isEmpty(mEdtPassword.getText().toString())) {
            mLayoutPassword.setError("Required.");
            return false;
        } else {
            mLayoutEmail.setError(null);
            mLayoutPassword.setError(null);
            return true;
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            String userId = user.getEmail().substring(0, user.getEmail().indexOf('@'));
            boolean isFound = false;
            for(UserInfo aInfo : MiniChouContext.getmUserInfoList()){
                if(aInfo.equals(userId)){
                    isFound = true;
                    break;
                }
            }

            if(isFound == false && userId.equals(MiniChouContext.getWatching_email()) == false){
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                String emailAddress = MiniChouContext.getWatching_email();

                UserInfo userData = new UserInfo();
                userData.setuId(userId);
                userData.setuToken(FirebaseInstanceId.getInstance().getToken());

                databaseReference
                        .child(emailAddress)
                        .child(Constraints.USER_NAME)
                        .child(userId)
                        .setValue(userData);
            }

            if(disp_type == 0) { //초기 진입시
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }else{
                if (user.getPhotoUrl() != null) {
                    new DownloadImageTask().execute(user.getPhotoUrl().toString());
                }
                mTextViewProfile.setText("DisplayName: " + user.getDisplayName());
                mTextViewProfile.append("\n\n");
                mTextViewProfile.append("Email: " + user.getEmail());
                mTextViewProfile.append("\n\n");
                mTextViewProfile.append("Firebase ID: " + user.getUid());
                mTextViewProfile.append("\n\n");
                mTextViewProfile.append("Email Verification: " + user.isEmailVerified());

                //if (user.isEmailVerified()) {
                if (true) {
                    findViewById(R.id.verify_button).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.verify_button).setVisibility(View.VISIBLE);
                }

                findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
                findViewById(R.id.email_password_fields).setVisibility(View.GONE);
                findViewById(R.id.signout_zone).setVisibility(View.VISIBLE);
            }

        } else {
            mTextViewProfile.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signout_zone).setVisibility(View.GONE);
        }
        hideProgressDialog();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap mIcon = null;
            try {
                InputStream in = new URL(urls[0]).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                mImageView.getLayoutParams().width = (getResources().getDisplayMetrics().widthPixels / 100) * 24;
                mImageView.setImageBitmap(result);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(disp_type == 0) //초기 진입시
            finish();
    }

    public int disp_type = -1;
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        disp_type = intent.getIntExtra("DISPLAY_TYPE",0);
    }

    private void setPreferences() {
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchSP = sp.getBoolean("sync",false);
        String strEmail = sp.getString("watching_email","");

        MiniChouContext.setIsParentsMode(switchSP);
        MiniChouContext.setWatching_email(strEmail);

        if( user != null )
            MiniChouContext.setMyEmail(user.getEmail());
    }
}