package com.game.dhanraj.hitthemouse;

import android.database.Cursor;
import android.database.SQLException;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

public class MainActivity extends AppCompatActivity {

    private static final String PREFERENCES_NAME = "MyPreferences";
    private static final int SOUND = 1;
    private boolean soundEnabled = true;
    private Hit hit1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        //whack=(WhackAMoleView)findViewById(R.id.whack);
        hit1 = (Hit) findViewById(R.id.hit);
        hit1.setKeepScreenOn(true);
        // whack.setKeepScreenOn(true);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        DatabaseAdapter db = new DatabaseAdapter(this);
        try{
            //readXML();
            db.open();
        } catch(SQLException sqle){
            throw sqle;
        }
        Cursor c = db .getRecord(1);
        startManagingCursor(c);
        if(c.moveToFirst())
        {
            do{
                soundEnabled = Boolean.parseBoolean(c.getString(1));
            }while(c.moveToNext());
        }
        db.close();
        hit1.soundOn = soundEnabled;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem sound = menu.add(0, SOUND, 0, "Backgound sound");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SOUND:
                String soundEnabledText = "Sound On";
                if (soundEnabled) {
                    soundEnabled = false;
                    hit1.soundOn = false;
                    soundEnabledText = "Sound Off";
                    // Toast.makeText(this, "Sound off", Toast.LENGTH_SHORT).show();
                } else {
                    soundEnabled = true;
                    hit1.soundOn = true;
                    //  Toast.makeText(this, "Sound on", Toast.LENGTH_SHORT).show();
                }
               /* DatabaseAdapter db = new DatabaseAdapter(this);
                try{
                    db.open();
                }catch (SQLException sqle){
                    throw sqle;
                }
                db.insertOrUpdateRecord(Boolean.toString(soundEnabled));
                db.close();

               // writeXML();*/
                Toast.makeText(this, soundEnabledText, Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }


    public void writeXML() {
        try {
            String FileName = "settings";
            FileOutputStream out = openFileOutput(FileName + ".xml", MODE_WORLD_WRITEABLE);
            StringBuffer profileXML = new StringBuffer();
            profileXML.append("<sound_setting>" + soundEnabled + "</sound_setting>\n");
            OutputStreamWriter osw = new OutputStreamWriter(out);
            osw.flush();
            ;
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readXML() throws XmlPullParserException,
            IOException {
        String tagName = "";
        XmlPullParserFactory factory =
                XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        try {
            InputStream in =
                    openFileInput("settings.xml");
            InputStreamReader isr = new
                    InputStreamReader(in);
            BufferedReader reader = new BufferedReader(isr);
            String str;
            StringBuffer buf = new StringBuffer();
            while ((str = reader.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            xpp.setInput(new StringReader(buf.toString()));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType ==
                        XmlPullParser.END_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG) {
                    tagName = xpp.getName();
                } else if (eventType == XmlPullParser.END_TAG) {
                } else if (eventType == XmlPullParser.TEXT) {
                    if (tagName.contains("sound_setting")) {
                        soundEnabled =
                                Boolean.parseBoolean
                                        (xpp.getText().toString());
                    }
                }
                eventType = xpp.next();
            }


        } catch (Exception FileNotFoundException) {
            System.out.println("File Not Found");

        }
    }
}
