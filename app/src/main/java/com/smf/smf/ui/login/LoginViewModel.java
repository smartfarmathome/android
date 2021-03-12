package com.smf.smf.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import durdinapps.rxfirebase2.RxFirebaseAuth;
import durdinapps.rxfirebase2.RxFirestore;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.util.Log;
import android.util.Patterns;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smf.smf.data.LoginRepository;
import com.smf.smf.data.model.LoggedInUser;
import com.smf.smf.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private String TAG = LoginViewModel.class.getSimpleName();

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public Disposable login(String username, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final CompositeDisposable compositeDisposable = new CompositeDisposable();
        return RxFirebaseAuth.signInWithEmailAndPassword(auth, username, password)
                .subscribeOn(Schedulers.io())
                .map(AuthResult::getUser)
                .subscribe(firebaseUser -> {
                            //Get and serialize data as loggedInUser
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            DocumentReference document = firestore.collection("Users").document(firebaseUser.getUid());
                            compositeDisposable.add(RxFirestore.getDocument(document)
                                    .map(userDoc -> userDoc.toObject(LoggedInUser.class)) // serialize here
                                    .subscribe(loggedInUser -> {
                                        loginRepository.setLoggedInUser(loggedInUser);
                                        loginResult.setValue(new LoginResult(new LoggedInUserView(loggedInUser.getDisplayName())));
                                        Log.i(TAG, "login: success");
                                    }, exception -> {
                                        loginResult.setValue(new LoginResult(R.string.login_failed));
                                        Log.e(TAG, "login: failed. Fail to read user information from firestore");
                                    }));
                        },
                        firebaseUserException -> {
                            loginResult.setValue(new LoginResult(R.string.login_failed));
                            Log.e(TAG, "login: failed");
                        }, compositeDisposable::clear);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}