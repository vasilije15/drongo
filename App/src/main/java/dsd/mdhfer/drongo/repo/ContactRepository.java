package dsd.mdhfer.drongo.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dsd.mdhfer.drongo.dao.ContactDao;
import dsd.mdhfer.drongo.database.AppDatabase;
import dsd.mdhfer.drongo.models.Contact;

public class ContactRepository {

    private ContactDao lContactDao; // local Contact Dao
    private LiveData<List<Contact>> allContacts;

    public ContactRepository(Application application) {

        AppDatabase db = AppDatabase.getDatabase(application);
        lContactDao = db.contactDao();
        allContacts = lContactDao.getBasicContactsInfo();
    }

    public LiveData<List<Contact>> getContacts() {
        return allContacts;
    }

    public void insert(Contact Contact) {
        new InsertContactAsyncTask(lContactDao).execute(Contact);
    }

    public void update(Contact Contact) {
        new UpdateContactAsyncTask(lContactDao).execute(Contact);
    }

    public void delete(Contact Contact) {
        new DeleteContactAsyncTask(lContactDao).execute(Contact);
    }

    public void deleteAll() {
        new DeleteAllContactAsyncTask(lContactDao).execute();
    }

    public boolean findIfExists(Contact contact) {
        return lContactDao.findByUsername(contact.getContactUsername());
    }

    public Contact checkIfAlreadyExists(String uuid) throws ExecutionException, InterruptedException {

        Callable<Contact> callable = new Callable<Contact>() {
            @Override
            public Contact call() throws Exception {
                return lContactDao.findByID(uuid);
            }
        };

        Future<Contact> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    public void updateProfilePicture(String uuid, String uri) {

        Callable<Contact> callable = new Callable<Contact>() {
            @Override
            public Contact call() throws Exception {
                lContactDao.updateProfilePicture(uuid, uri);
                return null;
            }
        };

        Future<Contact> future = Executors.newSingleThreadExecutor().submit(callable);

    }

    public Contact findContact(String uuid) throws ExecutionException, InterruptedException {

        Callable<Contact> callable = new Callable<Contact>() {
            @Override
            public Contact call() throws Exception {
                return lContactDao.findByID(uuid);
            }
        };

        Future<Contact> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


    // insert Contact asynchronously
    private static class InsertContactAsyncTask extends AsyncTask<Contact, Void, Void> {

        private ContactDao contactDao;

        private InsertContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Contact... Contacts) {
            contactDao.insertContact(Contacts[0]);
            return null;
        }
    }

    // update Contact asynchronously
    private static class UpdateContactAsyncTask extends AsyncTask<Contact, Void, Void> {

        private ContactDao contactDao;

        private UpdateContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Contact... Contacts) {
            contactDao.update(Contacts[0]);
            return null;
        }
    }

    // delete Contact asynchronously
    private static class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Void> {

        private ContactDao contactDao;

        private DeleteContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Contact... Contacts) {
            contactDao.delete(Contacts[0]);
            return null;
        }
    }

    // delete all contacts asynchronously
    private static class DeleteAllContactAsyncTask extends AsyncTask<Void, Void, Void> {

        private ContactDao contactDao;

        private DeleteAllContactAsyncTask(ContactDao contactDao) {

            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            contactDao.deleteAll();
            return null;
        }
    }

    // check if profile picture has already been set
    public String checkIfImageIsSet(String uuid) throws ExecutionException, InterruptedException {

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return lContactDao.checkIfImageIsSet(uuid);
            }
        };

        Future<String> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    // get public key of contact
    public String getKey(String uuid) throws ExecutionException, InterruptedException {

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return lContactDao.getKey(uuid);
            }
        };

        Future<String> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public String checkIfexists(String uuid) throws ExecutionException, InterruptedException {

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return lContactDao.checkIfExists(uuid);
            }
        };

        Future<String> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


}
