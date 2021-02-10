package dsd.mdhfer.drongo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.models.Avatar;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {

    List<Avatar> avatarList;

    public AvatarAdapter(List<Avatar> avatarList) {
        this.avatarList = avatarList;
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_avatar_card, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        holder.avatarImage.setImageResource(avatarList.get(position).getPicture());
    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }

    public static class AvatarViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarImage;

        public AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.avatarImage);
        }
    }
}
