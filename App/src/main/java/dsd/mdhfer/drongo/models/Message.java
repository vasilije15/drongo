package dsd.mdhfer.drongo.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "message",
        foreignKeys =
        @ForeignKey(entity = Contact.class,
                parentColumns = "contact_uuid",
                childColumns = "contact_uuid",
                onDelete = CASCADE)
)
public class Message {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    public int id;

    @NonNull
    @ColumnInfo(name = "message_content")
    public String messageContent;

    @NonNull
    @ColumnInfo(name = "contact_uuid")
    public String contactUuid;

    @Nullable
    @ColumnInfo(name = "received_at")
    public Date receivedAt;

    @Nullable
    @ColumnInfo(name = "sent_at")
    public Date sentAt;

    @Nullable
    @ColumnInfo(name = "read_at")
    public Date readAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getContactUuid() {
        return contactUuid;
    }

    public void setContactUuid(String contactUuid) {
        this.contactUuid = contactUuid;
    }

    @Nullable
    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(@Nullable Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", messageContent='" + messageContent + '\'' +
                ", contactUuid='" + contactUuid + '\'' +
                ", receivedAt=" + receivedAt +
                ", sentAt=" + sentAt +
                ", readAt=" + readAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof Message)
       {
           Message message = (Message) o;
           return this.contactUuid.equals(message.getContactUuid())
                   && this.messageContent.equals(message.getMessageContent())
                   && this.receivedAt.equals(message.getReceivedAt());

       }
        return false;
    }
}