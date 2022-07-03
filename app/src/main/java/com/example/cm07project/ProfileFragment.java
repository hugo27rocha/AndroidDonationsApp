package com.example.cm07project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userid;
    //TextView imageView;
    private Button logout;
    private Button addbtn;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        /** Profile **/
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        userid = user.getUid();


        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_profile, null);
        final TextView nameView = (TextView) root.findViewById(R.id.namevalue);
        final TextView emailView = (TextView) root.findViewById(R.id.emailvalue);
        logout = root.findViewById(R.id.signOut);

        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userprofile = snapshot.getValue(User.class);

                if (userprofile != null) {
                    String firstname = userprofile.firstname + " " + userprofile.lastname;
                    String email = userprofile.email;
                    nameView.setText(firstname);
                    emailView.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
               // Toast.makeText(ProfileFragment.this, "Profile Failed:" , Toast.LENGTH_SHORT).show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), AnimatedLoginActivity.class);
                startActivity(i);

            }
        });

        addbtn = root.findViewById(R.id.addProduct);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                CreateItemFragment llf = new CreateItemFragment();

                ft.replace(R.id.container, llf);
                ft.addToBackStack("tag");
                ft.commit();

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
                    list.add("Não tem itens!");
                }


                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //valor do nome da lista Exemplo:Natal
                final String selectedFromList = (String) listView.getItemAtPosition(position);

                reference2.orderByChild(selectedFromList).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            final String a = snapshot.child("item").getValue().toString();

                            if (selectedFromList.toString().equals(a)){

                                final String n1 =  snapshot.child("id").getValue().toString();
                                Bundle bundle = new Bundle();
                                if(getActivity() != null) {
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
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

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });
        return root;

    }


}
/*
    private void gatherMyProducts() {
        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userprofile = snapshot.getValue(User.class);

                if (userprofile != null) {
                    final String email = userprofile.email;

                    / Depois de ter o email do userid vai ver a tabela People aonde tem esse email para ver
                     * a que evento_id corresponde/
                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("People");
                    reference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            list.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                final String meventid = snapshot.child("eventid").getValue().toString();
                                final String mmail = snapshot.child("email").getValue().toString();
                                Se o mail do user e o email da pessoa que participa no
                                        * evento sao iguais -> vai buscar a tabela Event o nome do evento
                                        * para mostrar na listView/
                                if (mmail.equals(email)){
                                    //list.add(meventid);
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Events");
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            //list.clear();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                                final String a = snapshot.child("name").getValue().toString();
                                                final String mid = snapshot.child("id").getValue().toString();
                                                if (meventid.equals(mid)){
                                                    list.add(a);
                                                }
                                            }
                                            adapter.notifyDataSetChanged();

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }



                            }
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Toast.makeText(ProfileFragment.this, "Profile Failed:" , Toast.LENGTH_SHORT).show();
            }
        });
    }
*/

/*
        DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference("Events");
Vai fzaer com que cada item se torne clickable
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //valor do nome da lista Exemplo:Natal
                final String selectedFromList = (String) listView.getItemAtPosition(position);

                reference4.orderByChild(selectedFromList).addValueEventListener(new ValueEventListener() {
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
                                                                       }















        /*

        /** 1o vai ver o user id com a tabela Users para saber a info do mail**/
      /*  reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userprofile = snapshot.getValue(User.class);

                if (userprofile != null) {
                    String firstname = userprofile.firstname;
                    String lastname = userprofile.lastname;
                    final String email = userprofile.email;
                    / Depois de ter o email do userid vai ver a tabela People aonde tem esse email para ver
                     * a que evento_id corresponde/
                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("People");
                    reference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            list.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                final String meventid = snapshot.child("eventid").getValue().toString();
                                final String mmail = snapshot.child("email").getValue().toString();
                                Se o mail do user e o email da pessoa que participa no
                                 * evento sao iguais -> vai buscar a tabela Event o nome do evento
                                 * para mostrar na listView/
                                if (mmail.equals(email)){
                                    //list.add(meventid);
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Events");
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            //list.clear();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                                final String a = snapshot.child("name").getValue().toString();
                                                final String mid = snapshot.child("id").getValue().toString();
                                                if (meventid.equals(mid)){
                                                    list.add(a);
                                                }
                                            }
                                            adapter.notifyDataSetChanged();

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }



                            }
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Toast.makeText(ProfileFragment.this, "Profile Failed:" , Toast.LENGTH_SHORT).show();
            }
        });*/

//return inflater.inflate(R.layout.fragment_profile, container, false);