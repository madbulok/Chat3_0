package net;

interface OperationMutable extends AuthService {
    int changeNickName(String login, String newNickname);
    int changePassword(String login, String oldPassword, String newPassword);
}
