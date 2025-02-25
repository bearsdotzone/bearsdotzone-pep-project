package DAO;

import Model.Message;
import Util.ConnectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageDAO {

    /**
     * @param message a message to be created, lacking a generated message_id
     * @return the created message with its message_id or null if any error occurred.
     */
    @Nullable
    public Message createMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String SQL = "INSERT INTO message(posted_by,message_text,time_posted_epoch) VALUES (?,?,?);";
            // To update our message with its generated ID we must pass the flag Statement.RETURN_GENERATED_KEYS
            PreparedStatement preparedStatement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                message.setMessage_id(resultSet.getInt(1));
                return message;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * @return a list of all messages or an empty list if no messaages exist
     */
    @NotNull
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        Connection connection = ConnectionUtil.getConnection();
        try {
            String SQL = "SELECT * FROM message;";
            /*
             * This could be a normal Statement. However,
             * "If the same SQL statement is executed many times, it may be more efficient to use a PreparedStatement object."
             * - https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/Connection.html#createStatement()
             */
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                messages.add(createMessageFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return messages;
    }

    /**
     * @return the message or null if no message exists
     */
    @Nullable
    public Message getMessage(int messageID) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String SQL = "SELECT * FROM message WHERE message_id=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setInt(1, messageID);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return createMessageFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * @return true if such a message was deleted. Otherwise, return false.
     */
    public boolean deleteMessage(int messageID) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String deleteSQL = "DELETE FROM message WHERE message_id=?;";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL, Statement.RETURN_GENERATED_KEYS);

            deleteStatement.setInt(1, messageID);

            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * @param message a message containing the updated message_text, other fields are ignored
     * @return true if a message was successfully updated. Otherwise, false
     */
    public boolean updateMessage(int messageID, Message message) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String SQL = "UPDATE message SET message_text=? WHERE message_id=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setString(1, message.getMessage_text());
            preparedStatement.setInt(2, messageID);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    /**
     * @return a possibly empty list of all messages tied to an account or an empty list if no such account exists
     */
    @NotNull
    public List<Message> getAccountMessages(int accountID) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String SQL = "SELECT * FROM message WHERE posted_by=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setInt(1, accountID);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Message> messages = new ArrayList<>();
            while (resultSet.next()) {
                messages.add(createMessageFromResultSet(resultSet));
            }
            return messages;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Collections.emptyList();
    }

    @NotNull
    private static Message createMessageFromResultSet(ResultSet resultSet) throws SQLException {
        int message_id = resultSet.getInt(1);
        int posted_by = resultSet.getInt(2);
        String message_text = resultSet.getString(3);
        long time_posted = resultSet.getLong(4);

        return new Message(message_id, posted_by, message_text, time_posted);
    }
}
