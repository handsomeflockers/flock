package s165174_at_mail.itsligo.ie.flock_login;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by markm on 24/10/2017.
 */

public class Group {
    private String createdBy;
    private String  dateCreated;
    private String groupName;
    private String key;
    //private List<String> members;
    private Map<String, Object> members = new HashMap<String, Object>();
    public void setMember(Map<String, Object> map)
    {
        this.members = map;
    }
    public Map<String, Object> getMember()
    {
        return this.members;
    }
    public Group() {
        // empty default constructor, necessary for Firebase to be able to deserialize posts
    }
    public String getGroupName() {
        return groupName;
    }
    public String getDateCreated() {
        return dateCreated;
    }
    public String getCreatedBy(){
        return createdBy;
    }
    public String getKey(){ return key; }
    public void setKey(String key){
        this.key = key;
    }
    @Override
    public String toString(){
        return groupName;
    }

}