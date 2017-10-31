package s165174_at_mail.itsligo.ie.flock_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
//gjhkl
public class GroupActivity extends AppCompatActivity {

    private Button buttonAddGroup;
    private Button buttonLogout;
    private EditText editTextAddGroupName;
    private ListView listViewGroups;


    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayListOfGroups = new ArrayList<>();

    //get reference to right part of database
    private DatabaseReference groups = FirebaseDatabase.getInstance().getReference("groups");

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String TAG = "GroupActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        buttonAddGroup = (Button) findViewById(R.id.buttonAddGroup);
        buttonLogout= (Button) findViewById(R.id.buttonLogout);
        editTextAddGroupName = (EditText) findViewById(R.id.editTextGroupName);
        listViewGroups = (ListView) findViewById(R.id.listViewGroups);

        //array list for active groups
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListOfGroups);
        listViewGroups.setAdapter(arrayAdapter);

        //get logged in user
        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        buttonAddGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                goToCreateNewGroup();
                //firebaseAuth.signOut();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                firebaseAuth.signOut();
                goToMain();
            }
        });





        groups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while(i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                    //set.add((String)dataSnapshot.child("Group Name").getValue());
                    //snapshot.child("title").getValue();
                }
                arrayListOfGroups.clear();
                arrayListOfGroups.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), GroupHomeActivity.class);
                intent.putExtra("groupId", ((TextView)view).getText().toString());
                startActivity(intent);
            }
        });

    }

    private void goToMain(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void goToCreateNewGroup(){
        Intent i = new Intent(this, CreateNewGroupActivity.class);
        startActivity(i);
    }







}
