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
            if (srcDir.mkdirs()) {
                System.out.println("Source directory was missing; created " + srcDir.getAbsolutePath() + " so future syncs can run.");
            } else {
                System.err.println("Failed to create source directory at " + srcDir.getAbsolutePath() + ". Please ensure the path is writable.");
            }
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

    public static void main(String[] args) {
        String source = args.length > 0 ? args[0] : "data/source";
        String destination = args.length > 1 ? args[1] : "data/destination";

        System.out.println("Starting SyncManager manually with source=" + source + " destination=" + destination);
        SyncManager manager = new SyncManager(source, destination);
        manager.sync();
    }
}
