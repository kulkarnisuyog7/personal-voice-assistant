package industries.zk.personalvoiceassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech myTTS;
    private SpeechRecognizer mySpeechRegonizer;

    TextView mytext;
    BottomAppBar bottomAppBar;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
        fab = (FloatingActionButton) findViewById(R.id.floatActionBtn);
        mytext = (TextView) findViewById(R.id.textView);

        //testing purpose


        //check for the permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},1);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_ADMIN},1);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH},1);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CHANGE_WIFI_STATE},1);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},1);

        }

        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra( RecognizerIntent.EXTRA_MAX_RESULTS,1);
                if(!myTTS.isSpeaking()){
                    mySpeechRegonizer.startListening(intent);
                }


            }
        });
        setSupportActionBar(bottomAppBar);

        initializeTextToSpeech();
        initializeSpeechRecognizer();
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)){
            mySpeechRegonizer = SpeechRecognizer.createSpeechRecognizer(this);
            mySpeechRegonizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    mytext.setText("Listening....");
                    fab.setImageResource(R.drawable.ic_more_horiz_black_24dp);
                }

                @Override
                public void onBeginningOfSpeech() {
                    mytext.setText("Listening....");
                    fab.setImageResource(R.drawable.ic_more_horiz_black_24dp);
                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                    mytext.setText("Buffer Recieved");
                }

                @Override
                public void onEndOfSpeech() {
                    mytext.setText("Tap On Mic");
                    fab.setImageResource(R.drawable.ic_mic_none_black_24dp);
                }

                @Override
                public void onError(int error) {
                    if (error == 3) {
                        mytext.setText("Audio Recording failed");
                    }
                    else if(error == 5){
                        mytext.setText("Other Client Side Error");
                    }
                    else if (error == 9){
                        mytext.setText("Insufficient Permissions");
                        speak("Permissions are not Granted");
                    }
                    else if (error == 6){
                        mytext.setText("Speech Timeout");

                    }
                    else if (error == 7){
                        speak("Sorry, Not Recognized You Properly");
                    }else if (error == 4){
                        mytext.setText("Internet Connection..!");
                        speak("Check Your Internet Connection");
                    }
                    else{
                        mytext.setText("error "+error);
                    }
                }
                @Override
                public void onResults(Bundle bundle) {

                    List<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(results.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {
                    mytext.setText("Event");
                }
            });
        }
    }

    private void processResult(String command) {
        command = command.toLowerCase();

        if (command.indexOf("open") != -1) {
            List<PackageInfo> packageList = getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packageList.size(); i++){
                PackageInfo packageInfo = packageList.get(i);

                String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString().toLowerCase();
                String packName = packageInfo.packageName;

                String userAppName = command.substring(4,command.length());
                userAppName = userAppName.trim().replace(" ","");
                appName = appName.trim().replace(" ","");
                packName = packName.trim();

                if (userAppName.equals(appName)){
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packName);
                    speak("Opening "+appName+"....");
                    startActivity(launchIntent);
                    break;
                }else{
                    speak(userAppName+" is not installed in this device");
                }
            }
        }
        else if (command.indexOf("about") != -1) {
            if (command.indexOf("you") != -1 || command.indexOf("your") != -1 || command.indexOf("yourself") != -1) {
                speak("I am Voice Assistant ,You can Call me Natasha and  I am here to assist you ");
            }
        }
        else if (command.indexOf("what") != -1) {
            if (command.indexOf("name") != -1) {
                speak("My Name is Natasha");
            }
            if (command.indexOf("time") != -1) {
                Date now = new Date();
                String time = DateUtils.formatDateTime(this, now.getTime(), DateUtils.FORMAT_SHOW_TIME);
                speak("The Time Now is " + time);
            }
            if (command.indexOf("date") != -1) {
                Date now = new Date();
                String date = DateUtils.formatDateTime(this, now.getTime(), DateUtils.FORMAT_SHOW_YEAR);
                speak("The Date Today is " + date);
            }
        }
        else if (command.indexOf("search") != -1){
            String query = command.substring(6,command.length());
            String squery = null;
            try {
                squery = URLEncoder.encode(query,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            squery = "https://www.google.com/search?q="+squery;

            Intent intent = new Intent(MainActivity.this,WebActivity.class);
            intent.putExtra("query",squery);
            startActivity(intent);
            speak("Taking You To Google...");
        }else if(command.indexOf("locate")!= -1){
            // Map point based on address
            String sloc = command.substring(6,command.length());
            Uri location = Uri.parse("geo:0,0?q="+sloc);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
            startActivity(mapIntent);
        }else if (command.indexOf("turn") != -1 || command.indexOf("switch") != -1){
            if (command.indexOf("on") != -1){
                if (command.indexOf("bluetooth") != -1){
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    bluetoothAdapter.enable();
                    speak("Bluetooth turned on");
                }
                else if (command.indexOf("wi-fi") != -1){
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    speak("WIFI turned on");
                }
            }else if(command.indexOf("off") != -1){
                if (command.indexOf("bluetooth") != -1){
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    bluetoothAdapter.disable();
                    speak("Bluetooth turned off");
                }
                else if(command.indexOf("wi-fi") != -1){
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(false);
                    speak("WIFI turned off");
                }
            }
        }
        else {
            speak("There is no command like :"+command);
        }

    }

    private void initializeTextToSpeech() {
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(myTTS.getEngines().size() == 0){
                    Toast.makeText(MainActivity.this,"There is no text to speech engine on this system",Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    myTTS.setLanguage(Locale.UK);
                    speak("ready");
                }
            }
        });
    }

    private void speak(String message) {
        if(Build.VERSION.SDK_INT >= 21){
            myTTS.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        }
        else{
            myTTS.speak(message,TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_app_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.appBar_about){
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

}
