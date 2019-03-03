package com.example.ynote.ynote;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar commentToolbar;
    private EditText commentField;
    private ImageView commentPostBtn;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String userId;
    private String noteId;

    private RecyclerView comment_list_view;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;
    private List<User> userList;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public CommentsActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentToolbar = findViewById(R.id.comment_toolbar);
        commentField = findViewById(R.id.comment_field);
        commentPostBtn = findViewById(R.id.comment_post_btn);
        comment_list_view = findViewById(R.id.comment_list);

        //setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Comments");

        noteId = getIntent().getStringExtra("noteId");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        userList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(CommentsActivity.this,commentsList, userList);
        comment_list_view.setHasFixedSize(true);
        comment_list_view.setLayoutManager(new LinearLayoutManager(this));
        comment_list_view.setAdapter(commentsRecyclerAdapter);
        if(firebaseAuth.getCurrentUser() != null) {

            comment_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {
                        loadMoreComments();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Notes/" + noteId + "/Comments").orderBy("timestamp", Query.Direction.DESCENDING).limit(7);

            firstQuery.addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            commentsList.clear();
                            userList.clear();
                        }

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String commentId = doc.getDocument().getId();
                                final Comments comments = doc.getDocument().toObject(Comments.class);

                                String commentUserId = doc.getDocument().getString("userId");
                                firebaseFirestore.collection("users").document(commentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            User user = task.getResult().toObject(User.class);


                                            if (isFirstPageFirstLoad) {

                                                commentsList.add(comments);
                                                userList.add(user);

                                            } else {

                                                commentsList.add(0, comments);
                                                userList.add(0, user);
                                            }

                                            commentsRecyclerAdapter.notifyDataSetChanged();

                                        } else {
                                            Toast.makeText(CommentsActivity.this, "Exception : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                        isFirstPageFirstLoad = false;

                    }
                }
            });

        }
    }

    public void loadMoreComments(){

        if(firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("Notes/" + noteId + "/Comments")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(7);


            nextQuery.addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String commentId = doc.getDocument().getId();
                                final Comments comments = doc.getDocument().toObject(Comments.class);
                                String commentUserId = doc.getDocument().getString("userId");
                                firebaseFirestore.collection("users").document(commentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            User user = task.getResult().toObject(User.class);




                                                commentsList.add(comments);
                                                userList.add(user);



                                            commentsRecyclerAdapter.notifyDataSetChanged();

                                        } else {
                                            Toast.makeText(CommentsActivity.this, "Exception : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });}
                        }
                    }
                }
            });


        }



        commentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String commentMessage = commentField.getText().toString();
                    Map<String, Object> commentMap = new HashMap<>();
                    commentMap.put("message", commentMessage);
                    commentMap.put("userId", userId);
                    commentMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Notes/" + noteId + "/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(CommentsActivity.this, "Error Posting Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                commentField.setText("");
                                Toast.makeText(CommentsActivity.this, "Your Comment Posted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });


    }
}
