package s165174_at_mail.itsligo.ie.flock_login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private Button buttonMessages;
    private Button buttonShoppingList;
    //private Button buttonAddMember;

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
    String groupName;

    //To inflate the action bar with a menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_group_home, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //To add action to the Log out option on action bar menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                firebaseAuth.signOut();
                goToMain();
                break;
            case R.id.action_leave_group:
                Log.d("mmmmmmmmmmmmmmmm", "onOptionsItemSelected: ");
                leaveGroupHandler();
                break;
            case R.id.action_add_member_to_group:
                HandleAddMembers();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home);

        //To show action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        //set up components
        textViewHeading = (TextView) findViewById(R.id.textViewHeading);
        listViewAddedMembers = (ListView) findViewById(R.id.listViewMembersInGroup);
        buttonMessages = (Button) findViewById(R.id.buttonMessages);
        buttonShoppingList = (Button) findViewById(R.id.buttonShoppingList);

        //array list for added members
        arrayAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, arrayListOfMembers);

        listViewAddedMembers.setAdapter(arrayAdapter);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        firebaseAuth = FirebaseAuth.getInstance();

        if(bundle != null)
        {
            groupId =(String) bundle.get("groupId");
            Log.d("mmmmmmmmmmmmmmm", groupId);
            groupName =(String) bundle.get("groupName");
            Log.d("mmmmmmmmmmmmmmm", groupName);
        }

        textViewHeading.setText(groupName);

        groups.child(groupId).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                arrayListOfMembers.clear();
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

        buttonMessages.setOnClickListener(new View.OnClickListener(){
            //go to messages
            @Override
            public void onClick(View view) {
                goToMessages();
            }
        });

        buttonShoppingList.setOnClickListener(new View.OnClickListener(){
            //go to messages
            @Override
            public void onClick(View view) {
                goToShoppingList();
            }
        });
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

    private void goToMessages(){
        Intent i = new Intent(this, MessagesActivity.class);
        i.putExtra("groupId", groupId);
        startActivity(i);
    }

    private void goToShoppingList(){
        Intent i = new Intent(this, ShoppingListActivity.class);
        i.putExtra("groupId", groupId);
        startActivity(i);
    }

    private void goToMain(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void goToGroups(){
        Intent i = new Intent(this, GroupActivity.class);
        startActivity(i);
    }

    private void HandleAddMembers(){
        Intent i = new Intent(this, AddMemberToExistingGroupActivity.class);
        i.putExtra("groupId", groupId);
        i.putExtra("groupName", groupName);
        startActivity(i);
    }

    private void leaveGroupHandler(){
        Log.d("mmmmmmmmmmmmm", "leaveGroup: method");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        leaveGroup();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void leaveGroup(){
        groups.child(groupId).child("members").child(firebaseAuth.getUid()).removeValue();
        users.child(firebaseAuth.getUid()).child("groups").child(groupId).removeValue();
        goToGroups();
    }
}
