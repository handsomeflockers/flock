package s165174_at_mail.itsligo.ie.flock_login;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by markm on 19/10/2017.
 */

public class User {
    public String uid;
    public String name;
    public String phoneNumber;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public User(String uid, String name, String phoneNumber) {
        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("phoneNumber", phoneNumber);

        return result;
    }
}
