package dsd.mdhfer.drongo.models;

import com.stfalcon.chatkit.commons.models.IUser;


// wrapper class for ChatKit
public class UserWrapper implements IUser {

    private String id;
    private String name;
    private String avatar;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public UserWrapper(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "UserWrapper{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
