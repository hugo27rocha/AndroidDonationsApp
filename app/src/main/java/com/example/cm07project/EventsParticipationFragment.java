package com.example.cm07project;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventsParticipationFragment extends Fragment {
    // Firebase
    private DatabaseReference reference;
    private DatabaseReference reference2;
    private FirebaseUser user;

    // Views
    private EditText edit;
    private Button addd;
    private ListView listView;

    // Logic variables
    private String userid;
    private String userEmail;
    private List<Pair<String,Integer>> emList;
    private boolean isParticipating;
    private boolean firstTime;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_events__participation, null);
        firstTime = false;

        addd = root.findViewById(R.id.participar);

        String strtext = getArguments().getString("message");

        this.listView = (ListView) root.findViewById(R.id.listView);
        final ArrayList<String> list = new ArrayList<>();
        emList = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(root.getContext(), R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("People");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                emList.clear();
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final String a = snapshot.child("eventid").getValue().toString();
                    if (strtext.toString().equals(a)){
                        final String n1 =  snapshot.child("name").getValue().toString();
                        list.add(n1);

                        int index = list.size()-1;
                        emList.add(new Pair<>(snapshot.child("email").getValue().toString(),index));

                    }

                    //list.add(a);
                    //list.add(snapshot.getValue().toString());

                }
                profileListenersInit();
                adapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /** Profile **/
        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();

        addd.setOnClickListener(v-> {
            if(!isParticipating){
                reference2.child(userid).addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userprofile = snapshot.getValue(User.class);
                        if (userprofile != null) {
                            String firstname = userprofile.firstname;
                            String lastname = userprofile.lastname;
                            String email = userprofile.email;
                            String fullname = firstname +" "+lastname;

                            People event = new People(strtext.toString(),fullname,email);

                            FirebaseDatabase.getInstance().getReference("People")
                                    //.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .push()
                                    .setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getActivity(), "Participação registada", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(getActivity(), "Participação Failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                isParticipating = !isParticipating;
                addd.setText("Remover");
            } else {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            final String a = snapshot.child("eventid").getValue().toString();
                            final String e = snapshot.child("email").getValue().toString();
                            if (strtext.toString().equals(a) && userEmail.equals(e)){
                                snapshot.getRef().removeValue();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                isParticipating = !isParticipating;
                addd.setVisibility(View.INVISIBLE);

            }

        });


        return root;
    }

    private void profileListenersInit() {
        if(emList.isEmpty()){
            return;
        }

        String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("Users");
        reference3.orderByChild("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User tempU = snapshot.getValue(User.class);
                    String tempEmail = tempU.getEmail();

                    for(int i = 0; i < emList.size(); i++){
                        Pair<String,Integer> p = emList.get(i);

                        if (tempEmail.equals(p.first)) {
                            View childView = listView.getChildAt(p.second);
//                            emList.remove(i);
                            String uid = snapshot.getKey();

                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            if(!uid.equals(myid)){

                                childView.setOnClickListener(view -> {
                                    PublicProfileFragment ppFragment = new PublicProfileFragment(uid);
                                    fm.beginTransaction().replace(R.id.container, ppFragment)
                                            .addToBackStack("Profile").commit();
                                });
                            } else {
                                userEmail = tempEmail;
                                if(firstTime){
                                    isParticipating = true;
                                    addd.setText("Remove");
                                    firstTime = !firstTime;
                                }
                                childView.setOnClickListener(view -> {
                                    for(int k = 0; k < fm.getBackStackEntryCount(); k++) {
                                        fm.popBackStack();
                                    }
                                    MainActivity activity = (MainActivity) getActivity();
                                    activity.goToProfile();
                                });
                            }
                            break;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
