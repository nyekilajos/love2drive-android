package com.nyekilajos.love2drive;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jakewharton.rxbinding2.view.RxView;
import com.nyekilajos.love2drive.login.GoogleSignInHelper;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends RxAppCompatActivity {

    private static final int REQUEST_GOOGLE_LOGIN = 1;

    @BindView(R.id.sign_in_button)
    SignInButton signInButton;

    @BindView(R.id.progress)
    View progressBar;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this::showSignIn)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Auth.GoogleSignInApi.silentSignIn(googleApiClient).setResultCallback(resultCallback -> {
                            if (resultCallback.isSuccess()) {
                                openHome();
                            } else {
                                showSignIn(null);
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        showSignIn(new ConnectionResult(i));
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, GoogleSignInHelper.getGoogleSignInOptions())
                .build();

        RxView.clicks(signInButton).compose(bindToLifecycle()).subscribe(nextItem -> signIn());
    }

    private void showSignIn(ConnectionResult connectionResult) {
        if (connectionResult != null) {
            Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
        signInButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, REQUEST_GOOGLE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GOOGLE_LOGIN) {
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                Toast.makeText(this, account.getDisplayName(), Toast.LENGTH_LONG).show();
                openHome();
            }
        } else {
            Toast.makeText(this, result.getStatus().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
