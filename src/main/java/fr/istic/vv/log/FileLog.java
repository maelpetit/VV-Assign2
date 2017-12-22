package fr.istic.vv.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLog {

    public static void writeLog(String fileName, String message){
        String projectDir = System.getProperty("user.dir");
        try {
            File file = new File(projectDir + "/log/" + fileName + ".csv"); //"-" + LocalDateTime.now().toString().replace(':', '_')
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            fw.append(message);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
