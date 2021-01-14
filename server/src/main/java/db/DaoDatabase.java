package db;

import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoDatabase {
    private Connection connection;
    private PreparedStatement statement;

    public DaoDatabase() {
        connect();
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("org.sqlite.JDBC NOT FOUND!");
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:my_db.db");
            if (prepareDatabase()){
                System.out.println("Database is correct");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean prepareDatabase() {
        try {
            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS users\n" +
                    "(\n" +
                    "  userID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    "  login TEXT NOT NULL UNIQUE,\n" +
                    "  nickname TEXT NOT NULL,\n" +
                    "  password TEXT NOT NULL\n" +
                    ");");
            return statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public UserData getUserById(int id){
        try {
            statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            return new UserData(rs.getString("login"),
                                rs.getString("nickname"),
                                rs.getString("password"));
        } catch (SQLException e) {
            System.out.println("Error get users with id="+id);
            System.out.println(e.getMessage());
        }
        return new UserData("","","");
    }

    public List<UserData> getAllUser(){
        List<UserData> users = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM users");
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                users.add(new UserData(rs.getString("login"),
                                        rs.getString("nickname"),
                                        rs.getString("password")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public int changeUserNickname(String login, String newNickname){
        if (newNickname.length() < 3) return 0;
        try {
            statement = connection.prepareStatement("UPDATE users SET nickname = ? WHERE login = ?");
            statement.setString(1, newNickname);
            statement.setString(2, login);

            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int deleteUser(String userLogin){
        try {
            statement = connection.prepareStatement("DELETE FROM users WHERE login = ?");
            statement.setString(1, userLogin);

            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int registerUser(UserData user){
        if (user.getNickname().length() < 3) return 0;
        if (user.getLogin().length() < 3) return 0;
        if (user.getPassword().length() < 3) return 0;

        try {
            statement = connection.prepareStatement("INSERT INTO users(login, nickname, password) VALUES(?,?,?)");
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getNickname());
            statement.setString(3, DigestUtils.md5Hex(user.getPassword()).toUpperCase());
            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public String authUser(String login, String password){
        try {
            statement = connection.prepareStatement("SELECT * FROM users WHERE login=? AND password=?");
            statement.setString(1, login);
            statement.setString(2, DigestUtils.md5Hex(password).toUpperCase());
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void disconnect(){
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int changeUserPassword(String login, String oldPassword, String newPassword) {
        try {
            statement = connection.prepareStatement("UPDATE users SET password = ? WHERE login = ? AND password = ?");
            statement.setString(1, DigestUtils.md5Hex(newPassword).toUpperCase());
            statement.setString(2, login);
            statement.setString(3, DigestUtils.md5Hex(oldPassword).toUpperCase());

            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
}
