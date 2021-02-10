package dsd.mdhfer.drongo.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;

import java.util.List;
import java.util.regex.Pattern;

import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.adapters.AvatarAdapter;
import dsd.mdhfer.drongo.models.Avatar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AvatarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AvatarFragment extends Fragment implements DiscreteScrollView.OnItemChangedListener<AvatarAdapter.AvatarViewHolder>,
        View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public AvatarFragment() {
        // Required empty public constructor
    }

    private List<Avatar> avatarList;
    private TextView currentAvatarName;
    private DiscreteScrollView avatarPicker;
    private InfiniteScrollAdapter<?> infiniteAdapter;
    private TextView chooseYourAvatarText;

    public static AvatarFragment newInstance(String param1, String param2) {
        AvatarFragment fragment = new AvatarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_avatars, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        avatarList = Avatar.getAvatars();
avatarPicker = view.findViewById(R.id.avatar_picker);
        currentAvatarName = view.findViewById(R.id.avatar_name);
        chooseYourAvatarText = view.findViewById(R.id.chosen_username);
        infiniteAdapter = InfiniteScrollAdapter.wrap(new AvatarAdapter(avatarList));

        avatarPicker.setOrientation(DSVOrientation.HORIZONTAL);
        avatarPicker.setAdapter(infiniteAdapter);
        avatarPicker.addOnItemChangedListener(this);

        Button choseAvatarButton = view.findViewById(R.id.choose_avatar_button);

        String username = "";

        if (getArguments() != null) {
            AvatarFragmentArgs args = AvatarFragmentArgs.fromBundle(getArguments());
            username = args.getUsername();
        }

        chooseYourAvatarText.setText(username);

        String finalUsername = username;
        choseAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int positionInDataSet = infiniteAdapter.getRealPosition(avatarPicker.getCurrentItem());
AvatarFragmentDirections.ActionAvatarFragmentToPasswordFragment action = AvatarFragmentDirections.actionAvatarFragmentToPasswordFragment(finalUsername);
                action.setAvatar(avatarList.get(positionInDataSet).getName());

                navController.navigate(action);

            }
        });

        onAvatarChanged(avatarList.get(0));

        ImageView backButton = view.findViewById(R.id.back_button_avatar_picker);
        backButton.setOnClickListener(view1 -> navController.popBackStack());
        
    }

    @Override
    public void onClick(View v) {

    }

    private void onAvatarChanged(Avatar avatar) {
        currentAvatarName.setText(avatar.getName());
    }


    @Override
    public void onCurrentItemChanged(@Nullable AvatarAdapter.AvatarViewHolder viewHolder, int adapterPosition) {
        int positionInDataSet = infiniteAdapter.getRealPosition(adapterPosition);
        onAvatarChanged(avatarList.get(positionInDataSet));
    }
}
