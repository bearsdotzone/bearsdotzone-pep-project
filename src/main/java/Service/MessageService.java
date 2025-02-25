package Service;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MessageService {
    private final MessageDAO messageDAO;
    private final AccountDAO accountDAO;

    public MessageService() {
        accountDAO = new AccountDAO();
        messageDAO = new MessageDAO();
    }

    /**
     * The creation of the message will be successful if and only if the message_text is not blank, is not over 255
     * characters, and posted_by refers to a real, existing user. If successful, the response should contain the
     * message, including its message_id.
     *
     * @return the created message with its message_id or null if the message could not be created for any reason
     */
    @Nullable
    public Message createMessage(Message message) {
        if (message.getMessage_text().isEmpty())
            return null;
        if (message.getMessage_text().length() > 255)
            return null;
        if (accountDAO.getAccount(message.getPosted_by()) == null)
            return null;
        return messageDAO.createMessage(message);
    }

    /**
     * @return a list of all messages or an empty list if no messages exist
     */
    @NotNull
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    @Nullable
    public Message getMessage(int messageID) {
        return messageDAO.getMessage(messageID);
    }

    /**
     * @return the deleted message or null if the indicated message could not be deleted for any reason.
     */
    @Nullable
    public Message deleteMessage(int messageID) {
        Message message = messageDAO.getMessage(messageID);
        if (message == null)
            return null;
        if (messageDAO.deleteMessage(messageID))
            return message;
        return null;
    }

    /**
     * The update of a message should be successful if and only if the message id already exists and the new
     * message_text is not blank and is not over 255 characters. If the update is successful, the response should
     * contain the full updated message (including message_id, posted_by, message_text, and time_posted_epoch).
     *
     * @param messageID the ID of the message to update
     * @param message   the message to be updated to
     * @return the updated message or null if the indicated message could not be updated for any reason
     */
    @Nullable
    public Message updateMessage(int messageID, Message message) {
        if (messageDAO.getMessage(messageID) == null)
            return null;
        if (message.getMessage_text().isEmpty())
            return null;
        if (message.getMessage_text().length() > 255)
            return null;
        if (messageDAO.updateMessage(messageID, message))
            return messageDAO.getMessage(messageID);
        return null;
    }

    /**
     * @return a possibly empty list of the account's messages or an empty list if no such account exists
     */
    @NotNull
    public List<Message> getAccountMessages(int accountID) {
        return messageDAO.getAccountMessages(accountID);
    }

}
