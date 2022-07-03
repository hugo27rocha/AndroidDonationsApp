package com.example.cm07project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventsDetailsFragment extends Fragment {

    private DatabaseReference reference;
    private Button donation;
    private Button participar;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_events_details, null);
        final Button donation = root.findViewById(R.id.donationbutton);
        final Button participar = root.findViewById(R.id.participarbutton);

        //Vai buscar o id do evento passado pelo EventsFragment
        String strtext = getArguments().getString("message");

        if (strtext == "1"){
            //EVENTS 1
        }

        ListView listView = (ListView) root.findViewById(R.id.listView);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(root.getContext(), R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    final String a = snapshot.child("id").getValue().toString();
                    if (strtext.toString().equals(a)){
                        //list.add(a);
                        final String n1 = "Nome do evento: " + snapshot.child("name").getValue().toString();
                        final String n3 = "Data: " + snapshot.child("date").getValue().toString();
                        final String n4 = "Descrição: " + snapshot.child("des").getValue().toString();
                        final String n5 = "Localização: " + snapshot.child("streetadress").getValue().toString()
                                +", " + snapshot.child("state").getValue().toString();
                        list.add(n1);
                        list.add(n3);
                        list.add(n4);
                        list.add(n5);

                        // TODO:add username
                    }

                    //list.add(a);
                    //list.add(snapshot.getValue().toString());

                }

                adapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Bundle bundle = new Bundle();

        donation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                EventsDonationFragment llf = new EventsDonationFragment();
                bundle.putString("message", strtext.toString());
                llf.setArguments(bundle);
                ft.replace(R.id.container, llf);
                ft.addToBackStack("tag");
                ft.commit();
            }
        });

        participar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                EventsParticipationFragment llf = new EventsParticipationFragment();
                bundle.putString("message", strtext.toString());
                llf.setArguments(bundle);
                ft.replace(R.id.container, llf);
                ft.addToBackStack("tag");
                ft.commit();
            }
        });

        return root;
       // return inflater.inflate(R.layout.fragment_messages, container, false);
    }
}
