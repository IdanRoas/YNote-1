package com.example.ynote.ynote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder>{

    public List<Comments> commentsList;
    public List<User> userList;
    public Activity context;

    private String noteId;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public CommentsRecyclerAdapter(Activity context, List<Comments> commentsList,List<User> userList){

        this.commentsList = commentsList;
        this.userList = userList;
        this.context = context;
    }

    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);


        //context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        noteId = context.getIntent().getExtras().get("noteId").toString();

        //Intent i = getIntent();

        //final String noteId = parent.().get("noteId").toString();


        //return new CommentsRecyclerAdapter.ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        //CommentsRecyclerAdapter commentsRecyclerAdapter = new CommentsRecyclerAdapter(holder.itemView.getContext());

        final String commentId = commentsList.get(position).CommentsId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();


        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);

        /*String image_url = commentsList.get(position).get();
        String thumbUri = blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);*/

        String comment_user_id = commentsList.get(position).getUserId();

        if(comment_user_id.equals(currentUserId)){

            holder.commentDeleteBtn.setEnabled(true);
            holder.commentDeleteBtn.setVisibility(View.VISIBLE);
        }

        holder.commentDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                firebaseFirestore.collection("Notes/" + noteId + "/Comments").document(commentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        commentsList.remove(position);
                                        userList.remove(position);

                                        //notifyItemRemoved(position);
                                        notifyDataSetChanged();

                                    }
                                });
                                /*firebaseFirestore.collection("users" + currentUserId + "/Notifications").document(commentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        commentsList.remove(position);
                                        userList.remove(position);

                                        //notifyItemRemoved(position);
                                        notifyDataSetChanged();

                                    }
                                });*/
                                Toast.makeText(context, "Your Comment Has Been Deleted ...", Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this comment?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });

        String userImage = userList.get(position).getURI();
        String userName = userList.get(position).getName();
        holder.setUserData(userName,userImage);


        //Toast.makeText(context, "kkkkkk : " + (commentsList.get(position).getTimestamp()==null) , Toast.LENGTH_SHORT).show();
        /*try{
            long millisecond = commentsList.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("dd/MM/yyyy",new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e){

            Toast.makeText(context, "abcd Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }*/


    }




    @Override
    public int getItemCount() {

        if(commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private ImageView commentDeleteBtn;
        private TextView comment_message;
        //private TextView comment_date;

        private TextView commentUserName;
        private CircleImageView commentUserImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            commentDeleteBtn = mView.findViewById(R.id.comment_delete);


            //commentDeleteBtn.setOnClickListener(this);
        }

        public void setUserData(String name, String image){

            commentUserImage = mView.findViewById(R.id.comment_image);
            commentUserName = mView.findViewById(R.id.comment_username);
            commentUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(commentUserImage);

        }
        public void setComment_message(String message){

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

        }

        /*public void setTime(String date) {

            comment_date = mView.findViewById(R.id.comment_date);
            comment_date.setText(date);

        }*/

    }
}
