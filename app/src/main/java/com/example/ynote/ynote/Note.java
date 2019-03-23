package com.example.ynote.ynote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;



public class Note extends AppCompatActivity {
    private String name, date, score;
    private  String   profileUri = null, type;
    private UpdateScore updateScore = new UpdateScore();
    private CircleImageView noteUserImage;
    private   FragmentPagerAdapter adapterViewPager;
    static List<String> uriArray;
    static String noteId,title, text;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    private Button noteDeleteBtn;
    private ImageView noteLikeBtn;
    private TextView noteUserName, noteUserScore, noteComment,noteDate, noteTitle ,noteText,noteText2, noteLikeCnt, noteCommentCnt;
    private boolean sameUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        noteId = getIntent().getExtras().get("noteKey").toString();
        text = getIntent().getExtras().get("textMap").toString();
        title = getIntent().getExtras().get("titleMap").toString();
        date = getIntent().getExtras().get("dateMap").toString();
        type=getIntent().getExtras().get("typeMap").toString();
        final String noteUserId = getIntent().getExtras().get("userKey").toString();
        uriArray=(ArrayList<String>)getIntent().getExtras().get("uriMap");

        //long millisecond = commentsList.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("dd/MM/yyyy",new Date(date)).toString();
        noteDate = findViewById(R.id.note_date);
        noteDate.setText(dateString);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        final String currentUserId = firebaseAuth.getCurrentUser().getUid();


        ViewPager vpPager =findViewById(R.id.note_vpPager);
        noteDeleteBtn = findViewById(R.id.note_delete_btn);
        noteUserName = findViewById(R.id.note_user_name);
        noteUserScore = findViewById(R.id.note_user_score);
        noteUserImage = findViewById(R.id.note_user_image);
        noteCommentCnt = findViewById(R.id.note_comment_cnt);
        noteLikeCnt = findViewById(R.id.note_like_cnt);
        noteDate = findViewById(R.id.note_date);
        noteTitle = findViewById(R.id.note_title);
        noteText = findViewById(R.id.note_desc);
        noteText2=findViewById(R.id.note_desc2);
        noteTitle.setText(title);
        if(type.equals("pic")){
        noteText.setText(text);
        noteText.setVisibility(View.VISIBLE);
        vpPager.setVisibility(View.VISIBLE);
        noteText2.setVisibility(View.INVISIBLE);}
        else {
            noteText.setVisibility(View.INVISIBLE);
            noteText2.setText(text);
            vpPager.setVisibility(View.INVISIBLE);
            noteText2.setVisibility(View.VISIBLE);
        }






        if(noteUserId.equals(currentUserId)){
            sameUser = true;
            noteDeleteBtn.setEnabled(true);
            noteDeleteBtn.setVisibility((View.VISIBLE));
        }

        noteDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                firebaseFirestore.collection("Notes").document(noteId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        //updateScore.update(Note.this,currentUserId,-50);
                                        Intent deletedNoteIntent = new Intent(Note.this,MainScreen.class);
                                        startActivity(deletedNoteIntent);
                                        finish();

                                    }
                                });
                                Toast.makeText(Note.this, "Your Note Has Been Deleted ...", Toast.LENGTH_SHORT).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Note.this);
                builder.setMessage("Are you sure you want to delete this comment?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });



        noteLikeBtn = findViewById(R.id.note_like_btn);

        noteCommentCnt = findViewById(R.id.note_comment_cnt);
        noteCommentCnt.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentViewIntent = new Intent(Note.this,CommentsActivity.class);
                commentViewIntent.putExtra("noteId",noteId);
                startActivity(commentViewIntent);
            }
        }));

        noteComment = findViewById(R.id.note_comment);
        noteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(Note.this,NewComment.class);
                commentIntent.putExtra("noteId",noteId);
                commentIntent.putExtra("noteUserId",noteUserId);
                commentIntent.putExtra("noteTitle",title);
                startActivity(commentIntent);
            }
        });

        //Set likes and comments count
        firebaseFirestore.collection("Notes/" + noteId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                setCount("Likes", queryDocumentSnapshots, noteLikeCnt);
            }
        });
        firebaseFirestore.collection("Notes/" + noteId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                setCount(" Comments", queryDocumentSnapshots,noteCommentCnt);
            }
        });

        //Set User Like
        firebaseFirestore.collection("Notes/" + noteId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists())
                    noteLikeBtn.setImageDrawable(getDrawable(R.mipmap.action_like_accent));
                else
                    noteLikeBtn.setImageDrawable(getDrawable(R.mipmap.action_like_gray));
            }
        });

        //when user hits the like btn
        noteLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Notes/" + noteId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Notes/" + noteId + "/Likes").document(currentUserId).set(likesMap);
                             setScore(12,2, noteUserId,currentUserId);
                        }else {
                            firebaseFirestore.collection("Notes/" + noteId + "/Likes").document(currentUserId).delete();
                            setScore(-12,-2, noteUserId,currentUserId);
                        }
                    }
                });
            }
        });
        firebaseFirestore.collection("users").document(noteUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        name = (String) task.getResult().getData().get("name");
                        score = (String) task.getResult().getData().get("Score");
                        profileUri = (String) task.getResult().getData().get("URI");
                    }
                    noteUserScore.setText("Score : "+ score);
                    noteUserName.setText(name);
                    Uri uri = Uri.parse(profileUri);
                    Glide.with(getBaseContext())
                            .load(uri)
                            .into(noteUserImage);
                }
            }
        });


        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

    }
    //Set likes and comments count
    private  void setCount(String string, QuerySnapshot querySnapshot, TextView textView){
        int count = 0;
        if(!querySnapshot.isEmpty()){
            count = querySnapshot.size();
        }
        textView.setText(count + string);
    }

    /**
     *  score update function.
     * @param i-  points for  the note creator
     * @param j- points for the user that commit the action(like/comment..)
     * @param noteUserId
     * @param currentUserId
     */
    private void setScore(int i, int j,String noteUserId, String currentUserId){
        if (!sameUser){
            updateScore.update(Note.this,noteUserId,i);
            updateScore.update(Note.this,currentUserId,j);
        }
        else{
            updateScore.update(Note.this,noteUserId,j);
        }
    }




    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 1;
        private ArrayList<String> uri;



        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            uri = new ArrayList<>(uriArray);
            NUM_ITEMS = uri.size();
        }


        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Return the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

             if (uri != null) {
                switch (position) {

                    case 0: // Fragment # 0 - This will show the First fragment pic
                        return pic1Fragment.newInstance(uri.get(0));
                    case 1: // pic  # 1 - This will show the second fragment pic
                        return pic1Fragment.newInstance(uri.get(1));
                    case 2: // pic # 2
                        return pic1Fragment.newInstance(uri.get(2));
                    case 3: // pic # 3
                        return pic1Fragment.newInstance(uri.get(3));
                    case 4: // pic # 4
                        return pic1Fragment.newInstance(uri.get(4));
                    default:
                        return null;
                }

            }
            return null;}

            // Return the page title for the top indicator
            @Override
            public CharSequence getPageTitle ( int position){
                return "";

            }
        }
    }