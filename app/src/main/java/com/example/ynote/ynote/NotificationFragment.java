package com.example.ynote.ynote;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class NotificationFragment extends android.support.v4.app.Fragment {

    private List<NotificationModel> notificationList;
    private List<User> userList;
    RecyclerView notification_list_view;
    NotificationRecyclerViewAdapter notificationRecyclerViewAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    String User_id;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notification_list_view = view.findViewById(R.id.recycleView);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        User_id = auth.getCurrentUser().getUid();
        notificationList = new ArrayList<>();
        userList = new ArrayList<>();


        notificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter(notificationList, userList, getContext());
        notification_list_view.setHasFixedSize(true);
        notification_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        notification_list_view.setAdapter(notificationRecyclerViewAdapter);


        if (auth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
            notification_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if (reachedBottom) {
                        loadMoreNotifications();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("users/" + User_id + "/Notifications").orderBy("timestamp", Query.Direction.ASCENDING);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        if (isFirstPageFirstLoad) {
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            notificationList.clear();
                            userList.clear();
                            notificationRecyclerViewAdapter.notifyDataSetChanged();
                        }
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String notificationId = doc.getDocument().getId();
                                final NotificationModel notification = doc.getDocument().toObject(NotificationModel.class).withId(notificationId);

                                String notificationUserId = doc.getDocument().getString("userId");
                                firebaseFirestore.collection("users").document(notificationUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            User user = task.getResult().toObject(User.class);
                                            if (isFirstPageFirstLoad) {
                                                userList.add(user);
                                                notificationList.add(notification);
                                            } else {
                                                userList.add(0, user);
                                                notificationList.add(0, notification);
                                            }
                                            notificationRecyclerViewAdapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(getActivity(), "Exception : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
        return view;
    }

    public void loadMoreNotifications() {
        if (auth.getCurrentUser() != null) {
            Query nextQuery = firebaseFirestore.collection("users/" + User_id + "/Notifications")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible);
            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                String notificationId = doc.getDocument().getId();
                                final NotificationModel notification = doc.getDocument().toObject(NotificationModel.class).withId(notificationId);
                                String notificationUserId = doc.getDocument().getString("userId");
                                firebaseFirestore.collection("users").document(notificationUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            User user = task.getResult().toObject(User.class);
                                            userList.add(user);
                                            notificationList.add(notification);
                                            notificationRecyclerViewAdapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(getActivity(), "Exception : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }
}



