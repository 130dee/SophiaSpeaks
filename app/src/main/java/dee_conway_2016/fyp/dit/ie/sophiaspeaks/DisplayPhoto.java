package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
@SuppressWarnings("deprecated")
public class DisplayPhoto extends AppCompatActivity implements  View.OnClickListener{

    ImageView displayArea;
    ImageButton kill,what,sad,happy;
    TextToSpeech voice;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        voice=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });

        Intent intent = getIntent();

        File imgFile = new  File(intent.getStringExtra("photo"));


        if(imgFile.exists())
        {
            displayArea = (ImageView) findViewById(R.id.imageView);
            Bitmap bmap = BitmapFactory.decodeFile(intent.getStringExtra("photo"));//Uri.fromFile(imgFile)
            displayArea.setImageBitmap(bmap);

        }


        kill = (ImageButton) findViewById(R.id.killButton);
        kill.setOnClickListener(this);
        happy = (ImageButton) findViewById(R.id.happyButton);
        happy.setOnClickListener(this);
        sad = (ImageButton) findViewById(R.id.sadButton);
        sad.setOnClickListener(this);
        what = (ImageButton) findViewById(R.id.whatButton);
        what.setOnClickListener(this);

        promptSpeechInput();


    }
    private void promptSpeechInput() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "speech promt");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech_not_supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceControl(result.get(0).trim().toString());
                }else if(null == data){
                    finish();
                }
            }
        }
    }


    public void voiceControl(String command){
        String toSpeak = "Sorry, I didn't understand that command.  Please try again";
        Boolean understood=false;
        if(command.equals("kill")){
            toSpeak ="Not sending this photo to mommy";
            understood=true;
        }
        else if(command.equals("happy")){
            toSpeak ="I really love this photo";
            understood=true;
        }
        else if(command.equals("sad")){
            toSpeak ="This image makes me sad";
            understood=true;
        }
        else if(command.equals("what")){
            toSpeak ="Hey mommy, what is this";
            understood=true;
        }
        if(understood){
            voice.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
        else{
            voice.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            promptSpeechInput();
        }
    }

    @Override
    public void onClick(View v) {
        String toSpeak = "";
        if(v==kill){

            toSpeak ="Not sending this photo to mommy";
        }
        if(v==what){

            toSpeak ="Hey mommy, what is this";
        }
        if(v==sad){

            toSpeak ="This image makes me sad";
       }
        if(v==happy){

            toSpeak ="I really love this photo";
        }
        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
        voice.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //finish();

    }
}
