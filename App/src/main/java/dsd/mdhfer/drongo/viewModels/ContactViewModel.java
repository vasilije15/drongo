package dsd.mdhfer.drongo.viewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import dsd.mdhfer.drongo.models.Contact;
import dsd.mdhfer.drongo.repo.ContactRepository;

public class ContactViewModel extends AndroidViewModel {

    private ContactRepository mRepository;

    private final LiveData<List<Contact>> mContacts;

    public ContactViewModel(Application application) {
        super(application);
        mRepository = new ContactRepository(application);
        mContacts = mRepository.getContacts();
    }

    public LiveData<List<Contact>> getContacts() {
        return mContacts;
    }

    public void insert(Contact contact) {
        mRepository.insert(contact);
    }

    public void update(Contact contact) {
        mRepository.update(contact);
    }

    public void delete(Contact contact) {
        mRepository.delete(contact);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void updateProfilePicture(String uuid, String uri) {
        mRepository.updateProfilePicture(uuid, uri);
    }

    public boolean contactAlreadyExists(String username) throws ExecutionException, InterruptedException {
        return mRepository.checkIfAlreadyExists(username) instanceof Contact;
    }

    public Contact checkIfContactExists(String uuid) throws ExecutionException, InterruptedException {
        return mRepository.checkIfAlreadyExists(uuid);
    }

    public Contact findByUuid(String uuid) throws ExecutionException, InterruptedException {
        return mRepository.findContact(uuid);
    }


    public String checkIfImageIsSet(String uuid) throws ExecutionException, InterruptedException {
        return mRepository.checkIfImageIsSet(uuid);
    }

}
