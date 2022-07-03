package com.example.cm07project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StockFragment extends Fragment {

    private ListView listView;
    private FirebaseUser user;
    private DatabaseReference reference;

    private EditText edit;
    private Button addd;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_stock, null);
        edit = root.findViewById(R.id.edit);
        addd = root.findViewById(R.id.addd);

       // FirebaseDatabase.getInstance().getReference("Market").child("3").setValue("asb");
        //FirebaseDatabase.getInstance().getReference("Market").child("item").push().setValue("hugo");
        addd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_name = edit.getText().toString();
                //FirebaseDatabase.getInstance().getReference("Market").child("item").push().setValue(txt_name);
                Market event = new Market("0",txt_name);


                FirebaseDatabase.getInstance().getReference("Market")
                        //.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .push()
                        .setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Doação registered", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getActivity(), "Doação Failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


        ListView listView = (ListView) root.findViewById(R.id.listView);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(root.getContext(), R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Market");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Market");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final String a = snapshot.child("item").getValue().toString();
                    //list.add(snapshot.getValue().toString());
                    list.add(a);

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return root;
    }
}