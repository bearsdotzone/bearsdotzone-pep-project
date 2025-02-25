package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class SocialMediaController {

    private final AccountService accountService = new AccountService();
    private final MessageService messageService = new MessageService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     *
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // As a user, I should be able to create a new Account on the endpoint POST localhost:8080/register.
        // The body will contain a representation of a JSON Account, but will not contain an account_id.
        app.post("/register", this::createAccountHandler);
        // As a user, I should be able to verify my login on the endpoint POST localhost:8080/login.
        // The request body will contain a JSON representation of an Account, not containing an account_id.
        app.post("/login", this::loginAccountHandler);
        // As a user, I should be able to submit a new post on the endpoint POST localhost:8080/messages.
        // The request body will contain a JSON representation of a message, which should be persisted to the database, but will not contain a message_id.
        app.post("/messages", this::createMessageHandler);
        // As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages.
        app.get("/messages", this::getAllMessagesHandler);
        // As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages/{message_id}.
        app.get("/messages/{message_id}", this::getMessageHandler);
        // As a User, I should be able to submit a DELETE request on the endpoint DELETE localhost:8080/messages/{message_id}.
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        // As a user, I should be able to submit a PATCH request on the endpoint PATCH localhost:8080/messages/{message_id}.
        // The request body should contain a new message_text values to replace the message identified by message_id.
        // The request body can not be guaranteed to contain any other information.
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        // As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/accounts/{account_id}/messages.
        app.get("/accounts/{account_id}/messages", this::getAccountMessagesHandler);

        return app;
    }

    /**
     * If successful, the response body should contain a JSON of the Account, including its account_id. The response
     * status should be 200 OK. If the registration is not successful, the response status should be 400. (Client
     * error)
     *
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void createAccountHandler(Context context) throws JsonProcessingException {
        Account input = objectMapper.readValue(context.body(), Account.class);
        Account account = accountService.createAccount(input);

        if (account == null)
            context.status(400);
        else
            context.json(objectMapper.writeValueAsString(account));
    }

    /**
     * If successful, the response body should contain a JSON of the account in the response body, including its
     * account_id. The response status should be 200 OK. If the login is not successful, the response status should be
     * 401. (Unauthorized)
     *
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void loginAccountHandler(Context context) throws JsonProcessingException {
        Account input = objectMapper.readValue(context.body(), Account.class);
        Account account = accountService.loginUser(input);

        if (account == null)
            context.status(401);
        else
            context.json(objectMapper.writeValueAsString(account));
    }

    /**
     * If successful, the response body should contain a JSON of the message, including its message_id. The response
     * status should be 200. If the creation of the message is not successful, the response status should be 400.
     * (Client error)
     *
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void createMessageHandler(Context context) throws JsonProcessingException {
        Message input = objectMapper.readValue(context.body(), Message.class);
        Message message = messageService.createMessage(input);

        if (message == null)
            context.status(400);
        else
            context.json(objectMapper.writeValueAsString(message));
    }

    /**
     * The response body should contain a JSON representation of a list containing all messages retrieved from the
     * database. It is expected for the list to simply be empty if there are no messages. The response status should
     * always be 200.
     *
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getAllMessagesHandler(Context context) throws JsonProcessingException {
        List<Message> messages = messageService.getAllMessages();

        context.json(objectMapper.writeValueAsString(messages));
    }

    /**
     * The response body should contain a JSON representation of the message identified by the message_id. It is
     * expected for the response body to simply be empty if there is no such message. The response status should always
     * be 200.
     *
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getMessageHandler(Context context) throws JsonProcessingException {
        int messageID = Integer.parseInt(context.pathParam("message_id"));
        Message message = messageService.getMessage(messageID);

        if (message == null)
            context.status(200);
        else
            context.json(objectMapper.writeValueAsString(message));
    }

    /**
     * If the message existed, the response body should contain the now-deleted message. The response status should be
     * 200. If the message did not exist, the response status should be 200, but the response body should be empty.
     *
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void deleteMessageHandler(Context context) throws JsonProcessingException {
        int messageID = Integer.parseInt(context.pathParam("message_id"));
        Message message = messageService.deleteMessage(messageID);

        if (message == null)
            context.status(200);
        else
            context.json(objectMapper.writeValueAsString(message));
    }

    /**
     * If the update is successful, the response body should contain the full updated message (including message_id,
     * posted_by, message_text, and time_posted_epoch), and the response status should be 200. If the update of the
     * message is not successful for any reason, the response status should be 400. (Client error)
     *
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void updateMessageHandler(Context context) throws JsonProcessingException {
        int messageID = Integer.parseInt(context.pathParam("message_id"));
        Message input = objectMapper.readValue(context.body(), Message.class);
        Message message = messageService.updateMessage(messageID, input);

        if (message == null)
            context.status(400);
        else
            context.json(objectMapper.writeValueAsString(message));
    }

    /**
     * The response body should contain a JSON representation of a list containing all messages posted by a particular
     * user, which is retrieved from the database. It is expected for the list to simply be empty if there are no
     * messages. The response status should always be 200.
     *
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void getAccountMessagesHandler(Context context) throws JsonProcessingException {
        int accountID = Integer.parseInt(context.pathParam("account_id"));
        List<Message> messages = messageService.getAccountMessages(accountID);

        context.json(objectMapper.writeValueAsString(messages));
    }
}