
package com.example.cm07project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class EventsFragment extends Fragment {

    private ListView listView;
    private FirebaseUser user;
    private DatabaseReference reference;
    private Button v1;
    private Button event;

    ArrayList<String> listEventIds;
    ArrayAdapter adapterRegisteredEvents;
    ArrayList<String> listRegisteredEvents;
    ArrayList<String> list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_events, null);

        ListView listView = (ListView) root.findViewById(R.id.listView);
        ListView listViewMyEvents = (ListView) root.findViewById(R.id.listMyEvents);
        ListView listViewRegisteredEvents = (ListView) root.findViewById(R.id.listRegisterEvents);
        list = new ArrayList<>();
        final ArrayList<String> listmyEvents = new ArrayList<>();


        listEventIds = new ArrayList<>();
        listRegisteredEvents = new ArrayList<>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(root.getContext(), R.layout.simple_list_item_1, list);
        final ArrayAdapter<String> adapterMyEvents = new ArrayAdapter<String>(root.getContext(), R.layout.simple_list_item_1, listmyEvents);
        adapterRegisteredEvents = new ArrayAdapter<String>(root.getContext(), R.layout.simple_list_item_1, listRegisteredEvents);


        listViewMyEvents.setAdapter(adapterMyEvents);
        listView.setAdapter(adapter);
        listViewRegisteredEvents.setAdapter(adapterRegisteredEvents);

        final TextView but = (TextView) root.findViewById(R.id.eventvalue);
        //final Button v1 = root.findViewById(R.id.eventdetails1);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Events");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                list.clear();
                listmyEvents.clear();
                listEventIds.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    final String a = snapshot.child("name").getValue().toString();
                    final String id = snapshot.child("id").getValue().toString();
                    listEventIds.add(id);
                    if(snapshot.hasChild("uid"))
                    {
                        if(snapshot.child("uid").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        {
                            listmyEvents.add(a);
                        }
                    }
                    list.add(a);


                }

                adapterMyEvents.notifyDataSetChanged();
                adapter.notifyDataSetChanged();

                loadRegisteredEvents();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        listViewMyEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                new AlertDialog.Builder(getActivity())
                        .setMessage("Select Action")
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {


                                //valor do nome da lista Exemplo:Natal
                                final String selectedFromList = (String) listViewMyEvents.getItemAtPosition(position);

                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();

                                EventCreateFragment llf = new EventCreateFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("Name",selectedFromList);
                                ft.replace(R.id.container, llf);
                                llf.setArguments(bundle);
                                ft.addToBackStack("tag");
                                ft.commit();

                            }
                        })
                        .setNegativeButton("Show Details", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                //valor do nome da lista Exemplo:Natal
                                final String selectedFromList = (String) listViewMyEvents.getItemAtPosition(position);

                                reference.orderByChild(selectedFromList).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                            final String a = snapshot.child("name").getValue().toString();

                                            //se o nome do item selecionado for igual a pesquisa entra e encontra o id desse nome do evento no db
                                            //e muda de fragmaneto com informacao do id
                                            if (selectedFromList.toString().equals(a)){

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
                                                //Toast.makeText(getActivity(), "Value: "+n1.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



                            }
                        }).show();

            }

        });

        listViewRegisteredEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //valor do nome da lista Exemplo:Natal
                final String selectedFromList = (String) listViewRegisteredEvents.getItemAtPosition(position);

                reference.orderByChild(selectedFromList).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            final String a = snapshot.child("name").getValue().toString();

                            //se o nome do item selecionado for igual a pesquisa entra e encontra o id desse nome do evento no db
                            //e muda de fragmaneto com informacao do id
                            if (selectedFromList.toString().equals(a)){

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
                                //Toast.makeText(getActivity(), "Value: "+n1.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });


        /** Vai fzaer com que cada item se torne clickable**/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //valor do nome da lista Exemplo:Natal
                final String selectedFromList = (String) listView.getItemAtPosition(position);

                reference.orderByChild(selectedFromList).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            final String a = snapshot.child("name").getValue().toString();

                            //se o nome do item selecionado for igual a pesquisa entra e encontra o id desse nome do evento no db
                            //e muda de fragmaneto com informacao do id
                            if (selectedFromList.toString().equals(a)){

                                final String n1 =  snapshot.child("id").getValue().toString();
                                Bundle bundle = new Bundle();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                EventsDetailsFragment llf = new EventsDetailsFragment();
                                bundle.putString("message", n1.toString());
                                llf.setArguments(bundle);
                                ft.replace(R.id.container, llf);
                                ft.addToBackStack("tag");
                                ft.commit();
                                //Toast.makeText(getActivity(), "Value: "+n1.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });



        final Button event = root.findViewById(R.id.eventcreatbutton);



        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                EventCreateFragment llf = new EventCreateFragment();

                ft.replace(R.id.container, llf);
                ft.addToBackStack("tag");
                ft.commit();
            }
        });

        return root;
    }

    private void loadRegisteredEvents() {



        FirebaseDatabase.getInstance().getReference("People").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listRegisteredEvents.clear();
                for (int i = 0, listEventIdsSize = listEventIds.size(); i < listEventIdsSize; i++) {
                    String eventId = listEventIds.get(i);
                    String eventName=list.get(i);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        final String a = snapshot.child("eventid").getValue().toString();
                        if (eventId.toString().equals(a)) {
                            //list.add(a);
                            String email = snapshot.child("email").getValue().toString();

                            email = email.trim().toLowerCase();
                            if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equalsIgnoreCase(email)) {

                                if(!listRegisteredEvents.contains(eventName))
                                    listRegisteredEvents.add(eventName);
                            }


                        }


                    }


                }

                adapterRegisteredEvents.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}