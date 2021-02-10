package dsd.mdhfer.drongo.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contact")

public class Contact implements Parcelable {

    @PrimaryKey
    @NonNull
    @ColumnInfo (name = "contact_uuid")
    public String contactUuid;

    @ColumnInfo(name = "contact_username")
    public String contactUsername;

    @ColumnInfo(name = "given_username")
    public String givenUsername;

    @Nullable
    @ColumnInfo(name = "timestamp_added_at")
    public String timestampAddedAt;

    @ColumnInfo(name = "key")
    public String key;

    @ColumnInfo(name = "profile_picture_uri")
    public String profilePictureUri;

    @ColumnInfo(name = "ip_address")
    public String ipAddress;

    @Nullable
    @ColumnInfo(name = "last_message")
    public String lastMessage;

    protected Contact(Parcel in) {
        contactUuid = in.readString();
        contactUsername = in.readString();
        givenUsername = in.readString();
        timestampAddedAt = in.readString();
        key = in.readString();
        profilePictureUri = in.readString();
        ipAddress = in.readString();
        lastMessage = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Nullable
    @NonNull
    public String getContactUuid() {
        return contactUuid;
    }

    public void setContactUuid(@NonNull String contactUuid) {
        this.contactUuid = contactUuid;
    }

    public String getContactUsername() {
        return contactUsername;
    }

    public void setContactUsername(String contactUsername) {
        this.contactUsername = contactUsername;
    }

    public String getGivenUsername() {
        return givenUsername;
    }

    public void setGivenUsername(String givenUsername) {
        this.givenUsername = givenUsername;
    }

    @Nullable
    public String getTimestampAddedAt() {
        return timestampAddedAt;
    }

    public void setTimestampAddedAt(@Nullable String timestampAddedAt) {
        this.timestampAddedAt = timestampAddedAt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProfilePictureUri() {
        return profilePictureUri;
    }

    public void setProfilePictureUri(String profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }

    public Contact(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactUuid);
        dest.writeString(contactUsername);
        dest.writeString(givenUsername);
        dest.writeString(timestampAddedAt);
        dest.writeString(key);
        dest.writeString(profilePictureUri);
        dest.writeString(ipAddress);
        dest.writeString(lastMessage);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactUuid='" + contactUuid + '\'' +
                ", contactUsername='" + contactUsername + '\'' +
                ", givenUsername='" + givenUsername + '\'' +
                ", timestampAddedAt='" + timestampAddedAt + '\'' +
                ", key='" + key + '\'' +
                ", profilePictureUri='" + profilePictureUri + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                '}';
    }
}