package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import static net.CommandsRouter.*;

public class ClientHandler {
    private Server server = null;
    private Socket socket = null;
    private DataOutputStream dataOutputStream;
    private DataInputStream inputStream;
    private String nickname ="";
    private String login ="";

    public void setNickname(String nickname) {
        if (nickname.length() < 3) return;
        this.nickname = nickname;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            inputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            socket.setSoTimeout(120000);
            new Thread(()->{
                try {
                    auth();
                    readMessages();
                } catch (IOException ex) {
                    System.out.println(ex.getLocalizedMessage());
                } finally {
                    closeConnections();
                }
            }).start();

        } catch (SocketException e){
            server.unsubscribe(this);
            System.out.println(nickname + " отключился");
        }catch (IOException ex) {
            System.out.println("Непредвиденная ошибка");
        }
    }

    private void closeConnections() {
        server.unsubscribe(this);
        server.broadcastMessage(this, nickname+" вышел из чата");
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readMessages() throws IOException  {
        while (true){
            String str = inputStream.readUTF();
            if(str.startsWith(ROOT)) {

                if (str.equals(END_CONNECTION)) {
                    dataOutputStream.writeUTF(END_CONNECTION);
                    login = "";
                    break;
                }

                if (str.startsWith(PRIVATE_MESSAGE)) {
                    String[] token = str.split("\\s+", 3);
                    if (token.length < 3){
                        continue;
                    }
                    server.messageToNickname(this, token[1], token[2]);
                }
                if (str.startsWith(CHANGE_NICKNAME)){
                    String[] token = str.split("\\s+", 2);
                    if (server.getDbService().changeNickName(login, token[1]) == 1){
                        server.broadcastMessage(this, nickname + " сменил(а) ник на " + token[1]);
                        dataOutputStream.writeUTF(str);
                        server.updateNickname(nickname, token[1]);

                    } else {
                        server.messageToNickname(this, nickname, "Ошибка смены ника. Ник не должен быть коротким!");
                    }
                }

            } else {
                server.broadcastMessage(this, str);
            }
        }
    }

    private void auth() throws IOException {
        while (true){
            String str = inputStream.readUTF();
            if (str.startsWith(START_AUTH)){
                String[] token = str.split("\\s");
                String newNick = server.getDbService().getNickNameByLoginAndPass(token[1], token[2]);

                login = token[1];

                if (newNick != null){
                    if (!server.isLoginAuthenticated(token[1])){
                        nickname = newNick;
                        sendMessage(AUTH_OK + nickname);
                        server.subscribe(this);
                        server.broadcastMessage(this, "Клиент " + nickname + " подключился!");
                        break;
                    } else {
                        sendMessage("Учетная запись уже используется");
                    }
                } else {
                    sendMessage("Неверный логин или пароль");
                }
            }

            if (str.startsWith(START_REG)){
                String[] token = str.split("\\s");
                if(token.length < 4){
                    continue;
                }
                int isRegistration = server.getDbService()
                        .doRegistration(token[1], token[2], token[3]);
                if(isRegistration == 1){
                    sendMessage(REG_OK);
                } else {
                    sendMessage(REG_ERROR);
                }
            }
        }
    }

    void sendMessage(String message){
        try{
            dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }
}
