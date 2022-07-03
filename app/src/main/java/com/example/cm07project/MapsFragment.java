package com.example.cm07project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment {

    private DatabaseReference reference;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            getDataFromFirebase(googleMap);

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);



        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    List<Places> placesList = new ArrayList<>();
    Places places;

    void getDataFromFirebase(GoogleMap googleMap){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Events");
        placesList.clear();
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String,String> map = (Map<String, String>) snapshot.getValue();

                places = new Places();
                places.setCountry(map.get("country"));
                places.setState(map.get("state"));
                places.setStreetAdress(map.get("streetadress"));
                places.setEvent_name(map.get("name"));
                places.setOrg(map.get("org"));
                places.setDate(map.get("date"));
                placesList.add(places);

                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setPadding(20,20,20,200);


                LatLng address = null;

                for (int i = 0; i<placesList.size();i++){
                    try {
                        String adr = placesList.get(i).getStreetAdress()+ ","+
                                placesList.get(i).getState()+","+
                                placesList.get(i).getCountry()+",";
                        //Funtion no final do codigo
                        address=getLatLongFromAdress(getActivity(),adr);

                        googleMap.addMarker(new MarkerOptions()
                                .position(address)
                                .title(placesList.get(i).getEvent_name())
                                .snippet(placesList.get(i).getOrg()+", " + placesList.get(i).getDate()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(address,10));

                        //Quando se carrega no marker mostra uma InfoWindow Custom
                        googleMap.setInfoWindowAdapter(new CustomInfoWindowForGoogleMap(getActivity()));


                    }catch (Exception e){
                    }
                }

                // adding on click listener to marker of google maps.
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // on marker click we are getting the title of our marker
                        // which is clicked and displaying it in a toast message.
                        String markerName = marker.getTitle();
                        //Toast.makeText(getActivity(), "Evento Cliked Teste", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

                // adding on click listener to WindowInfo of google maps
                //Vai para o EventsDetailsFragments correspondent
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        String markerName = marker.getTitle();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Events");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                    final String a = snapshot.child("name").getValue().toString();

                                    if (markerName.toString().equals(a)){

                                        final String n1 =  snapshot.child("id").getValue().toString();
                                        Bundle bundle = new Bundle();
                                        FragmentManager fm = getFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        EventsDetailsFragment llf = new EventsDetailsFragment();
                                        bundle.putString("message", n1.toString());
                                        llf.setArguments(bundle);
                                        ft.replace(R.id.container, llf);
                                        ft.addToBackStack("tag");
                                        ft.commit();

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    LatLng getLatLongFromAdress(Context context, String Stradress){
        Geocoder geocoder = new Geocoder(context);
        List<Address> address;
        LatLng latLng = null;
        try {
            address=geocoder.getFromLocationName(Stradress,2);
            if (address==null){
                return null;
            }
            Address loc= address.get(0);
            latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

        }catch (Exception e){

        }

        return latLng;
    }





}