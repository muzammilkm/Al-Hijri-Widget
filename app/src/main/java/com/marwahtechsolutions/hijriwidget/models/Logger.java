package com.marwahtechsolutions.hijriwidget.models;

import android.nfc.Tag;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by MKhaja on 10/24/2017.
 */

public class Logger {
    public static void d(String tag, String message) {
        Log.d(tag, message);
        File backupPath = Environment.getExternalStorageDirectory();
        backupPath = new File(backupPath.getPath() + "/Android/data/com.marwahtechsolutions.hijriwidget/files");
        if (!backupPath.exists()) {
            backupPath.mkdirs();
        }
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            File file = new File(backupPath.getPath() + "/log.html");
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(String.format("<b>%s</b> : <i>%s - %s</i><br>", DateFormat.getDateTimeInstance().format(new Date()), tag, message));
            bw.newLine();
        } catch (IOException e) {
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
