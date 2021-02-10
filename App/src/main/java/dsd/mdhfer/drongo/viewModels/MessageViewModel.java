package dsd.mdhfer.drongo.viewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import dsd.mdhfer.drongo.models.Message;
import dsd.mdhfer.drongo.repo.MessageRepository;

public class MessageViewModel extends AndroidViewModel {

    private MessageRepository lRepository;
    private final LiveData<List<Message>> allMessages;

    public MessageViewModel(Application application) {
        super(application);
        lRepository = new MessageRepository(application);
        allMessages = lRepository.getMessages();
    }

    public LiveData<List<Message>> getMessages() {
        return allMessages;
    }

    public void insert(Message message) {
        lRepository.insert(message);
    }

    public List<Message> getMessagesForContact(String uuid) throws ExecutionException, InterruptedException {
        return lRepository.getMessagesForContact(uuid);
    }

}
