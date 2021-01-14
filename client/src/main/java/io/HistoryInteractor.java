package io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class HistoryInteractor implements HistoryService<String> {

    private String pathHistory;
    private File fileHistory;
    private RandomAccessFile randomAccessFile;

    public HistoryInteractor(String nickname) {
        this.pathHistory = "history_"+nickname+".txt";
        createNewFile();
    }

    private void createNewFile(){
        fileHistory = new File(pathHistory);
        if (!fileHistory.exists()){
            try {
                fileHistory.createNewFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void save(String s) {
        System.out.println("Caving to file length: " + s.length());
        try {
            randomAccessFile = new RandomAccessFile(fileHistory, "rw");
            randomAccessFile.seek(randomAccessFile.getFilePointer());
            randomAccessFile.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String load() {
        System.out.println("Loading from file history");
        try {
            randomAccessFile = new RandomAccessFile(fileHistory, "r");
            return randomAccessFile.readUTF();
        } catch (IOException e) {}
        finally {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";

    }

    @Override
    public void changeFile(String newFile) {
        this.pathHistory = "history_"+newFile+".txt";
        createNewFile();
    }
}
