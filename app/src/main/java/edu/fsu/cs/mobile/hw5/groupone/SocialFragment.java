package edu.fsu.cs.mobile.hw5.groupone;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFragment extends Fragment {

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference messsageRef=db.collection("Users");
    private MessageAdapter adapter;
    private DocumentReference numRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public SocialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_social, container, false);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        setUpRecyclerView(v, getContext());
        return v;
    }

    private void setUpRecyclerView(View v, final Context c) {
        Query query=messsageRef.document(currentUser.getUid()).collection("Messages")
                .orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Message> options=new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();
        adapter=new MessageAdapter(options);

        RecyclerView recyclerView=v.findViewById(R.id.social_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(c));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                        DocumentReference docRef=documentSnapshot.getReference();
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc=task.getResult();
                                String phoneNum=new String(doc.getString("number"));
                                //send text message
                                Uri uri = Uri.parse("smsto:" + phoneNum);
                                Intent smsText = new Intent(Intent.ACTION_SENDTO, uri);

                                //this code below starts messaging app but right now it crashes the whole app

                smsText.putExtra("sms_body", "Sorry, I'm busy right now studying at " +
                        "Strozier");
                startActivity(smsText);

                            }
                        });
                            }
                        });
                         }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
