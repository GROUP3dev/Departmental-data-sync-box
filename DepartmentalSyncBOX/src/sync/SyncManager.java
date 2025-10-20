package sync;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SyncManager {

    private String sourceFolder;
    private String destinationFolder;

    public SyncManager(String sourceFolder, String destinationFolder) {
        this.sourceFolder = sourceFolder;
        this.destinationFolder = destinationFolder;
    }

    public void sync() {
        File srcDir = new File(sourceFolder);
        File destDir = new File(destinationFolder);

        if (!srcDir.exists()) {
            System.out.println("Source directory does not exist!");
            return;
        }

        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File[] files = srcDir.listFiles();
        if (files == null) {
            System.out.println("No files to sync.");
            return;
        }

        for (File file : files) {
            try {
                File destFile = new File(destDir, file.getName());
                Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Synced: " + file.getName());
            } catch (IOException e) {
                System.err.println("Failed to sync: " + file.getName());
                e.printStackTrace();
            }
        }
    }
}
