package s165174_at_mail.itsligo.ie.flock_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddMemberToExistingGroupActivity extends AppCompatActivity {

    private Button buttonDone;
    private Button buttonAddNumber;
    private EditText editTextAddPhoneNumber;
    private ListView listViewAddedMembers;

    private ArrayAdapter<User> arrayAdapter;
    private ArrayList<User> arrayListOfMembers = new ArrayList<>();
    private ArrayList<String> arrayListOfMembersString = new ArrayList<>();

    //get reference to root of database
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    //get reference to groups part of database
    private DatabaseReference groups = FirebaseDatabase.getInstance().getReference("groups");
    //get ref to users in db
    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("flock-login").child("users");

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String groupId;
    String groupName;

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
        setContentView(R.layout.activity_add_member_to_existing_group);

        //get info from previous activity
        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null)
        {
            groupId =(String) bundle.get("groupId");
            groupName = (String) bundle.get("groupName");
            Log.d("mmmmmmmmmmmmmmm", groupId);
        }

        //To show action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add people to your group");

        buttonDone = (Button) findViewById(R.id.buttonFinished);
        buttonAddNumber = (Button) findViewById(R.id.buttonAddNumber);
        editTextAddPhoneNumber = (EditText) findViewById(R.id.editTextAddPhoneNumber);
        listViewAddedMembers = (ListView) findViewById(R.id.listViewAddedMembers);

        //get logged in user
        firebaseAuth = FirebaseAuth.getInstance();

        //link array to listview
        arrayAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, arrayListOfMembers);
        listViewAddedMembers.setAdapter(arrayAdapter);

        //get members already in group
        groups.child(groupId).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("mmmmmmmmmmmmmmmm", snapshot.toString());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    String ps = (String)postSnapshot.getKey();
                    Log.d("mmmmmmmmmmmmmmmm", ps);
                    arrayListOfMembersString.add(ps);
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "Error contacting database", Toast.LENGTH_SHORT).show();

            }

        });
        getUsers();

        //search for new member
        buttonAddNumber.setOnClickListener(new View.OnClickListener(){
            //ADD ANOTHER PERSON TO THE GROUP
            @Override
            public void onClick(View view){
                Log.d("mmmmmmmmmmmmmmmmmmm", "onClick: ");
                String phoneNumber = editTextAddPhoneNumber.getText().toString();
                if(TextUtils.isEmpty(phoneNumber)){
                    //phone number is empty
                    Toast.makeText(getApplicationContext(), "Please enter a phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("mmmmmmmmmmmmmmmmmmm", "phone number fine");
                //if phone number isn't empty, try find a match in db
                Query queryRef = users.orderByChild("phoneNumber").equalTo(phoneNumber);
                queryRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("mmmmmmmmmmmmmmmmmmm", "bzzzzt");
                        if (dataSnapshot.exists()) {
                            Log.d("mmmmmmmmmmmmmmmmmmmm", "onDataChange: found one");
                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                User foundPhoneNumberUser = new User();
                                foundPhoneNumberUser.uid = (String) issue.child("uid").getValue();
                                foundPhoneNumberUser.name = (String) issue.child("name").getValue();
                                foundPhoneNumberUser.phoneNumber = (String) issue.child("phoneNumber").getValue();
                                if (!Arrays.asList(arrayListOfMembers).contains(foundPhoneNumberUser)) {
                                    arrayListOfMembers.add(foundPhoneNumberUser);
                                    editTextAddPhoneNumber.setText("");
                                    //add to users in db
                                    Map<String, Object> member = new HashMap<>();
                                    member.put(foundPhoneNumberUser.uid, true);
                                    groups.child(groupId).child("members").updateChildren(member);
                                    //add to groups in db
                                    Map<String, Object> group = new HashMap<>();
                                    group.put(groupId, true);
                                    users.child(foundPhoneNumberUser.uid).child("groups").updateChildren(group);
                                    //send a message to say who has been added
                                    messageGroupAboutNewMember(foundPhoneNumberUser.name);
                                }
                                //arrayListOfMembers.add(foundPhoneNumberUser);
                                arrayAdapter.notifyDataSetChanged();
                                //editTextAddPhoneNumber.setText("");
                                Toast.makeText(getApplicationContext(), "Added "+foundPhoneNumberUser.name, Toast.LENGTH_SHORT).show();

                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Sorry, we couldn't find anyone in our database with that number", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error contacting database", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener(){
            //go to messages
            @Override
            public void onClick(View view) {
                goToGroupHome();
            }
        });

    }

    private void goToMain(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void getUsers(){
        arrayListOfMembers.clear();
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    for (String object: arrayListOfMembersString){
                        if(ds.getKey().equals(object)){
                            User u = ds.getValue(User.class);
                            Log.d("u/tostring", u.toString());
                            arrayListOfMembers.add(u);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        arrayAdapter.notifyDataSetChanged();
    }

    public void messageGroupAboutNewMember(String newMemberName){
        //create a message
        Message m = new Message("FLOCK", newMemberName + " has been added to the group");
        //get its map
        Map<String, Object> mValues = m.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/messages/" + groupId + "/", mValues);
        //make the update
        root.child("messages").child(groupId).push().setValue(m);
    }

    public void goToGroupHome(){
        Intent intent = new Intent(getApplicationContext(), GroupHomeActivity.class);
        intent.putExtra("groupId", groupId);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
    }
}
