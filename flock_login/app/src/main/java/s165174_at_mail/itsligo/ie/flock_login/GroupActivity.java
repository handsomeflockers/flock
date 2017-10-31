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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//gjhkl
public class GroupActivity extends AppCompatActivity {

    private Button buttonAddGroup;
    private Button buttonLogout;
    private EditText editTextAddGroupName;
    private ListView listViewGroups;


    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayListOfGroups = new ArrayList<>();
    private ArrayList<Group> arrayListOfGroupObjects = new ArrayList<>();
    private ArrayAdapter<Group> groupArrayAdapter;


    //get reference to right part of database
    private DatabaseReference groups;

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
        //listViewGroups.setAdapter(arrayAdapter);

        groupArrayAdapter = new ArrayAdapter<Group>(this, android.R.layout.simple_list_item_1, arrayListOfGroupObjects);
        listViewGroups.setAdapter(groupArrayAdapter);

        groups = FirebaseDatabase.getInstance().getReference("groups");

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




        //any time the groups part of the db changes
        groups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
/*
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                //Set<Group> groupSet = new HashSet<Group>();
                while(i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                    //set.add((String)dataSnapshot.child("Group Name").getValue());
                    //snapshot.child("title").getValue();
                    //groupSet.add((Group)i.next());
                }
                arrayListOfGroups.clear();
                arrayListOfGroups.addAll(set);
                //arrayListOfGrouupObjects.addAll(groupSet);
                //Log.d("mmmmmmmmmmmmmmmmmmmm", "groupset: " + groupSet);
                arrayAdapter.notifyDataSetChanged();
*/


                arrayListOfGroups.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    //Group group = snapshot.getValue(Group.class);
                    //Log.d("mmmmmmmmmmmmmmmmm", group.getGroupName());
                    Group g = snapshot.getValue(Group.class);
                    g.setKey((snapshot.getKey()));
                    Log.d("mmmmmmmmmmmmmmmmmmmmm", g.getGroupName());
                    arrayListOfGroupObjects.add(g);
                    //Log.d("mmmmmmmmmmmmmmmmmmmmm", g.members.toString());
                    arrayListOfGroups.add(g.getGroupName());

                }

                groupArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //when an individual group is clicked
        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = arrayListOfGroupObjects.get(position);
                Intent intent = new Intent(getApplicationContext(), GroupHomeActivity.class);
                intent.putExtra("groupId", String.valueOf(group.getKey()));
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
