package s165174_at_mail.itsligo.ie.flock_login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GroupActivity extends AppCompatActivity {

    private Button buttonAddGroup;
    private EditText editTextAddGroupName;
    private ListView listViewGroups;


    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayListOfGroups = new ArrayList<>();

    //get reference to right part of database
    private DatabaseReference groups = FirebaseDatabase.getInstance().getReference("groups");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        buttonAddGroup = (Button) findViewById(R.id.buttonAddGroup);
        editTextAddGroupName = (EditText) findViewById(R.id.editTextGroupName);
        listViewGroups = (ListView) findViewById(R.id.listViewGroups);

        //array list for active groups
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListOfGroups);
        listViewGroups.setAdapter(arrayAdapter);

        buttonAddGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //create new group
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("Group Name", editTextAddGroupName.getText().toString());
                //groups.updateChildren(map);
                groups.push().setValue(map);
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

    }
}
