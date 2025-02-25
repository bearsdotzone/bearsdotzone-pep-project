package Service;

import DAO.AccountDAO;
import Model.Account;
import org.jetbrains.annotations.Nullable;

public class AccountService {
    private final AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    /**
     * The registration will be successful if and only if the username is not blank, the password is at least 4
     * characters long, and an Account with that username does not already exist. If all these conditions are met, the
     * response should contain the Account, including its account_id.
     *
     * @return the newly created account if the operation was successful. Returns null if unsuccessful.
     */
    @Nullable
    public Account createAccount(Account account) {
        if (account.getUsername().isEmpty())
            return null;
        if (account.getPassword().length() < 4)
            return null;
        if (accountDAO.getAccount(account.getUsername()) != null)
            return null;
        return accountDAO.createAccount(account);
    }

    /**
     * The login will be successful if and only if the username and password provided match a real account existing on
     * the database. If successful, the response body should contain the account, including its account_id.
     *
     * @return the account of the user including its account_id. Otherwise, null if an account could not be retrieved
     * for any reason.
     */
    @Nullable
    public Account loginUser(Account account) {
        return accountDAO.loginAccount(account);
    }

}
