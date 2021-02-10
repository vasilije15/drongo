package dsd.mdhfer.drongo.models;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

import dsd.mdhfer.drongo.viewModels.ContactViewModel;


// wrapper class for ChatKit
public class MessageWrapper implements IMessage {

    private Message message;
    private UserWrapper userWrapper;

    public MessageWrapper(Message message, UserWrapper userWrapper) {
        this.message = message;
        this.userWrapper = userWrapper;
    }

    @Override
    public String getId() {
        if (message.getSentAt() != null) {
            return userWrapper.getId() + "" + message.getSentAt();
        } else {
            return userWrapper.getId() + "" + message.getReceivedAt();
        }
    }

    @Override
    public String getText() {
        return message.getMessageContent();
    }

    @Override
    public IUser getUser() {
        return userWrapper;
    }

    @Override
    public Date getCreatedAt() {
        if (message.getSentAt() != null) {
            return message.getSentAt();
        } else {
            return message.getReceivedAt();
        }
    }

    @Override
    public String toString() {
        return "MessageWrapper{" +
                "message=" + message +
                ", userWrapper=" + userWrapper +
                '}';
    }
}
