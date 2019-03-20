package com.example.ynote.ynote;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewComment extends AppCompatActivity {

    //private Toolbar newCommentToolbar;
    private TextView toolbarTitle;

    private EditText commentFieldText;
    private ImageView commentAddBtn;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String currentUserId;
    private String noteUserId;
    private boolean sameUser = false;
    private UpdateScore updateScore = new UpdateScore();
    private String noteId;
    ProgressBar progressBar;
    Map<String, Object> commentMap = new HashMap<>();
    private String noteTitle;


   /* private RecyclerView comment_list_view;

    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;
    private List<User> userList;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);
        //this.setFinishOnTouchOutside(false);

        //newCommentToolbar = findViewById(R.id.new_comment_toolbar);
        //toolbarTitle = newCommentToolbar.findViewById(R.id.toolbar_title);
        toolbarTitle = findViewById(R.id.toolbar_title);
        progressBar = findViewById(R.id.sendProgressBar);


        //TextView mTitle = toolbarTitle.findViewById(R.id.toolbar_title);

        //newCommentToolbar = findViewById(R.id.comment_toolbar);
        //getSupportActionBar().setTitle("Add New Comment");

        commentFieldText = findViewById(R.id.comment_field_text);
        commentAddBtn = findViewById(R.id.comment_add_btn);

        noteId = getIntent().getStringExtra("noteId");
        noteTitle = getIntent().getStringExtra("noteTitle");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        noteUserId = getIntent().getExtras().get("noteUserId").toString();
        currentUserId = firebaseAuth.getCurrentUser().getUid();


        if(noteUserId.equals(currentUserId)) {
            sameUser = true;
        }

        /*commentsList = new ArrayList<>();
        userList = new ArrayList<>();*/

        //maybe I need to remove context in commentsRecyclerAdapter
        //commentsRecyclerAdapter = new CommentsRecyclerAdapter(Co.this,commentsList, userList);


        commentAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressBar.setVisibility(View.VISIBLE);
                String commentMessage = commentFieldText.getText().toString();

                if (!TextUtils.isEmpty(commentMessage)) {

                    commentMap.put("message", commentMessage);
                    commentMap.put("userId", currentUserId);
                    commentMap.put("timestamp", FieldValue.serverTimestamp());
                    commentMap.put("noteId", noteId);
                    commentMap.put("noteTitle", noteTitle);


                    //Toast.makeText(NewComment.this, "Im here", Toast.LENGTH_SHORT).show();

                    firebaseFirestore.collection("Notes/" + noteId + "/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(NewComment.this, "Error Posting Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                commentFieldText.setText("");
                                Toast.makeText(NewComment.this, "Your Comment Posted", Toast.LENGTH_SHORT).show();
                                if (!sameUser) {
                                    updateScore.update(NewComment.this, noteUserId, 15);
                                    updateScore.update(NewComment.this, currentUserId, 4);
                                } else {
                                    updateScore.update(NewComment.this, noteUserId, 4);
                                }
                                //progressBar.setVisibility(View.INVISIBLE);
                                //finish();

                            }
                        }
                    });

                    //if (!sameUser){
                        firebaseFirestore.collection("users/" + noteUserId + "/Notifications").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(NewComment.this, "Error Posting Notifications Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    //commentFieldText.setText("");
                                    //Toast.makeText(NewComment.this, "Your Comment Posted", Toast.LENGTH_SHORT).show();


                                }
                            }
                        });
                   //}
                    progressBar.setVisibility(View.INVISIBLE);
                    finish();


                }

            }
        });

    }
}
