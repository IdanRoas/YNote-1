package com.example.ynote.ynote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.PolyUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static android.R.layout.simple_list_item_1;


public class HomeFragment extends Fragment {

    final String[] radiusOption = {"5Km", "1.5km", "300m", "40m", "5m"};
    String photoUrl, facebookUserId;
    ListView publicListView, sponseredListView;
    ArrayList<String> titles = new ArrayList<String>();
    FirebaseAuth mAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    int maxMeterDisplay, minMeterDisplay;
    ArrayAdapter spinnerAdapter, arrayAdapter;
    Spinner radiusSpin;
    View view;
    Map<Integer, String> noteKeyMap, userKeyMap,textMap,titleMap,dateMap,typeMap;
    Map<Integer,List<String>> uriMap;
      List<String> uriList;
     static String text;


    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        noteKeyMap = new HashMap<>();
        userKeyMap = new HashMap<>();
        uriMap=new HashMap<>();
        textMap=new HashMap<>();
        titleMap = new HashMap<>();
        dateMap = new HashMap<>();
        typeMap = new HashMap<>();



        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        radiusSpin = view.findViewById(R.id.radiusSpinner);
        arrayAdapter = new ArrayAdapter(getActivity(), simple_list_item_1, titles);
        sponseredListView = view.findViewById(R.id.sponsored_listview);
        publicListView = view.findViewById(R.id.public_listview);
        publicListView.setAdapter(arrayAdapter);
        // intent to Note activity and send thr relevant data for display the relevant Note
        publicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), Note.class);
                intent.putExtra("noteKey", noteKeyMap.get(i));
                intent.putExtra("noteKey", noteKeyMap.get(i));
                intent.putExtra("userKey", userKeyMap.get(i));
                intent.putExtra("textMap",textMap.get(i));
                intent.putExtra("titleMap",titleMap.get(i));
                intent.putExtra("dateMap",dateMap.get(i));
                intent.putExtra("typeMap",typeMap.get(i));
                intent.putExtra("uriMap",(new  ArrayList<>(uriMap.get(i))));
                startActivity(intent);

            }
        });

        spinnerAdapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, radiusOption);
        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        radiusSpin.setAdapter(spinnerAdapter);

        radiusSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            //spinner option
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                switch ((pos)) {
                    case 0:
                        spinnerOption(1500,5000);
                        break;
                    case 1:
                        spinnerOption(300,1500);
                        break;
                    case 2:
                        spinnerOption(40,300);
                        break;
                    case 3:
                        spinnerOption(5,40);
                        break;
                    case 4:
                        spinnerOption(0,5);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        facebookUserId = "";
        // find the Facebook profile and get the user's id
        for (UserInfo profile : user.getProviderData()) {
            // check if the provider id matches "facebook.com"
            if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                facebookUserId = profile.getUid();
            }
        }

        final ImageView newNote = view.findViewById(R.id.addNote_btn);

        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add = new Intent(getActivity(), MapsActivity.class);
                startActivity(add);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }


// convert  ArrayList<mpa> to List<LatLng>
    static List<LatLng> convertToLatLngList(ArrayList<Map> mapArrayList) {
        List<LatLng> list = new ArrayList<>();
        for (int i=0; i<mapArrayList.size(); i++)
            list.add(new LatLng(Double.parseDouble(mapArrayList.get(i).get("latitude").toString()),Double.parseDouble(mapArrayList.get(i).get("longitude").toString() )));
        return list;
    }

// display the note according the  distance spinner option
private void spinnerOption(int min, int max){
    maxMeterDisplay = max;
    minMeterDisplay = min;
    titles.clear();
    arrayAdapter.notifyDataSetChanged();
    publicListView.setAdapter(arrayAdapter);
    retrieveNote();
}
    // save the relevant data from firestore for display the match note according to the title and location and pass it to the note activity
    private void retrieveNote() {
        noteKeyMap.clear();
        db.collection("Notes").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getDocument().get("title") != null && doc.getDocument().get("biggestRadius") != null) {
                            double biggestRadius = Double.parseDouble(doc.getDocument().get("biggestRadius").toString());
                            ArrayList<Map>getPolyLineP=(ArrayList<Map>) doc.getDocument().get("polylineP");
                            //display the  relevant location note
                            if(getPolyLineP!=null)
                           if (maxMeterDisplay >= biggestRadius && minMeterDisplay <= biggestRadius&&MainScreen.getUserLocation()!=null) {
                                if (PolyUtil.containsLocation(MainScreen.getUserLocation(),convertToLatLngList(getPolyLineP), true)) {
                                    String title = doc.getDocument().get("title").toString();
                                    String noteId = doc.getDocument().getId();
                                    String userKey = doc.getDocument().get("userId").toString();
                                    uriList= (List<String>)doc.getDocument().get("uri");
                                     text=doc.getDocument().get("text").toString();
                                     String type=doc.getDocument().get("type").toString();
                                     String date=doc.getDocument().get("date").toString();
                                     //List<LatLng> polyLineP = doc.getDocument().get("polylineP");
                                    titles.add(title);

                                    /*NoteObj parcelNote = new NoteObj();
                                    parcelNote.setText(text);
                                    parcelNote.setType(type);
                                    parcelNote.setUri(uriList);
                                    parcelNote.setTitle(title);
                                    parcelNote.setUserId(userKey);
                                    parcelNote.setBiggestRadius(biggestRadius);
                                    parcelNote.setDate(date);*/
                                    //Parcelable noteParcel = new NoteObj(getPolyLineP,text,type,uriList,title,userKey,biggestRadius,date);

                                    /*Intent in = new Intent(this, ProcessDataActivity.class);
                                    in.putExtra("note", parcelNote);
                                    startActivity(in);*/

                                    noteKeyMap.put(titles.size() - 1, noteId);
                                    userKeyMap.put(titles.size() - 1, userKey);
                                    uriMap.put(titles.size()-1,uriList);
                                    textMap.put(titles.size()-1,text);
                                    titleMap.put(titles.size()-1,title);
                                    dateMap.put(titles.size()-1,date);
                                    typeMap.put(titles.size()-1,type);
                                    arrayAdapter.notifyDataSetChanged();
                                    publicListView.setAdapter(arrayAdapter);
                                      }
                            }
                        }
                    }
                }}

        });

    }
}


