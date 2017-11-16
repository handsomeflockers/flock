package s165174_at_mail.itsligo.ie.flock_login;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {

    //notification - related
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews;
    private Context context;


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
        setContentView(R.layout.activity_messages);

        context = this;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(),R.layout.custom_notification_messages);

        remoteViews.setImageViewResource(R.id.notification_icon, R.mipmap.ic_launcher);
        //remoteViews.setTextViewText(R.id.notification_text, "New MESSAGES");

        //To show action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");

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

        /*
        //this might not be a good way to retrieve the messages
        root.child("messages").child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");

                Boolean notified = false;

                arrayMessages.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message m = snapshot.getValue(Message.class);
                    Log.d(TAG, "onDataChange: " + m.getMessage());
                    arrayMessages.add(m);
                    if (notified = false){
                        Log.d(TAG, "notified = false");
                        //don't want to send notification for every single message each time
                        Intent notificaation_intent = new Intent(context, MessagesActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificaation_intent, 0);

                        builder = new NotificationCompat.Builder(context);
                        builder.setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(m.getSender())
                                .setContentText(m.getMessage())
                                .setAutoCancel(true)
                                .setCustomContentView(remoteViews)
                                .setContentIntent(pendingIntent);
                        notificationManager.notify((int)System.currentTimeMillis(), builder.build());
                        notified = true;
                    }
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/




        //new method of getting messages

        root.child("messages").child(groupId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendChat(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendChat(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







        getUserDetails();

    }//end oncreate


    private void appendChat(DataSnapshot ds){
        Message m = ds.getValue(Message.class);
        Log.d(TAG, "onDataChange: " + m.getMessage());
        arrayMessages.add(m);
        arrayAdapter.notifyDataSetChanged();
        sendMessageNotification(m);
    }

    public void sendMessageNotification(Message m){
        remoteViews.setTextViewText(R.id.notification_text, m.getSender() + ": " + m.getMessage());
        Intent notification_intent = new Intent(context, MessagesActivity.class);
        notification_intent.putExtra("groupId", groupId);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notification_intent, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notification_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(m.getSender())
                .setContentText(m.getMessage())
                .setAutoCancel(true)
                .setCustomContentView(remoteViews)
                .setContentIntent(pendingIntent);
       //notificationManager.notify((int)System.currentTimeMillis(), builder.build());
    }


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
    private void goToMain(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
