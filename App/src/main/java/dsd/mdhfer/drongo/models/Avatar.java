package dsd.mdhfer.drongo.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dsd.mdhfer.drongo.R;

public class Avatar {

    private int id;
    private String name;
    private int picture;

    public Avatar(int id, String name, int picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public static List<Avatar> getAvatars() {
        return Arrays.asList(
                new Avatar(1, "Abuki", R.mipmap.abuki),
                new Avatar(2, "Deki", R.mipmap.deki),
                new Avatar(3, "Iki", R.mipmap.iki),
                new Avatar(4, "Luki", R.mipmap.luki),
                new Avatar(5, "Maki", R.mipmap.maki),
                new Avatar(6, "Paki", R.mipmap.paki),
                new Avatar(7, "Peki", R.mipmap.peki),
                new Avatar(8, "Teki", R.mipmap.teki),
                new Avatar(9, "Toki", R.mipmap.toki),
                new Avatar(10, "Vaki", R.mipmap.vaki)
                );
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", picture=" + picture +
                '}';
    }
}
