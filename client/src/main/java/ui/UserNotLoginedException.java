package ui;

public class UserNotLoginedException extends Exception {
    public UserNotLoginedException() {
    }

    public UserNotLoginedException(String message) {
        super(message);
    }
}
