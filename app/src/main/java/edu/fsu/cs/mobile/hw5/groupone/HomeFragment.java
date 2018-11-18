package edu.fsu.cs.mobile.hw5.groupone;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private Map<String, Object> message=new HashMap<>();

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        final FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
        final TextView roomNum = (TextView) v.findViewById(R.id.roomInputStr);
        final TextView studySubject = (TextView) v.findViewById(R.id.subjectInputStr);
        final RadioButton yes=v.findViewById(R.id.yesRadioBtn);
        final RadioButton no=v.findViewById(R.id.noRadioBtn);
        Button update=v.findViewById(R.id.updateBtn);

        roomNum.setError(null);
        studySubject.setError(null);
        yes.setError(null);
        no.setError(null);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(studySubject.getText().toString().matches("")){
                    studySubject.setError("Enter Study subject");
                }
                else{
                    db.collection("Users").document(currentUser.getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc=task.getResult();
                            StringBuilder sb = new StringBuilder("");

                            sb.append("Im studying " + studySubject.getText().toString());
                            sb.append(" at " + doc.getString("location"));

                            if(yes.isChecked()){//they do want to include room #
                                sb.append(" in room " + roomNum.getText().toString() + ".\n");
                                sb.append("You can study with me if you want.");

                            }
                            String response = sb.toString();
                            message.put("message", response);
                            db.collection("Users").document(currentUser.getUid()).update(message);
                        }
                    });

                }
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.logout_button, menu);
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }

    private void logout() {
        mAuth.signOut();
        Intent myIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(myIntent);
        getActivity().finish();
    }
}
