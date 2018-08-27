package com.example.anderson.whatsapp.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.anderson.whatsapp.Adapter.ContatosAdapter;
import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.activity.ChatActivity;
import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.example.anderson.whatsapp.helper.RecyclerItemClickListener;
import com.example.anderson.whatsapp.helper.UsuarioFirebase;
import com.example.anderson.whatsapp.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {
    private RecyclerView recyclerView;
    private ContatosAdapter contatosAdapter;
    private ArrayList<Usuario> listContatos = new ArrayList<>();
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase().child("Usuario");
    private ValueEventListener valueEventListener;
    private FirebaseUser user;
    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);
        user = UsuarioFirebase.getUsuarioAtual();
        recyclerView = view.findViewById(R.id.recyclerListaContatos);

        /**Configurar Adapter*/
        contatosAdapter = new ContatosAdapter(listContatos, getActivity());

        /**Configurar RecyclerView*/
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(contatosAdapter);

        /**Evento de clique*/
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chatContato", listContatos.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
        return view;
    }

    public void recuperarContatos(){
        listContatos.clear();
        valueEventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    if (!user.getPhoneNumber().equals(usuario.getCel()))
                    listContatos.add(usuario);
                }
                contatosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        /*String[] numbers = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        for (final String number : numbers){
            valueEventListener = reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot dados : dataSnapshot.getChildren()){
                        Usuario usuario = dados.getValue(Usuario.class);
                        if(usuario.getCel() == number){
                            recuperarContatos();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }*/
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        reference.removeEventListener(valueEventListener);
    }
}
