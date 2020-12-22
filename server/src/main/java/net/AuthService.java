package net;

import java.io.Closeable;

public interface AuthService extends Closeable {
    String getNickNameByLoginAndPass(String login, String password);
    int doRegistration(String login, String pass, String nick);
}
