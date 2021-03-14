package com.smf.smf.data;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smf.smf.data.model.LoggedInUser;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import durdinapps.rxfirebase2.RxFirebaseAuth;
import io.reactivex.Maybe;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private FirebaseAuth mAuth;
    private String TAG = LoginDataSource.class.getSimpleName();

    public LoginDataSource() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void implSignIn(String username, String password) {

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(Executors.newSingleThreadExecutor(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
////                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();

                        }
                        // ...
                    }
                });
    }

    private void createUser(String username, String password) {
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(Executors.newSingleThreadExecutor(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public Result<LoggedInUser> login(String username, String password) {
        // TODO
        return null;
    }

    public void logout() {
        // TODO: revoke authentication
        return;
    }
}