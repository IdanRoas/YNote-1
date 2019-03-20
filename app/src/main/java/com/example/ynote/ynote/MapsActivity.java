package com.example.ynote.ynote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.SphericalUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
   final private int MAX_AREA=1500;
    private GoogleMap mMap;
    private View draggableView;
    static List<LatLng> polylinePoints = new ArrayList<>();
    private Polyline polyline;
    Button minus, plus;
    int zoom = 18;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMapAsync(this);

        draggableView = findViewById(R.id.draggable);
        minus = findViewById(R.id.minus);
        plus =  findViewById(R.id.plus);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;









            if (MainScreen.getUserLocation()!= null) {
                mMap.clear();
                mMap.setMinZoomPreference((float) 13);
                mMap.setIndoorEnabled(true);
                mMap.setMyLocationEnabled(true);
                googleMap.setBuildingsEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainScreen.getUserLocation(), zoom));


            }

            //add  free hand polygon, limited for 1.5 Km/
            draggableView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    LatLng position = mMap.getProjection().fromScreenLocation(
                            new Point((int) motionEvent.getX(), (int) motionEvent.getY()));

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        clearPolygon();
                        polylinePoints.add(position);
                        polyline = mMap.addPolyline(new PolylineOptions().addAll(polylinePoints));
                    }else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        polylinePoints.add(position);
                        polyline.setPoints(polylinePoints);
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                        polylinePoints.add( polylinePoints.get(0));
                        polyline.setPoints(polylinePoints);
                        if (getBiggestRadius()>MAX_AREA) {
                            Toast.makeText(getBaseContext(), "The  share area is limit for 1.5 KM ", Toast.LENGTH_LONG).show();
                            clearPolygon();
                        }
                        else {
                            // save dialog message
                            new AlertDialog.Builder(MapsActivity.this)
                                    .setIcon(android.R.drawable.ic_menu_set_as)
                                    .setTitle("save location")
                                    .setMessage("Do you want to save this area?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(getBaseContext(), NewNote.class);
                                            startActivity(intent);
                                            Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_LONG).show();
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clearPolygon();
                                }
                            }).show();
                        }
                    }
                    return true;
                }
            });

        // zoom in button- replace the original zoom in button
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainScreen.getUserLocation(),zoom++ ));
            }
        });

        /// zoom out button- replace the original zoom out button
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MainScreen.getUserLocation(),zoom-- ));
            }
        });
    }

    static protected Map<String, GeoPoint> getPolylinePoints() {
        Map<String, GeoPoint> map = new HashMap<>();
        for (LatLng i : polylinePoints)
            map.put(""+i, new GeoPoint(i.latitude,i.longitude));
        return map;
    }
    //clear polygon
    private void clearPolygon(){
       if(polyline!=null) {
           polylinePoints.clear();
           polyline.remove();
           polyline = null;
       }
    }
    // return the biggest radius in the polygon
    // calculate the biggest distance between each two point, and return the maximum radius
    static protected double getBiggestRadius(){
     double  max=0;
       for(int i= 0; i< polylinePoints.size()-2; i++){
           for(int j= i+1; j< polylinePoints.size()-1; j++){
               if( SphericalUtil.computeDistanceBetween(polylinePoints.get(i), polylinePoints.get(j))>max)
                   max=SphericalUtil.computeDistanceBetween(polylinePoints.get(i), polylinePoints.get(j));
           }}
           return max/2;
    }

}