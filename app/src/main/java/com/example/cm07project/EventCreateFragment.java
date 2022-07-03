package com.example.cm07project;

import android.app.usage.UsageEvents;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class EventCreateFragment extends Fragment {

    private EditText mname;
    private EditText mdesc;

    private EditText mdata;
    private EditText mrua;
    private EditText mdist;
    private Button create;
    private DatabaseReference reference;

    String key,id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_event_create, null);

        create = root.findViewById(R.id.createEventButton);

        mname = root.findViewById(R.id.eventnameedit);
        mdesc = root.findViewById(R.id.eventdescedit);

        mdata = root.findViewById(R.id.editTextDate);
        mrua = root.findViewById(R.id.rua_edit);
        mdist = root.findViewById(R.id.district_edit);
        reference= FirebaseDatabase.getInstance().getReference("Events");
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String n1 = mname.getText().toString();
                String n2 = mdesc.getText().toString();

                String n4 = mdata.getText().toString();
                String n5 = mrua.getText().toString();
                String n6 = mdist.getText().toString();
                String n7 = "Portugal";
                String n8 = UUID.randomUUID().toString();


                String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(id!=null)
                {
                    n8=id;
                }
                Evento event = new Evento(n1,n2,n4,n5,n6,n7,n8,uid);

                DatabaseReference reference1=   FirebaseDatabase.getInstance().getReference("Events");

                if(key!=null)
                {
                    reference1=reference1.child(key);
                }
                else
                    reference1=reference1.push();

                reference1.setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Evento registered", Toast.LENGTH_SHORT).show();
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            EventsFragment llf = new EventsFragment();
                            ft.replace(R.id.container, llf);
                            ft.addToBackStack("tag");
                            ft.commit();
                        }else {
                            Toast.makeText(getActivity(), "Evento Failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });



        if(getArguments()!=null)
        {
            String eventName=getArguments().getString("Name");

            reference.orderByChild(eventName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        final String a = snapshot.child("name").getValue().toString();

                        //se o nome do item selecionado for igual a pesquisa entra e encontra o id desse nome do evento no db
                        //e muda de fragmaneto com informacao do id
                        if (eventName.toString().equals(a)){

                            id =  snapshot.child("id").getValue().toString();
                            final String name =  snapshot.child("name").getValue().toString();
                            final String des =  snapshot.child("des").getValue().toString();
                            //final String org =  snapshot.child("org").getValue().toString();
                            final String date =  snapshot.child("date").getValue().toString();
                            final String streetadress =  snapshot.child("streetadress").getValue().toString();
                            final String state =  snapshot.child("state").getValue().toString();


                            mname.setText(name);
                            mdesc.setText(des);
                            mdata.setText(date);
                            mrua.setText(streetadress);
                            mdist.setText(state);


                            key=snapshot.getKey();
                            create.setText("Update Event");




                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


        return  root;
    }
}
