package com.example.ynote.ynote;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileAbout;
    private TextView profileUserName;
    private TextView profileScore;
    private CircleImageView profileUserImage;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String name,score,about,profileUri;
    private String profileUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileAbout = findViewById(R.id.profile_user_about);
        profileUserName = findViewById(R.id.profile_user_name);
        profileScore = findViewById(R.id.profile_user_score);
        profileUserImage = findViewById(R.id.profile_user_image);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        profileUserId = getIntent().getExtras().get("notificationUserId").toString();

        final String currentUserId = firebaseAuth.getCurrentUser().getUid();


        firebaseFirestore.collection("users").document(profileUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        name = (String) task.getResult().getData().get("name");
                        score = (String) task.getResult().getData().get("Score");
                        profileUri = (String) task.getResult().getData().get("URI");
                        about = (String) task.getResult().getData().get("About");
                    }
                    profileScore.setText("Score : "+ score);
                    profileUserName.setText(name);
                    profileAbout.setText(about);
                    Uri uri = Uri.parse(profileUri);
                    Glide.with(getBaseContext())
                            .load(uri)
                            .into(profileUserImage);
                }
            }
        });

        /*ViewPager vpPager =findViewById(R.id.note_vpPager);
        adapterViewPager = new Note.MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);*/
    }
}
