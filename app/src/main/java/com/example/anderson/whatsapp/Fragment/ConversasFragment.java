package com.example.anderson.whatsapp.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.anderson.whatsapp.Adapter.ConversasAdapter;
import com.example.anderson.whatsapp.R;
import com.example.anderson.whatsapp.activity.ChatActivity;
import com.example.anderson.whatsapp.config.ConfiguracaoFirebase;
import com.example.anderson.whatsapp.helper.RecyclerItemClickListener;
import com.example.anderson.whatsapp.helper.UsuarioFirebase;
import com.example.anderson.whatsapp.model.Conversa;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {
    private RecyclerView conversas;
    private List<Conversa> conversaList = new ArrayList<>();
    private ConversasAdapter conversasAdapter;
    private DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference conversaRef;
    private ChildEventListener childEventListener;

    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        //Config adapter
        conversasAdapter = new ConversasAdapter(conversaList, getContext());

        //Confg recycler
        conversas = view.findViewById(R.id.recyclerConversas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        conversas.setLayoutManager(layoutManager);
        conversas.setHasFixedSize(true);
        conversas.setAdapter(conversasAdapter);

        conversas.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), conversas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Conversa conversaSelecionada = conversaList.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chatContato", conversaSelecionada.getUsuario());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        conversaRef = reference.child("Conversas"). child(UsuarioFirebase.getIdUsuario());

        return view;
    }

    public void recuperaConversas(){
        childEventListener = conversaRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Conversa conversa = dataSnapshot.getValue(Conversa.class);
                conversaList.add(conversa);
                conversasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperaConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversaRef.removeEventListener(childEventListener);
    }
}
