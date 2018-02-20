package s165174_at_mail.itsligo.ie.flock_login;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by markm on 20/02/2018.
 */

public class TaskListAdapter extends ArrayAdapter {
    public List<ShoppingListItem> itemList;
    private int resource;
    private boolean checkedBool;
    private LayoutInflater inflater;
    //get reference to root of database
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();


    public TaskListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
        itemList = objects;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.shopping_list_layout, null);
        }

        LinearLayout linearLayout;
        TextView textViewItem;
        CheckBox checkbox;

        linearLayout = (LinearLayout) convertView.findViewById(R.id.msgBackground);
        textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
        checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);

        linearLayout.setOnTouchListener(null);

        checkedBool = itemList.get(position).getChecked();

        //set textview to the item in db
        textViewItem.setText(itemList.get(position).getItem());
        //set checkbox equal to the boolean in db
        checkbox.setChecked(itemList.get(position).getChecked());
        //set what happens if checkbox is checked
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                checkedBool = !checkedBool;
                //update db
                Map<String,Object> m = new HashMap<String,Object>();
                m.put("checked", checkedBool);
                root.child("taskList").child(itemList.get(position).getGroupId()).child(itemList.get(position).getKey()).updateChildren(m);
            }
        });

        return convertView;
    }
}
