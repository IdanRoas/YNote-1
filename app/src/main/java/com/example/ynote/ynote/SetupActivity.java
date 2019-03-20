package com.example.ynote.ynote;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;


public class SetupActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    Uri mainImageURI;
    TextView name;
    Button set_btn;
    EditText aboutText;
    String about_text,facebookProfileImage;
    private StorageReference storageReference;
    StorageReference image_path;
    Task<Uri> download_uri;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        circleImageView = findViewById(R.id.note_user_image);
        name=findViewById(R.id.note_user_name);
        set_btn=findViewById(R.id.save_set_btn);
        aboutText=findViewById(R.id.about_text);
        name.setText(user.getDisplayName());
        String facebookUserId = "";

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

     // find the Facebook profile and get the user's id
        for(UserInfo profile : user.getProviderData()) {
            // check if the provider id matches "facebook.com"
            if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                facebookUserId = profile.getUid();
            }
        }
        // facebook photo profile
         facebookProfileImage = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";

        Glide.with(getBaseContext())
                .load(Uri.parse(facebookProfileImage)) // the uri you got from Firebase
                .into(circleImageView);

        storageReference=FirebaseStorage.getInstance().getReference();

        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                       aboutText.setText((String)task.getResult().getData().get("About"));
                        String image =  (String)task.getResult().getData().get("URI");
                        if (!image.equals(facebookProfileImage)) {
                            Glide.with(getBaseContext())
                                    .load(image)
                                    .into(circleImageView);
                        }
                    }
                }
            }
        });

       final Map <String, String> userdata=  new HashMap<>();
        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about_text=aboutText.getText().toString();
                aboutText.setText(about_text);

                //String token_id = FirebaseInstanceId.getInstance().getToken();
                userdata.put("name", user.getDisplayName());
                userdata.put("Score", "15");
                userdata.put("About", about_text);
                userdata.put("URI",facebookProfileImage);
                //userdata.put("token_id",token_id);


        if(mainImageURI!=null&&!mainImageURI.toString().equals(facebookProfileImage)) {
             image_path = storageReference.child("userProfileImage").child(user.getUid() + ".jpg");
            image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        download_uri = image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                storageReference.child("userProfileImage").child(user.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                    }
                                });
                                if(mainImageURI!=null)
                                    userdata.put("URI",uri.toString());




                            }
                        });

                        Toast.makeText(SetupActivity.this, "The image uploaded", Toast.LENGTH_LONG).show();

                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error uploaded "+error, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }else  {userdata.put("URI",mainImageURI.toString());
            db.collection("users").document(user.getUid()).set(userdata);}
                Intent intent= new Intent(SetupActivity.this, MainScreen.class);
                startActivity(intent);

            } });


        circleImageView.setOnLongClickListener(new  View.OnLongClickListener() {

            @Override public boolean onLongClick(View view) {
                if (facebookProfileImage!=null) {
                    Glide.with(getBaseContext())
                            .load(facebookProfileImage)
                            .into(circleImageView);
                    setMainImageURIToFacebookImageURI();
                }else
                    Glide.with(getBaseContext())
                            .load(R.drawable.defaultprofile)
                            .into(circleImageView);
                return true;   }
        });


                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M))
                            if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            else {
                                BringImagePicker();
                            }
                        else
                            BringImagePicker();

                    }
                });
    }


    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                circleImageView.setImageURI(mainImageURI);




            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Log.i("Error",error.toString());

            }
        }

    }
    public  void setMainImageURIToFacebookImageURI(){
        mainImageURI=Uri.parse(facebookProfileImage);
    }
}

//*/