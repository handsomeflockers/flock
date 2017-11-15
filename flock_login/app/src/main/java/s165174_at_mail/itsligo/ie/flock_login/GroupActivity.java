package s165174_at_mail.itsligo.ie.flock_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    private ArrayList<String> arrayListGroupsInUsersData = new ArrayList<>();


    //get reference to right part of database
    private DatabaseReference groups;
    private DatabaseReference usersGroups;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String TAG = "mmmmmmmmmmmmmmm";

    //To inflate the action bar with a menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //To add action to the Log out option on action bar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                firebaseAuth.signOut();
                goToMain();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Log.d(TAG, "onCreate: CURRENTLY IN ONCREATE");


        //To show action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);


        buttonAddGroup = (Button) findViewById(R.id.buttonAddGroup);
        buttonLogout= (Button) findViewById(R.id.buttonLogout);
        editTextAddGroupName = (EditText) findViewById(R.id.editTextGroupName);
        listViewGroups = (ListView) findViewById(R.id.listViewGroups);

        //array list for active groups
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListOfGroups);
        //listViewGroups.setAdapter(arrayAdapter);
        arrayListOfGroupObjects.clear();
        groupArrayAdapter = new ArrayAdapter<Group>(this, android.R.layout.simple_list_item_1, arrayListOfGroupObjects);
        listViewGroups.setAdapter(groupArrayAdapter);

        usersGroups = FirebaseDatabase.getInstance().getReference("flock-login/users");

        //send token to db (for receiving notifications)
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            mUser.getToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                Log.d(TAG, "token = " + idToken);
                                Map<String, Object> map = new HashMap<String, Object>();
                                //map.put("token", idToken);
                                map.put("token", FirebaseInstanceId.getInstance().getToken());
                                usersGroups.child(mUser.getUid()).child("token").setValue(map);
                            } else {
                                Log.d(TAG, "no token");
                            }
                        }
                    });
        }

        //set database routes
        groups = FirebaseDatabase.getInstance().getReference("groups");
        //usersGroups = FirebaseDatabase.getInstance().getReference("flock-login/users/"+ firebaseAuth.getCurrentUser().getUid() + "/groups");

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
                    goToMain();
                }
                // ...
            }
        };

        //ensure token is up-to-date
        Map<String, Object> map = new HashMap<>();
        map.put("token", FirebaseInstanceId.getInstance().getToken());
        usersGroups.child(firebaseAuth.getCurrentUser().getUid()).updateChildren(map);

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

        //get the list of groups from users/uid/groups
        //this'll allow us to retrieve only the groups associated with the current user
        usersGroups.child(firebaseAuth.getCurrentUser().getUid()).child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear current array
                arrayListGroupsInUsersData.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    //add each child value to array
                    arrayListGroupsInUsersData.add(snapshot.getKey().toString());
                    Log.d(TAG, "onDataChange: " + arrayListGroupsInUsersData);
                }
                //now we can use the groups array to retrieve each group
                arrayListOfGroupObjects.clear();
                for (final String group: arrayListGroupsInUsersData){
                    groups.child("/" + group).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            // SUCCESS!
                            Group g = snapshot.getValue(Group.class);
                            g.setKey(group);
                            arrayListOfGroupObjects.add(g);
                            Log.d(TAG, "onDataChange: " + g.getGroupName());
                            groupArrayAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            // error callback is not called
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






/*

        //THIS DOESN'T LET US SET EFFECTIVE RULES IN FIREBASE
        //any time the groups part of the db changes
        groups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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
*/


        //when an individual group is clicked
        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = arrayListOfGroupObjects.get(position);
                Intent intent = new Intent(getApplicationContext(), GroupHomeActivity.class);
                intent.putExtra("groupId", String.valueOf(group.getKey()));
                intent.putExtra("groupName", String.valueOf(group.getGroupName()));
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
