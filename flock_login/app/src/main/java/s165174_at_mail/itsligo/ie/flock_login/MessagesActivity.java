package s165174_at_mail.itsligo.ie.flock_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {


    //set up variables
    private ListView listViewMessages;
    private EditText editTextMessage;
    private ImageButton buttonSend;

    private ArrayAdapter<Message> arrayAdapter;
    private ArrayList<Message> arrayMessages = new ArrayList<>();

    //get reference to root of database
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    //firebase auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String groupId;
    private String TAG = "mmmmmmmmmmmmm";
    User u;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        //get the user
        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("mmmmmmmmmmm", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("mmmmmmmmmmmm", "onAuthStateChanged:signed_out");
                    //probably go back to login page here
                }
                // ...
            }
        };

        //initiate components
        listViewMessages = (ListView) findViewById(R.id.listViewMessages);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        buttonSend = (ImageButton) findViewById(R.id.buttonSend);

        //array list for added members
        arrayAdapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, arrayMessages);
        listViewMessages.setAdapter(arrayAdapter);

        //get extras
        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            groupId = (String) bundle.get("groupId");
        }

        //set click handler for send button
        buttonSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                sendMessage();
            }
        });

        root.child("messages").child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");

                arrayMessages.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message m = snapshot.getValue(Message.class);
                    Log.d(TAG, "onDataChange: " + m.getMessage());
                    arrayMessages.add(m);
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getUserDetails();

    }//end oncreate

    private void sendMessage(){
        Log.d(TAG, "sendMessage: ");
        String msg = editTextMessage.getText().toString();
        if(!msg.equals("")){
            //if msg isn't empty
            //create new message
            Message m = new Message(u.name, msg);
            //get its map
            Map<String, Object> mValues = m.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/messages/" + groupId + "/", mValues);
            //make the update
            //root.child("message").child(groupId).updateChildren(childUpdates);
            //root.updateChildren(childUpdates);
            root.child("messages").child(groupId).push().setValue(m);
            editTextMessage.setText("");
        }
    }

    private void getUserDetails(){
        root.child("flock-login").child("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
                u = dataSnapshot.getValue(User.class);
                /*for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: " + snapshot);
                    u = snapshot.getValue(User.class);
                    Log.d(TAG, "onDataChange: " + u.toString());
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
