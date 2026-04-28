package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GarageManager {

    private String storagePath;
    private boolean canEditSlots;

    public GarageManager() {
        loadConfig();
    }

    private void loadConfig() {
        Properties props = new Properties();

        try (InputStream input = new FileInputStream("config.properties")) {

            props.load(input);

            storagePath = props.getProperty("db.path");
            canEditSlots = Boolean.parseBoolean(
                    props.getProperty("garage.slots.editable")
            );

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void printConfig() {
        System.out.println("Storage Path: " + storagePath);
        System.out.println("Can Edit Slots: " + canEditSlots);
    }
}