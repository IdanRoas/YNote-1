package com.example.ynote.ynote;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder>{
    private List<NotificationModel> notificationList;
    public List<User> userList;
    private Context context;
    private String noteId;
    private CharSequence ago;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String noteTitle;


    public NotificationRecyclerViewAdapter(List<NotificationModel> notificationList, List<User> userList, Context context) {
        this.notificationList = notificationList;
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_notification_item,parent,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationRecyclerViewAdapter.ViewHolder holder,final int position) {

        holder.setIsRecyclable(false);
        final String notificationId = notificationList.get(position).NotificationsId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        final String notificationMessage = notificationList.get(position).getMessage();
        final String notification_user_id = notificationList.get(position).getUserId();
        noteId = notificationList.get(position).getNoteId();
        noteTitle = notificationList.get(position).getNoteTitle();
        final String userImage = userList.get(position).getURI();
        final String userName = userList.get(position).getName();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            long time = notificationList.get(position).getTimestamp().getTime();
            long now = System.currentTimeMillis();
            ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
        } catch (Exception e){
            Toast.makeText(context, " Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        holder.setNotificationText(userName,userImage,notificationMessage,noteTitle,ago);
        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notificationViewIntent = new Intent(context,ProfileActivity.class);
                notificationViewIntent.putExtra("notificationUserId",notification_user_id);
                context.startActivity(notificationViewIntent);
            }
        });
        holder.notificatioItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, Note.class);
            // ToDo: send note object

            }
        });
    }

    @Override
    public int getItemCount() {

        if(notificationList != null) {
            return notificationList.size();
        } else {
            return 0;
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private CircleImageView circleImageView;
        private TextView userName;
        private TextView notificationMessage;
        private TextView notificationDate;
        private TextView notificationNoteTitle;
        private ConstraintLayout notificatioItem;

        public ViewHolder(View itemView){
            super(itemView);
            view=itemView;
            }

        public void setUserData(String name, String image){
            circleImageView = view.findViewById(R.id.listview_image);
            notificatioItem= view.findViewById(R.id.notification_item);
            userName.setText(name);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(circleImageView);
        }
        public void setNotification_message(String message){
            notificationMessage = view.findViewById(R.id.listview_message);
            notificationMessage.setText(message);
        }

        public void setTime(CharSequence date){
            notificationDate = view.findViewById(R.id.listview_date);
            notificationDate.setText(date);
        }

        public void setNotificationText(String name, String image, String message,String title, CharSequence date) {
            circleImageView = view.findViewById(R.id.listview_image);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption)
                    .load(image).
                    into(circleImageView);
            notificationNoteTitle = view.findViewById(R.id.listview_note_title);
            notificationMessage = view.findViewById(R.id.listview_message);
            notificationNoteTitle.setText(title);
            String sourceString = "<b><big>" + name + "</b> " + " commented: " +"<b>"+ message+"</b> " + " in your note: ";
            notificationMessage.setText(Html.fromHtml(sourceString));
            notificationDate = view.findViewById(R.id.listview_date);
            notificationDate.setText(date);
        }
    }
}

