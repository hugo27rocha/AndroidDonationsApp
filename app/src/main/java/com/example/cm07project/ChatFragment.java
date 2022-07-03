package com.example.cm07project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.List;


public class ChatFragment extends Fragment {

    private final String otherUid;
    private final String otherName;
    private List<Chat> mchat;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private MessageAdapter messageAdapter;


    ImageButton btn_send;
    EditText text_send;
    RecyclerView recyclerView;

    public ChatFragment(String otherUid, String name) {
        this.otherUid = otherUid;
        this.otherName = name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);;
        TextView tv = (TextView) v.findViewById(R.id.username);
        tv.setText(this.otherName);
        this.btn_send = v.findViewById(R.id.btn_send);
        this.text_send = v.findViewById(R.id.text_send);
        this.recyclerView = v.findViewById(R.id.recycler_view);
        this.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        this.recyclerView.setLayoutManager(linearLayoutManager);

        btn_send.setOnClickListener(view -> {
            String msg = text_send.getText().toString();
            if(!msg.isEmpty()){
                sendMessage(fuser.getUid(),this.otherUid, msg);
            } else {
                Toast.makeText(getContext(),
                        "You cant send an empty message",Toast.LENGTH_SHORT).show();
            }
            text_send.setText("");
        });

        tv.setOnClickListener(view -> {
            int index = getActivity().getFragmentManager().getBackStackEntryCount() - 1;
            if (index > 0) {
                FragmentManager.BackStackEntry backEntry = getActivity()
                        .getSupportFragmentManager().getBackStackEntryAt(index);
                String tag = backEntry.getName();
                if(!tag.equals("Profile")){
                    goToProfile();
                }
            } else {
                goToProfile();
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                readMesagges(fuser.getUid(), otherUid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }

    private void goToProfile(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PublicProfileFragment ppFragment = new PublicProfileFragment(this.otherUid);
        fm.beginTransaction().replace(R.id.container, ppFragment)
                .addToBackStack("EventParticipant").commit();
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);

        // adicionar user ao chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(otherUid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(otherUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMesagges(final String myid, final String userid){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(getContext(), mchat);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}