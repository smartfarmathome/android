package com.smf.smf.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smf.smf.MainActivity;
import com.smf.smf.R;
import com.smf.smf.data.model.LoggedInUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import durdinapps.rxfirebase2.RxFirestore;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

public class SignupActivity extends AppCompatActivity {

    private EditText inputUserName, inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String TAG = SignupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputUserName = findViewById(R.id.display_username);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUserNameString = inputUserName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(inputUserNameString)) {
                    Toast.makeText(getApplicationContext(), "Enter user Name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    if( task.getResult() == null ) {
                                        Log.e(TAG, "onComplete: result is null");
                                        return;
                                    }

                                    FirebaseUser firebaseUser = task.getResult().getUser();
                                    if( firebaseUser == null ) {
                                        Log.e(TAG, "onComplete: firebaseUser is null");
                                        return;
                                    }

                                    Toast.makeText(SignupActivity.this, "Welcome " + inputUserNameString, Toast.LENGTH_SHORT).show();
                                    String uid = firebaseUser.getUid();
                                    LoggedInUser myNewUser = new LoggedInUser(uid, inputUserNameString);

                                    //Set data
                                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                    DocumentReference document = firestore.collection("Users").document(uid);
                                    RxFirestore.setDocument(document, myNewUser).subscribe(new CompletableObserver() {
                                        @Override
                                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onComplete() {
                                            //Launch mainActivity
                                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                            finish();
                                        }

                                        @Override
                                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                            Log.e(TAG, "onError: Account has been created but fail to save data to DB. Exception => " + e);
                                        }
                                    });
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}