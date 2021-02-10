package dsd.mdhfer.drongo.viewHolders;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import dsd.mdhfer.drongo.R;

import static dsd.mdhfer.drongo.utility.Utility.checkURIResource;
import static dsd.mdhfer.drongo.utility.Utility.getAvatar;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    private final TextView contactUsernameView;
    private final CircleImageView contactProfilePicture;
    private final TextView contactLastMessage;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        contactUsernameView = itemView.findViewById(R.id.contactNick);
        contactProfilePicture = itemView.findViewById(R.id.userProfilePicRecycler);
        contactLastMessage = itemView.findViewById(R.id.contactLastMessage);
    }

    public void bindUsername(String text) {
        contactUsernameView.setText(text);
    }

    public void bindProfilePic(String profilePictureUri) {
        if (profilePictureUri != null) {
            if (profilePictureUri.contains("avatar@")) {
                int picture = getAvatar(profilePictureUri);
                contactProfilePicture.setImageResource(picture);
            } else {

                Uri uri = Uri.parse(profilePictureUri);
                Context context = itemView.getContext();
                if (checkURIResource(context, uri)){
                    contactProfilePicture.setImageURI(uri);
                }else{
                    contactProfilePicture.setImageResource(R.mipmap.luki);
                }

            }
        } else {
            contactProfilePicture.setImageResource(R.mipmap.luki);
        }
    }

    public void bindLastMessage(String text) {
        contactLastMessage.setText(text);
    }

    public static ContactViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_row, parent, false);
        return new ContactViewHolder(view);
    }

}
