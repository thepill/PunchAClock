package de.jbdevelop.punchaclock.helper;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Modifier;

import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.db.TimePeriodRepository;
import de.jbdevelop.punchaclock.db.WifiRepository;
import de.jbdevelop.punchaclock.model.TimePeriod;

/**
 * Created by Jan on 02.05.2016.
 */
public class PACBackup extends AppCompatActivity {
    public static final String PARTIAL_FILEPATH = "/pac/";
    public static final String LOGCAT_TAG = "PAC-Backup";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
    }

    public void backupAreas(View view) {
        writeEntry(AreaRepository.getInstance().getAll(), "areas");
    }

    public void backupTimeperiods(View view) {
        writeEntry(TimePeriodRepository.getInstance().getAll(), "timeperiods");
    }

    public void backupWifis(View view)  {
        writeEntry(WifiRepository.getInstance().getAll(), "wifis");
    }

    private void writeEntry(Object object, String name){
        File file = getFile(name);
        FileOutputStream f = null;

        try {
            f = new FileOutputStream(file, false);
            OutputStreamWriter streamWriter = new OutputStreamWriter(f);

            String json ="";

            try {
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).create();
                json = gson.toJson(object);
            } catch (Exception e) {
                Log.e("json", e.toString());
            }

            streamWriter.append(json);
            streamWriter.close();
            f.close();
            Toast.makeText(this,"Wrote to " + file.getPath(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e(LOGCAT_TAG, e.toString());
        } catch (IOException e) {
            Log.e(LOGCAT_TAG, e.toString());
        }
    }


    private static File getFile(String name){
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + PARTIAL_FILEPATH);
        dir.mkdirs();
        return new File(dir, name + ".json");
    }
}
