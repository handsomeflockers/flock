package s165174_at_mail.itsligo.ie.flock_login;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MessageAdapter extends ArrayAdapter {

    public List<Message> messageList;
    private int resource;
    private LayoutInflater inflater;

    //firebase auth
    private FirebaseAuth firebaseAuth;


    public MessageAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
        messageList = objects;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            convertView = inflater.inflate(R.layout.message_layout, null);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        TextView textViewSender;
        TextView textViewMessage;
        LinearLayout linLayout;

        textViewSender = (TextView) convertView.findViewById(R.id.textViewSender);
        textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
        linLayout = (LinearLayout) convertView.findViewById(R.id.msgBackground);



        textViewMessage.setText(messageList.get(position).getMessage());

        if(firebaseAuth.getCurrentUser().getUid().equals(messageList.get(position).getUid())){
            //sender uid is logged in user uid
            textViewSender.setText("");
            textViewMessage.setTextColor(Color.parseColor("#212121"));
            linLayout.setBackgroundColor(Color.parseColor("#BDBDBD"));
            linLayout.setGravity(Gravity.RIGHT);




        }else if(messageList.get(position).getSender().equals("FLOCK")){
            //if FLOCK is the sender
            textViewSender.setText("FLOCK");
            linLayout.setGravity(Gravity.CENTER);
            linLayout.setBackgroundColor(Color.parseColor("#757575"));
            textViewSender.setTextColor(Color.parseColor("#FFFFFF"));
            textViewMessage.setTextColor(Color.parseColor("#FFFFFF"));
        } else{
            textViewSender.setText(messageList.get(position).getSender());
            linLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            linLayout.setGravity(Gravity.LEFT);

            textViewSender.setTextColor(Color.parseColor("#757575"));
            textViewMessage.setTextColor(Color.parseColor("#212121"));
        }

        return convertView;
    }
}
