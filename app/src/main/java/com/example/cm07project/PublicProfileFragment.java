package com.example.cm07project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PublicProfileFragment extends Fragment {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userid;
    private Button sendmsgbutton;
    private TextView nameView;

    public PublicProfileFragment(String userid){
        this.userid = userid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_public_profile, container, false);
        this.nameView = (TextView) root.findViewById(R.id.namevalue);
        this.sendmsgbutton = root.findViewById(R.id.sendmsgbutton);
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userprofile = snapshot.getValue(User.class);

                if (userprofile != null) {
                    String firstname = userprofile.firstname + " " + userprofile.lastname;
                    nameView.setText(firstname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //List view dos produtos que o user tem
        ListView listView = (ListView) root.findViewById(R.id.myproductsView);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(root.getContext(), R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Products");

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final String muserid = snapshot.child("userID").getValue().toString();

                    if (muserid.equals(userid)){
                        list.add(snapshot.child("item").getValue().toString());
                    }
                }


                if(list.isEmpty()) {
                    list.add("Ainda não tem itens");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {

            final String selectedFromList = (String) listView.getItemAtPosition(position);
            reference2.orderByChild(selectedFromList).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final String a = snapshot.child("item").getValue().toString();

                        if (selectedFromList.toString().equals(a)){
                            final String n1 =  snapshot.child("id").getValue().toString();
                            Bundle bundle = new Bundle();
                            FragmentManager fm = getFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ItemDetailsFragment llf = new ItemDetailsFragment();
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
        });

        sendmsgbutton.setOnClickListener((view -> {
            final FragmentManager fm = getActivity().getSupportFragmentManager();
            ChatFragment chatFragment =
                    new ChatFragment(this.userid, this.nameView.getText().toString());
            fm.beginTransaction().replace(R.id.container, chatFragment)
                    .addToBackStack("Profile").commit();
        }));

        return root;
    }
}