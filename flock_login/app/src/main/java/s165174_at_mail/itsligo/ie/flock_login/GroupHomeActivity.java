package s165174_at_mail.itsligo.ie.flock_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupHomeActivity extends AppCompatActivity {

    private TextView textViewHeading;
    private ListView listViewAddedMembers;

    private ArrayAdapter<User> arrayAdapter;
    private ArrayList<String> arrayListOfMembersString = new ArrayList<>();
    private ArrayList<User> arrayListOfMembers = new ArrayList<>();

    //get reference to root of database
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    //get reference to right part of database
    private DatabaseReference groups = FirebaseDatabase.getInstance().getReference("groups");

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("flock-login").child("users");

    String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home);

        //set up components
        textViewHeading = (TextView) findViewById(R.id.textViewHeading);
        listViewAddedMembers = (ListView) findViewById(R.id.listViewMembersInGroup);

        //array list for added members
        arrayAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, arrayListOfMembers);

        listViewAddedMembers.setAdapter(arrayAdapter);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null)
        {
            groupId =(String) bundle.get("groupId");
            Log.d("mmmmmmmmmmmmmmm", groupId);
        }

        textViewHeading.setText(groupId);

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
        arrayAdapter.notifyDataSetChanged();
    }

    public void getUsers(){
        //arrayAdapter.clear();
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
}
