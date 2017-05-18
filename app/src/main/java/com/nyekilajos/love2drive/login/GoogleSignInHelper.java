package com.nyekilajos.love2drive.login;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Created by Lajos_Nyeki on 5/18/2017.
 */

public final class GoogleSignInHelper {

    private static final GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build();

    private GoogleSignInHelper() {
    }

    public static GoogleSignInOptions getGoogleSignInOptions() {
        return googleSignInOptions;
    }
}
