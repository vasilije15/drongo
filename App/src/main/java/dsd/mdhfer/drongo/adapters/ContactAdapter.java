package dsd.mdhfer.drongo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import dsd.mdhfer.drongo.models.Contact;
import dsd.mdhfer.drongo.ui.activities.ChatActivity;
import dsd.mdhfer.drongo.viewHolders.ContactViewHolder;
import dsd.mdhfer.drongo.viewModels.ContactViewModel;


public class ContactAdapter extends ListAdapter<Contact, ContactViewHolder> {

    private Context context;
    private ContactViewModel contactViewModel;

    public ContactAdapter(@NonNull DiffUtil.ItemCallback<Contact> diffCallback, Context context, ContactViewModel contactViewModel) {
        super(diffCallback);
        this.context = context;
        this.contactViewModel = contactViewModel;
    }

    protected ContactAdapter(@NonNull AsyncDifferConfig<Contact> config) {
        super(config);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ContactViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact current = getItem(position);

        if (current.getGivenUsername().equalsIgnoreCase("N/A")) {
            holder.bindUsername(current.getContactUsername());
        } else {
            holder.bindUsername(current.getGivenUsername());
        }

        if (current.getLastMessage() == null) {
            holder.bindLastMessage("No messages");
        } else {
            String lastMessage = current.getLastMessage();
            // if message is too long it does not fit in the contact row
            if (lastMessage.length() > 25){
                holder.bindLastMessage(lastMessage.substring(0,25)+"...");
            }else{
                holder.bindLastMessage(lastMessage);
            }

        }

        holder.bindProfilePic(current.getProfilePictureUri());

        // Show Chat Activity with contact
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("username", current.getContactUsername());
            intent.putExtra("ip", current.getIpAddress());
            intent.putExtra("avatar", current.getProfilePictureUri());
            intent.putExtra("uuid", current.getContactUuid());
            intent.putExtra("key", current.getKey());
            context.startActivity(intent);
        });

    }

    public static class ContactDiff extends DiffUtil.ItemCallback<Contact> {

        @Override
        public boolean areItemsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem.getContactUsername().equals(newItem.getContactUsername());
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

}