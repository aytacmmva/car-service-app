package model;

import java.io.*;

public class StorageManager {

    public static <T> void saveState(T app, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(app);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T loadState(String filePath, Class<T> clazz) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return clazz.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            try {
                return clazz.getDeclaredConstructor().newInstance(); // ✔️ düzgün yol
            } catch (Exception ex) {
                throw new RuntimeException("Obyekt yaradıla bilmədi", ex);
            }
        }
    }
}