package net;

import db.DaoDatabase;
import db.UserData;


public class DatabaseRepository implements OperationMutable {

    private DaoDatabase database;

    public DatabaseRepository() {
        database = new DaoDatabase();

    }

    @Override
    public String getNickNameByLoginAndPass(String login, String password) {
        return database.authUser(login, password);
    }

    @Override
    public int doRegistration(String login, String pass, String nick) {
        return database.registerUser(new UserData(login, pass, nick));
    }

    @Override
    public int changeNickName(String login, String newNickname) {
        return database.changeUserNickname(login, newNickname);
    }

    @Override
    public int changePassword(String login, String oldPassword, String newPassword) {
        return database.changeUserPassword(login, oldPassword, newPassword);
    }

    @Override
    public void close() {
        database.disconnect();
    }
}
