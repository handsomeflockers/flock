package s165174_at_mail.itsligo.ie.flock_login;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by markm on 31/10/2017.
 */

public class Message {
    private String  sender;
    private String message;
    private Long timestamp;
    public Message() {
        // empty default constructor, necessary for Firebase to be able to deserialize posts
    }
    public Message(String sender, String message){
        this.sender = sender;
        this.message = message;
        this.timestamp = System.currentTimeMillis()/1000;
    }

    public void addTimeStamp(){
        this.timestamp = System.currentTimeMillis()/1000;
    }

    public String getMessage(){
        return message;
    }

    public String getSender(){
        return sender;
    }

    public Long getTimestamp(){
        return timestamp;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("sender", sender);
        result.put("message", message);
        result.put("timestamp", timestamp);
        return result;
    }

    @Override
    public String toString(){
        return sender + ":  " + message;
    }

}
