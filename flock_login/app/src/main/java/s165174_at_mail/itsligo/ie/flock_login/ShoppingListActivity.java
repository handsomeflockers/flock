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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static s165174_at_mail.itsligo.ie.flock_login.R.id.buttonAddItem;

public class ShoppingListActivity extends AppCompatActivity {

    //set up variables
    private ListView listViewItems;
    private EditText editTextAddItem;
    private Button buttonSend;

    private ArrayAdapter<ShoppingListItem> arrayAdapter;
    private ArrayList<ShoppingListItem> arrayItems = new ArrayList<>();

    private ArrayList<String> arrayListShoppingListItems = new ArrayList<>();

    private ShoppingListAdapter shoppingListAdapter;

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
        setContentView(R.layout.activity_shopping_list);

        //To show action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shopping List");

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

        //get extras
        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            groupId = (String) bundle.get("groupId");
        }

        //initiate components
        listViewItems = (ListView) findViewById(R.id.listViewShoppingListItems);
        editTextAddItem = (EditText) findViewById(R.id.editTextAddItem);
        buttonSend = (Button) findViewById(buttonAddItem);

        //array list for added members
        arrayAdapter = new ArrayAdapter<ShoppingListItem>(this, android.R.layout.simple_list_item_1, arrayItems);
        ShoppingListAdapter arrayAdapter = new ShoppingListAdapter(this, R.layout.shopping_list_layout, arrayItems);
        listViewItems.setAdapter(arrayAdapter);

        //when button is pressed
        buttonSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                addItem();
            }
        });

        //get the items from database
        root.child("shoppingList").child(groupId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendShoppingList(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //see changeList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //when we get to removing items after a certain amount of time,
                //we need to sort this part out
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //I don't think we'll need this
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //gotta pop up a toast here
            }
        });

        getUserDetails();
    }

    private void appendShoppingList(DataSnapshot ds){
        //add new item to shopping list arraylist
        ShoppingListItem i = ds.getValue(ShoppingListItem.class);
        i.setKey(ds.getKey());
        i.setGroupId(groupId);
        Log.d(TAG, "onDataChange: " + i.getKey());
        arrayItems.add(i);
        arrayAdapter.notifyDataSetChanged();
    }

    private void changeList(DataSnapshot ds){
        //this needs to reflect changes made on other devices
        //basically, if a user ticks or unticks a checkbox item,
        //we need to update it here - preferably without loading
        //every item from the db again
    }

    private void getUserDetails(){
        //who is logged in?
        root.child("flock-login").child("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
                u = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addItem(){
        Log.d(TAG, "addItem: ");
        String item = editTextAddItem.getText().toString();
        if(!item.equals("")){
            //if item isn't empty
            //create new item
            ShoppingListItem i = new ShoppingListItem(u.name, item, u.uid);
            //get its map
            //make the update
            root.child("shoppingList").child(groupId).push().setValue(i.toMap());
            editTextAddItem.setText("");
        }
    }



    private void goToMain(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}

