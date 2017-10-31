package s165174_at_mail.itsligo.ie.flock_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateNewGroupActivity extends AppCompatActivity {

    private Button buttonAddGroup;
    private Button buttonAddNumber;
    private EditText editTextGroupName;
    private EditText editTextAddPhoneNumber;
    private ListView listViewAddedMembers;

    private ArrayAdapter<User> arrayAdapter;
    private ArrayList<User> arrayListOfMembers = new ArrayList<>();

    //get reference to root of database
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    //get reference to right part of database
    private DatabaseReference groups = FirebaseDatabase.getInstance().getReference("groups");

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("flock-login").child("users");

    private User creator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        buttonAddGroup = (Button) findViewById(R.id.buttonCreateGroup);
        buttonAddNumber = (Button) findViewById(R.id.buttonAddNumber);
        editTextGroupName = (EditText) findViewById(R.id.editTextGroupName);
        editTextAddPhoneNumber = (EditText) findViewById(R.id.editTextAddPhoneNumber);
        listViewAddedMembers = (ListView) findViewById(R.id.listViewAddedMembers);

        //get logged in user
        firebaseAuth = FirebaseAuth.getInstance();
        creator = new User();
        creator.uid = firebaseAuth.getUid();
        users.child(creator.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                creator.name = (String) snapshot.child("name").getValue();
                creator.phoneNumber = (String) snapshot.child("phoneNumber").getValue();
                arrayListOfMembers.add(creator);
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                creator.name = "unknown name";
                creator.phoneNumber = "unknown phone number";
                Toast.makeText(getApplicationContext(), "Error contacting database", Toast.LENGTH_SHORT).show();

            }

        });

        //array list for added members
        arrayAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, arrayListOfMembers);

        listViewAddedMembers.setAdapter(arrayAdapter);

        buttonAddNumber.setOnClickListener(new View.OnClickListener(){
            //ADD ANOTHER PERSON TO THE GROUP
            @Override
            public void onClick(View view){
                Log.d("mmmmmmmmmmmmmmmmmmm", "onClick: ");
                String phoneNumber = editTextAddPhoneNumber.getText().toString();
                if(TextUtils.isEmpty(phoneNumber)){
                    //email is empty
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
                            // dataSnapshot is the "issue" node with all children with id 0
                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                User foundPhoneNumberUser = new User();
                                foundPhoneNumberUser.uid = (String) issue.child("uid").getValue();
                                foundPhoneNumberUser.name = (String) issue.child("name").getValue();
                                foundPhoneNumberUser.phoneNumber = (String) issue.child("phoneNumber").getValue();
                                arrayListOfMembers.add(foundPhoneNumberUser);
                                arrayAdapter.notifyDataSetChanged();
                                editTextAddPhoneNumber.setText("");
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

        buttonAddGroup.setOnClickListener(new View.OnClickListener(){
            //ACTUALLY CREATE THE GROUP
            @Override
            public void onClick(View view) {
                Log.d("mmmmmmmmmmmmmm", "onClick: ");
                String groupName = editTextGroupName.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    //groupName is empty
                    Toast.makeText(getApplicationContext(), "Please enter a group name", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("mmmmmmmmmmmmmm", "going well");
                DatabaseReference key = groups.push();
                Log.d("mmmmmmmmmmmmmm", "bzzzt");
                //create new group
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("groupName", groupName);
                map.put("createdBy", firebaseAuth.getCurrentUser().getUid());
                map.put("dateCreated", Calendar.getInstance().getTime().toString());
                map.put("members", "");
                //groups.updateChildren(map);
                //groups.push().setValue(map);
                key.setValue(map);
                String keyOfJustCreatedNode = key.getKey();
                Log.d("mmmmmmmmmmmmmm", keyOfJustCreatedNode);
                DatabaseReference attachUsers = groups.child(keyOfJustCreatedNode).child("members");
                Map<String, Object> usersMap = new HashMap<String, Object>();
                for (User u : arrayListOfMembers) {
                    usersMap.put(u.uid, true);
                    Log.d("mmmmmmmmmmmmmm", u.uid);
                    attachUsers.updateChildren(usersMap);
                }

                Toast.makeText(getApplicationContext(), "Group created", Toast.LENGTH_SHORT).show();

                goToGroups();

            }
        });


    }

    private void goToGroups(){
        Intent i = new Intent(this, GroupActivity.class);
        startActivity(i);
    }


}
