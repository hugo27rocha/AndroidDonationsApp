package com.example.cm07project;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
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


public class ProductsFragment extends Fragment {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userid;
    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<String> list;
    private ArrayList<String> listdisplay;
    private SearchView searchView;
    private Spinner spinner;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        /** Profile **/
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Products");

        userid = user.getUid();


        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_products, null);


        //List view dos produtos que o user tem
        listView = (ListView) root.findViewById(R.id.productsView);
        list = new ArrayList<>();
        listdisplay = new ArrayList<>();
        adapter = new ArrayAdapter<String>(root.getContext(), R.layout.simple_list_item_1, listdisplay);
        listView.setAdapter(adapter);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) root.findViewById(R.id.searchView);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);

        root.findViewById(R.id.itemcreatebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                CreateItemFragment llf = new CreateItemFragment();

                ft.replace(R.id.container, llf);
                ft.addToBackStack("tag");
                ft.commit();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference().child("Products");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                listdisplay.clear();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final String muserid = snapshot.child("userID").getValue().toString();

                    Log.i("meuid",userid);
                    Log.i("idpost",muserid);

                    if (!muserid.equals(userid)){
                        list.add(snapshot.child("item").getValue().toString());
                        listdisplay.add(snapshot.child("item").getValue().toString());
                    }



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

                reference.orderByChild(selectedFromList).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            final String a = snapshot.child("item").getValue().toString();

                            //se o nome do item selecionado for igual a pesquisa entra e encontra o id desse nome do evento no db
                            //e muda de fragmaneto com informacao do id
                            if (selectedFromList.toString().equals(a)){

                                final String n1 =  snapshot.child("id").getValue().toString();
                                Bundle bundle = new Bundle();
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(getActivity(), "Pesquisa exata não encontrada",Toast.LENGTH_LONG).show();
                    loadList();
                }
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchList(newText);
                //    adapter.getFilter().filter(newText);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        spinner = (Spinner) root.findViewById(R.id.category_spinner_search);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String cat =  adapterView.getItemAtPosition(i).toString();
                displayFromCategory(cat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
               //
            }
        });

        return root;

    }

    private void displayFromCategory(String cat) {
        if (cat.equals("Selecionar...")) {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    listdisplay.clear();


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final String muserid = snapshot.child("userID").getValue().toString();

                        if (!muserid.equals(userid)){
                            list.add(snapshot.child("item").getValue().toString());
                            listdisplay.add(snapshot.child("item").getValue().toString());
                        }



                    }
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    listdisplay.clear();


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final String muserid = snapshot.child("userID").getValue().toString();
                        final String category = snapshot.child("category").getValue().toString();


                        if (!muserid.equals(userid) && category.equals(cat)){
                            list.add(snapshot.child("item").getValue().toString());
                            listdisplay.add(snapshot.child("item").getValue().toString());
                        }



                    }

                    if(list.isEmpty()) {
                        Toast.makeText(getContext(), "Não foram encontrados items", Toast.LENGTH_LONG).show();

                    }
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }


    }

    private void loadList() {
        listdisplay.clear();

        for(String s: list) {
            listdisplay.add(s);
        }
        adapter.notifyDataSetChanged();
    }

    private void searchList(String search) {
        listdisplay.clear();

        for(String s: list) {
            if(contains(s,search)) {
                listdisplay.add(s);
            }

        }
        adapter.notifyDataSetChanged();
    }

    private boolean contains(String result, String key) {
        result = result.toLowerCase().replace(" ", "");
        key = key.toLowerCase().replace(" ", "");

        // use a char array
        char[] letters = key.toCharArray();

        // loop through it
        for (int i= 0; i < letters.length; i++) {
            if (i > letters.length - 4) {
                return result.contains(key.substring(i));
            }

            // remove the first part of the string, so that we're searching for
            // letters that exist in a string in order they appear in the key.
            result = result.substring(i);
        }

        return true;
    }

}