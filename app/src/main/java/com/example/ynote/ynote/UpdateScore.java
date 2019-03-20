package com.example.ynote.ynote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateScore {

    public UpdateScore(){

    }

    public void update(final Context context, final String UserId, final int i) {

        final Map<String, Object> userdata=  new HashMap<>();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference docRef = firebaseFirestore.collection("users").document(UserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = (String) task.getResult().getData().get("name");
                        String profileUri = (String) task.getResult().getData().get("URI");
                        String about = (String) task.getResult().getData().get("About");
                        String score = (String) task.getResult().getData().get("Score");
                        int intScore;
                        try {
                            intScore = Integer.parseInt(score);
                        }
                        catch (NumberFormatException e)
                        {
                            intScore = 15;
                        }
                        int newScore = (intScore + i);
                        String stringNewScore = "" + newScore;

                        userdata.put("name", name);
                        userdata.put("Score", stringNewScore);
                        userdata.put("About", about);
                        userdata.put("URI",profileUri);

                        docRef.set(userdata);
                    }
                }
               else{
                    Toast.makeText(context , "Exception : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });

    }
}
