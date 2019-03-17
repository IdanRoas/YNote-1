package com.example.ynote.ynote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final int RC_SIGN_IN = 2;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String KEY_FIRST_TIME = "firstTime";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(

                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ynote_logo)
                        // Set logo drawable
                        .setTheme(R.style.MYheme)
                        .build(),
                RC_SIGN_IN);

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            IdpResponse response = IdpResponse.fromResultIntent(data);
            Intent intent = new Intent(this.getBaseContext(),MainScreen.class);
            startActivity(intent);

            if (resultCode == RESULT_OK) {

                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean firstTime = prefs.getBoolean(KEY_FIRST_TIME, false);

                if (firstTime) {
                    intent = new Intent(this.getBaseContext(),MainScreen.class);
                    startActivity(intent);
                } else {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(KEY_FIRST_TIME, true);
                    editor.commit();
                    Intent sendToSetup = new Intent(this, SetupActivity.class);
                    startActivity(sendToSetup);
                    finish();
                }


            } else {
                Toast.makeText(getBaseContext(), "Failed, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
