package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Locale;
@SuppressWarnings("deprecation")// speak is deprecated, but the testing device is an old device so needs that version
public class ParentHome extends AppCompatActivity implements View.OnClickListener {

    public static final String SHARED = "globals";
    SharedPreferences shared;
    TextToSpeech voice;

    ImageButton checkmess, editIms, addSub, child, worker;
    Vibrator buttonVibe;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // get a vibrator class to notify the user of button clicks
        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //assign the views and the buttons with click listeners
        checkmess = (ImageButton) findViewById(R.id.ckmessages);
        editIms = (ImageButton) findViewById(R.id.editIms);
        addSub = (ImageButton) findViewById(R.id.subBtn);
        child = (ImageButton) findViewById(R.id.childBtn);
        worker = (ImageButton) findViewById(R.id.workerBtn);
        checkmess.setOnClickListener(this);
        editIms.setOnClickListener(this);
        addSub.setOnClickListener(this);
        child.setOnClickListener(this);
        worker.setOnClickListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //build a TextTospeech service
        voice = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });
    }

    @Override//ensure the user is logged in else go to login
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
        if (v == checkmess) {
            Intent intent = new Intent(this, ParentView.class);
            startActivity(intent);

        }
        if (v == editIms) {
            Intent intent = new Intent(this, ImageEditActivity.class);
            startActivity(intent);


        }
        if (v == addSub) {
            Intent intent = new Intent(this, Register.class);
            intent.putExtra("type", "child");
            startActivity(intent);

        }
        if (v == child) {
            Intent intent = new Intent(this, HomeActivity.class);

            startActivity(intent);

        }
        if (v == worker) {
            Intent intent = new Intent(this, AddGame.class);
            startActivity(intent);

        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ParentHome Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://dee_conway_2016.fyp.dit.ie.sophiaspeaks/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ParentHome Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://dee_conway_2016.fyp.dit.ie.sophiaspeaks/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    
}
