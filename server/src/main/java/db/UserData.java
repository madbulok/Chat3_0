package db;

public class UserData {
    private String login;
    private String password;
    private String nickname;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UserData(String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
    }
}
