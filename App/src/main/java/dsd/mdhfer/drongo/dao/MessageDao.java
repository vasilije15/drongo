package dsd.mdhfer.drongo.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import dsd.mdhfer.drongo.models.Message;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM message")
    List<Message> getAll();

    @Query("SELECT * FROM message")
    LiveData<List<Message>> getAllMessages();

    @Query("SELECT * FROM message WHERE contact_uuid LIKE :contactUuid")
    List<Message> getMessagesForContact(String contactUuid);

    @Query("SELECT * FROM message WHERE contact_uuid LIKE :contactUuid")
    LiveData<List<Message>> getLiveDataOfMessagesForContact(String contactUuid);

    @Update
    void update(Message message);

    @Insert
    void insertMessage(Message message);

    @Delete
    void delete(Message message);

    @Query("DELETE FROM message")
    void deleteAll();
}
