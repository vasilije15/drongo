package dsd.mdhfer.drongo.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import dsd.mdhfer.drongo.models.Contact;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contact")
    List<Contact> getAll();


    @Query("SELECT * FROM contact WHERE contact_username LIKE :username")
    Contact findByName(String username);

    @Query("SELECT * FROM contact WHERE contact_uuid LIKE :contactUuid")
    Contact findByID(String contactUuid);


    @Query("SELECT * from contact")
    LiveData<List<Contact>> getBasicContactsInfo();

    @Query("SELECT * FROM contact WHERE contact_username LIKE :username")
    boolean findByUsername(String username);

    @Query("UPDATE contact SET profile_picture_uri = :uri WHERE contact_uuid LIKE :uuid")
    void updateProfilePicture(String uuid, String uri);

    @Query("UPDATE contact SET given_username = :givenUsername WHERE contact_uuid LIKE :uuid")
    void updateGivenUsername(String uuid, String givenUsername);

    @Query("SELECT profile_picture_uri FROM contact WHERE  contact_uuid = :uuid")
    String checkIfImageIsSet(String uuid);

    @Query("SELECT `key` FROM contact WHERE  contact_uuid = :uuid")
    String getKey(String uuid);

    @Query("SELECT contact_uuid FROM contact WHERE  contact_uuid = :uuid")
    String checkIfExists(String uuid);

    @Update
    void update(Contact contact);

    @Insert
    void insertContact(Contact contact);

    @Delete
    void delete(Contact contact);

    @Query("DELETE FROM contact")
    void deleteAll();
}
