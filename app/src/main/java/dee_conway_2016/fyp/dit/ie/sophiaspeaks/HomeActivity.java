package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity implements  View.OnClickListener{

    TextToSpeech voice;
    ImageButton play, album, snap;
    public static final String SHARED = "globals";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        play = (ImageButton) findViewById(R.id.playGame);
        snap = (ImageButton) findViewById(R.id.cameraButton);
        album = (ImageButton) findViewById(R.id.albumButton);
        play.setOnClickListener(this);
        snap.setOnClickListener(this);
        album.setOnClickListener(this);

    }

    //Inflate the menu bar with icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }

    /*
    * (non-Javadoc)
    * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        SharedPreferences shared = getSharedPreferences(SHARED, 0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("name",null);
        editor.putString("email",null);
        editor.putString("amLogged","false");
        editor.putString("type",null);
        editor.commit();


        finish();
        return true;


    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences shared = getSharedPreferences(SHARED, 0);
        String user = shared.getString("name", "nancy");
        this.setTitle("Logged in:" +user);
        if (shared.getString("amLogged","false").equalsIgnoreCase("true")){


        }
        else{
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View v) {

        if (v == play) {
            //go to game activity
            Intent intent = new Intent(this, MatchingGame.class);
            startActivity(intent);

        }
        if (v == snap) {
            //go to camera
            Intent intent = new Intent(this, TakePhoto.class);
            startActivity(intent);

        }
        if (v == album) {
            //show album
                String sayThis = "lets have a look at some photos";
                //voice.speak(sayThis, TextToSpeech.QUEUE_FLUSH, null);
                Intent intent = new Intent(this,AlbumActivity.class);
                startActivity(intent);


        }
    }
}

