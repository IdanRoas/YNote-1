package com.example.ynote.ynote;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import static com.example.ynote.ynote.MapsActivity.getBiggestRadius;
import static com.example.ynote.ynote.MapsActivity.polylinePoints;


public class NewNote extends AppCompatActivity {

    EditText editText, titleView;
    Button shareButton;
    String text, title,name = "x";
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Uri mainImageURI;
    ImageView imageView;
    final String[] uriArray = new String[5];
    final ArrayList<String> downloadUriArray = new ArrayList<>();
    int picIndex=0;
    TextView[] textViewsArray= new TextView[5];
    private StorageReference storageReference;
    ProgressBar  progressBar;
    int checker=0;
    final FirebaseFirestore db= FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        editText = findViewById(R.id.text);
        shareButton = findViewById(R.id.share_button);
        titleView = findViewById(R.id.title);
        imageView = findViewById(R.id.imageView_btn);
        final Button im_btn = findViewById(R.id.img_button);

        textViewsArray[0] = findViewById(R.id.pic1_btn);
        textViewsArray[1] = findViewById(R.id.pic2_btn);
        textViewsArray[2] = findViewById(R.id.pic3_btn);
        textViewsArray[3] = findViewById(R.id.pic4_btn);
        textViewsArray[4] = findViewById(R.id.pic5_btn);


        im_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = 0;
                while (uriArray[i] != null && i < 5)
                    i++;
                if (i == 4) {
                    Toast.makeText(getBaseContext(), "Limit for 5 picture", Toast.LENGTH_LONG).show();
                }
                    picIndex = i;
                BringImagePicker();
            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();
        progressBar = findViewById(R.id.progressBar);
        // share button.
        shareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String time = Calendar.getInstance().getTime().toString();
                progressBar.setVisibility(View.VISIBLE);
                for ( int i = 0; i <= 4; i++) {
                    if (uriArray[i] != null) {
                        final StorageReference imagePath = storageReference.child("noteImages").child(user.getUid() + time).child("image" + i + ".jpg");
                        imagePath.putFile(Uri.parse(uriArray[i])).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadUriArray.add(uri.toString());
                                        checker++;
                                        if(checker==5)
                                            send();


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getBaseContext(), "Failed upload, try agin", Toast.LENGTH_LONG).show();
                                        checker++;
                                        if(checker==5)
                                            send();
                                    }
                                });
                            }
                        });
                    }else {
                        checker++;
                        if (checker==5)
                            send();
                    }

                }




                }
        });

        for(int i=0;i<5;i++) {
            setLongClickButton(i);
            setClickButton(i);
        }

    }

        private void BringImagePicker() {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(3, 4)
                    .start(NewNote.this);

        }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    mainImageURI = result.getUri();
                    uriArray[picIndex]=mainImageURI.toString();
                    imageView.setImageURI(mainImageURI);
                    textViewsArray[picIndex].setVisibility(View.VISIBLE);
                    textViewsArray[picIndex].setTextColor(Color.BLACK);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Exception error = result.getError();
                    Log.i("Error",error.toString());
                }
            }
    }

    public  void setLongClickButton(final int i) {
        textViewsArray[i].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                uriArray[i]=null;
                textViewsArray[i].setTextColor(Color.RED);
                imageView.setImageResource(R.drawable.ic_launcher_foreground);

                return true;

            }
        });
    }

    public void setClickButton(final int i){

        textViewsArray[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(getBaseContext())
                        .load(Uri.parse(uriArray[i]))
                        .into(imageView);
            }

        });

    }

    public void send(){
        if (checker==5) {
            String userId = user.getUid();
            String text = editText.getText().toString();
            title = titleView.getText().toString();
            NoteObj noteOb = new NoteObj(polylinePoints, text, "pic", downloadUriArray, title, userId, getBiggestRadius(), Calendar.getInstance().getTime().toString());
            db.collection("Notes").document().set(noteOb);
            progressBar.setVisibility(View.INVISIBLE);


            Intent intent = new Intent(NewNote.this, MainScreen.class);
            startActivity(intent);
        }
    }

}




