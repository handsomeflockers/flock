package s165174_at_mail.itsligo.ie.flock_login;

import java.util.List;

/**
 * Created by markm on 24/10/2017.
 */

public class Group {
    private String createdBy;
    private String  dateCreated;
    private String groupName;
    private List<String> members;
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
    public List<String> getMembers(){
        return members;
    }
}