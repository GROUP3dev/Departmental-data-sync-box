package util;

import model.Record;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ReportUtil {

    public static void generateCSV(List<Record> records, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Header
            writer.println("Record ID,Student ID,Type,Title,Description,Status,Created At");

            // Data
            for (Record r : records) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s%n",
                        r.getRecordId(),
                        escapeSpecialCharacters(r.getStudentIdNo()),
                        escapeSpecialCharacters(r.getRecordType()),
                        escapeSpecialCharacters(r.getTitle()),
                        escapeSpecialCharacters(r.getDescription()),
                        escapeSpecialCharacters(r.getStatus()),
                        r.getCreatedAt() != null ? r.getCreatedAt().toString() : "");
            }
        }
    }

    private static String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
