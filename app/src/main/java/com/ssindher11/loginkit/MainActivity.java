package com.ssindher11.loginkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.snapchat.kit.sdk.OAuth2Manager;
import com.snapchat.kit.sdk.SnapKitActivity;
import com.snapchat.kit.sdk.SnapLogin;
import com.snapchat.kit.sdk.core.controller.LoginStateController;
import com.snapchat.kit.sdk.core.networking.AuthTokenManager;
import com.snapchat.kit.sdk.login.models.MeData;
import com.snapchat.kit.sdk.login.models.UserDataResponse;
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout mViewRoot;
    Button loginBtn, logoutBtn, statusBtn;
    TextView statusTV;
    ImageView bitmojiIV;

    private LoginStateController.OnLoginStateChangedListener mLoginStateChangedListener;

    String query = "{me{bitmoji{avatar},displayName}}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();
        setListeners();

        mLoginStateChangedListener = new LoginStateController.OnLoginStateChangedListener() {
            @Override
            public void onLoginSucceeded() {
                Toast.makeText(MainActivity.this, "Login successful :)", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoginFailed() {
                Toast.makeText(MainActivity.this, "Login unsuccessful :(", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLogout() {
                Toast.makeText(MainActivity.this, "Logged out succesfully!", Toast.LENGTH_SHORT).show();
            }
        };

        SnapLogin.getLoginStateController(this).addOnLoginStateChangedListener(mLoginStateChangedListener);
    }


    private void initialiseViews() {
        mViewRoot = findViewById(R.id.cl_root);
        statusTV = findViewById(R.id.tv_status);
        loginBtn = findViewById(R.id.btn_login);
        logoutBtn = findViewById(R.id.btn_logout);
        statusBtn = findViewById(R.id.btn_status);
        bitmojiIV = findViewById(R.id.iv_bitmoji);
    }

    private void setListeners() {
        loginBtn.setOnClickListener(view -> {
            SnapLogin.getAuthTokenManager(this).startTokenGrant();

            checkStatus();
        });

        bitmojiIV.setOnClickListener(view -> checkStatus());

        statusBtn.setOnClickListener(view -> {

            if (SnapLogin.isUserLoggedIn(MainActivity.this)) {
                SnapLogin.fetchUserData(MainActivity.this, query, null, new FetchUserDataCallback() {
                    @Override
                    public void onSuccess(@Nullable UserDataResponse userDataResponse) {
                        if (userDataResponse == null || userDataResponse.getData() == null) {
                            return;
                        }

                        MeData meData = userDataResponse.getData().getMe();
                        if (meData == null) {
                            return;
                        }

                        statusTV.setText(userDataResponse.getData().getMe().getDisplayName());

                        if (meData.getBitmojiData() != null) {
                            Glide.with(MainActivity.this).load(meData.getBitmojiData().getAvatar()).into(bitmojiIV);
                        }

                    }

                    @Override
                    public void onFailure(boolean isNetworkError, int statusCode) {
                        Toast.makeText(MainActivity.this, "Some Failure Occurred!!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Logged out!!", Toast.LENGTH_SHORT).show();
            }
        });

        logoutBtn.setOnClickListener(view -> {
            SnapLogin.getAuthTokenManager(this).revokeToken();
            checkStatus();
        });
    }

    private void checkStatus() {
        boolean isUserLoggedIn = SnapLogin.isUserLoggedIn(this);
//        boolean isUserLoggedIn = SnapLogin.getAuthTokenManager(this).hasAccessToScope(getString(R.string.scope_displayname));

        if (isUserLoggedIn) {
            statusTV.setText("Logged In!");
        } else {
            statusTV.setText("Logged Out!");
        }
    }

}
