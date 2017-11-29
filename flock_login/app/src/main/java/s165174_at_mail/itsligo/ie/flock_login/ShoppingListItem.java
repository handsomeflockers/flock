package s165174_at_mail.itsligo.ie.flock_login;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by markm on 28/11/2017.
 */

public class ShoppingListItem {
    private String  sender;
    private String item;
    private Long timestamp;
    private String uid;
    private String groupId;
    private Boolean checked;
    private String key;
    public ShoppingListItem() {
        // empty default constructor, necessary for Firebase to be able to deserialize posts
    }
    public ShoppingListItem(String sender, String message){
        this.sender = sender;
        this.item = message;
        this.timestamp = System.currentTimeMillis()/1000;
    }

    public ShoppingListItem(String sender, String item, String uid){
        this.sender = sender;
        this.item = item;
        this.uid = uid;
        this.timestamp = System.currentTimeMillis()/1000;
    }

    public void addTimeStamp(){
        this.timestamp = System.currentTimeMillis()/1000;
    }

    public String getItem(){
        return item;
    }

    public String getSender(){
        return sender;
    }

    public String getUid(){ return uid; }

    public Boolean getChecked(){ return checked; }

    public String getKey(){ return key; }

    public void setKey(String key){ this.key = key;}

    public String getGroupId(){ return groupId; }

    public void setGroupId(String gid){ this.groupId = gid;}

    public Long getTimestamp(){
        return timestamp;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("sender", sender);
        result.put("item", item);
        result.put("timestamp", timestamp);
        result.put("uid", uid);
        result.put("checked", false);
        return result;
    }

    @Override
    public String toString(){
        return sender + ":  " + item;
    }
}
