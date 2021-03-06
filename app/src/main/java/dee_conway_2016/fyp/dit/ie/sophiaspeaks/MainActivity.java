package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

@SuppressWarnings("deprecation")// speak is deprecated, but the testing device is an old device so needs that version
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String SHARED = "globals";
    Button open;
    public SharedPreferences shared;
    Vibrator buttonVibe;
    TextToSpeech voice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        shared = getSharedPreferences(SHARED, 0);
        open = (Button) findViewById(R.id.button);
        open.setOnClickListener(this);
        //build a TextTospeech service
        voice = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });

        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        buttonVibe.vibrate(100);
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);


    }



    @Override// on resume check if there is a curren logged in user
    public void onResume(){
        super.onResume();
        SharedPreferences shared = getSharedPreferences(SHARED, 0);
        String user = shared.getString("name", "LogIn");
        this.setTitle(user);
        if (shared.getString("amLogged","false").equalsIgnoreCase("true")){
            goHome();

        }

    }// if the last logged in user is a child go to the child view

    public void goHome(){
         String userType = shared.getString("usertype","user");
         if(userType.equalsIgnoreCase("child")){
             Intent intent = new Intent(this,HomeActivity.class);
             startActivity(intent);
         }else{
             Log.d("myTag", "goHome Error");
             SharedPreferences.Editor editor = shared.edit();
             editor.putString("amLogged","false");
             editor.apply();
         }

     }
}
