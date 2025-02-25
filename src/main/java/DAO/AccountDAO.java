package DAO;

import Model.Account;
import Util.ConnectionUtil;
import org.jetbrains.annotations.Nullable;

import java.sql.*;

public class AccountDAO {

    /**
     * @param account the account to be created, lacking its generated account_id
     * @return the created account, populated with its generated account_id or null if any error occurs.
     */
    @Nullable
    public Account createAccount(Account account) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String SQL = "INSERT INTO account(username, password) VALUES (?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                int generated_account_id = resultSet.getInt(1);
                account.setAccount_id(generated_account_id);
                return account;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * @return the account for a given username or null if no such account exists
     */
    @Nullable
    public Account getAccount(String username) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String SQL = "SELECT * FROM account WHERE username=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int account_id = resultSet.getInt(1);
                String account_username = resultSet.getString(2);
                String account_password = resultSet.getString(3);

                return new Account(account_id, account_username, account_password);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * @return the account for a given account_id or null if no such account exists
     */
    @Nullable
    public Account getAccount(int account_id) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String SQL = "SELECT * FROM account WHERE account_id=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setInt(1, account_id);

            ResultSet returnedKeys = preparedStatement.executeQuery();
            if (returnedKeys.next()) {
                String account_username = returnedKeys.getString(2);
                String account_password = returnedKeys.getString(3);

                return new Account(account_id, account_username, account_password);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * @param account an account containing only username and password
     * @return the account with its username, password, and account_id
     */
    public Account loginAccount(Account account) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String SQL = "SELECT * FROM account WHERE username=? AND password=?;";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int account_id = resultSet.getInt(1);
                String account_username = resultSet.getString(2);
                String account_password = resultSet.getString(3);

                return new Account(account_id, account_username, account_password);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
