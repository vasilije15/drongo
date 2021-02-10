package dsd.mdhfer.drongo.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dsd.mdhfer.drongo.dao.MessageDao;
import dsd.mdhfer.drongo.database.AppDatabase;
import dsd.mdhfer.drongo.models.Message;

public class MessageRepository {

    private MessageDao lMessageDao; // local Message Dao
    private LiveData<List<Message>> allMessages;

    public MessageRepository(Application application) {

        AppDatabase db = AppDatabase.getDatabase(application);
        lMessageDao = db.messageDao();
        allMessages = lMessageDao.getAllMessages();
    }

    public LiveData<List<Message>> getMessages() {
        return allMessages;
    }

    public void insert(Message message) {
        new InsertMessageAsyncTask(lMessageDao).execute(message);
    }


    // insert Message asynchronously
    private static class InsertMessageAsyncTask extends AsyncTask<Message, Void, Void> {

        private MessageDao messageDao;

        private InsertMessageAsyncTask(MessageDao MessageDao) {
            this.messageDao = MessageDao;
        }

        @Override
        protected Void doInBackground(Message... Messages) {
            messageDao.insertMessage(Messages[0]);
            return null;
        }
    }

    public List<Message> getMessagesForContact(String uuid) throws ExecutionException, InterruptedException {

        Callable<List<Message>> callable = new Callable<List<Message>>() {
            @Override
            public List<Message> call() throws Exception {
                return lMessageDao.getMessagesForContact(uuid);
            }
        };

        Future<List<Message>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }


}
