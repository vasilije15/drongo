package dsd.mdhfer.drongo.ui.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.adapters.ContactAdapter;
import dsd.mdhfer.drongo.services.ServerService;
import dsd.mdhfer.drongo.ui.activities.Scanner;
import dsd.mdhfer.drongo.ui.activities.UserQR;
import dsd.mdhfer.drongo.viewModels.ContactViewModel;
import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;
    private FloatingActionButton add_btn;
    private FloatingActionButton scan_qr_code_btn;
    private FloatingActionButton generate_qr_code_btn;
    private boolean clicked = false;

    private static final String TAG = "ContactsFragment";


    private ContactViewModel contactViewModel;


    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;

    public ContactsFragment() {
        // Required empty public constructor
    }


    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
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
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        contactViewModel = new ViewModelProvider(getActivity()).get(ContactViewModel.class);

        TextView noContactsLabel = view.findViewById(R.id.noContactsLabel);

        RecyclerView recyclerView = view.findViewById(R.id.contactList);
        final ContactAdapter adapter = new ContactAdapter(new ContactAdapter.ContactDiff(), getActivity(), contactViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        contactViewModel.getContacts().observe(getActivity(), contacts -> {

            if (contacts.size() == 0){
                noContactsLabel.setVisibility(View.VISIBLE);
            }else{
                noContactsLabel.setVisibility(View.GONE);
            }

            adapter.submitList(contacts);
        });



        FloatingActionButton addContacts = view.findViewById(R.id.add_contacts_button);
        addContacts.setOnClickListener(v -> {

            // TODO change start activity for result
            Intent intent = new Intent(getActivity(), Scanner.class);
            startActivityForResult(intent, 69);// Activity is started with requestCode 69

        });

        FloatingActionButton showQRFab = view.findViewById(R.id.show_qr_fab);

        showQRFab.setOnClickListener(v -> {

//                navController.navigate(R.id.action_menuContacts_to_userQRFragment);

            // User's QR code can be shown in new Activity or in fragment

            Intent intent = new Intent(getActivity(), UserQR.class);
            startActivity(intent);

        });


        rotateOpen = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(view.getContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(view.getContext(), R.anim.to_bottom_anim);

        this.add_btn = view.findViewById(R.id.add_btn);
        this.scan_qr_code_btn = view.findViewById(R.id.add_contacts_button);
        this.generate_qr_code_btn = view.findViewById(R.id.show_qr_fab);

        this.add_btn.setOnClickListener(v -> onAddButtonClicked());

    }

    private void onAddButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        this.clicked = !this.clicked;
    }

    private void setVisibility(Boolean clicked){
        if(!clicked){
            scan_qr_code_btn.setVisibility(View.VISIBLE);
            generate_qr_code_btn.setVisibility(View.VISIBLE);
        }else{
            scan_qr_code_btn.setVisibility(View.INVISIBLE);
            generate_qr_code_btn.setVisibility(View.INVISIBLE);
        }

    }

    private void setAnimation(Boolean clicked){
        if(!clicked){
            generate_qr_code_btn.startAnimation(fromBottom);
            scan_qr_code_btn.startAnimation(fromBottom);
            add_btn.startAnimation(rotateOpen);
        }else{
            generate_qr_code_btn.startAnimation(toBottom);
            scan_qr_code_btn.startAnimation(toBottom);
            add_btn.startAnimation(rotateClose);
        }
    }


    // Get the results:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == 0) {
            Toasty.info(getActivity(), "Scan cancelled", Toast.LENGTH_SHORT).show();
        } else if (data.getStringExtra("QR").equals("Invalid")) {
            Toasty.error(getActivity(), "This is not a contact QR code!", Toast.LENGTH_LONG).show();
        } else if (data.getStringExtra("QR").equals("Exists")){
            Toasty.info(getActivity(), "Contact info has been updated!", Toast.LENGTH_LONG).show();
        }else{
            Toasty.success(getActivity(), "Successfully added " + data.getStringExtra("QR"), Toast.LENGTH_LONG).show();
        }
    }
}
