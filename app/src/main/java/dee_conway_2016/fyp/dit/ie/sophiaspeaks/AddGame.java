package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddGame extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    //attributes needed by the activity to complete
    public static final String GAMENAME = "game";
    public static final String EMAIL = "email";
    public static final String TIME = "time";
    public static final String DATE = "datestring";
    public static final String SHARED = "globals";
    //urls to be used in the volley requests
    public static final String REG_URL = "http://52.50.76.1/sophiaFYP/makegame.php";
    public static final String GAMES_URL = "http://52.50.76.1/sophiaFYP/getgames.php?email=";
    //a shared preference file to allow duser data to be shared across all activities
    SharedPreferences shared;
    //button to add a new theme game
    public Button addNewTheme;
    EditText gameTag;
    ListView theme;
    Vibrator buttonVibe;
    TextToSpeech voice;

    // list to staore all themes an populate the list view
    ArrayList<Themes> listOfThemes = new ArrayList<>();
    //GamesAdapter myGameListAdapter = null;


    @Override//listener on the arrayadapter to identify which item has been clicked
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Alert Box to show results from previous theme games
        int pos = position;
        String themeItem = (String) theme.getItemAtPosition(pos);
        AlertDialog.Builder adb = new AlertDialog.Builder(
                AddGame.this);
        adb.setTitle("Theme: "+parent.getItemAtPosition(pos)+" / n");
                adb.setMessage("Correct/incorrect");
        adb.setPositiveButton("Ok", null);
        adb.show();
    }

    //internal class of themes, data will be parsed to an instance
    // of themes and placed in the list of themes array
    class Themes{
        public String id;
        public String gametheme;
        public String correct;
        public String incorrect;
        public String datetime;
        public String currentGame;
        public String datestring;

    }



    @Override//initialize and run the activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //make an instance of vibrator so that the buttons will vibrate
        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        shared = getSharedPreferences(SHARED, 0);
        //get the logged in user email from the shared pref file
        String email = shared.getString("email","email");
        getJsonArrayOfGames(email);

        addNewTheme       = (Button) findViewById(R.id.addNewGameButton);
        addNewTheme.setOnClickListener(this);
        gameTag          = (EditText) findViewById(R.id.addGameEditText);
        theme            = (ListView)findViewById(R.id.listOfThemes);

        //An Array of strings to act as a place holder for the array of themes
        String [] thisStuff= new String[]{"stuff","more Stuff", "Even Mpore Stuff","Stuff to check scrolling","some more","seems like scrooling is not working, I don't know why","last Item on the list"};//Arrayadapter to parse out the string array in to a list of clickable items
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, thisStuff);

        //set the adapter on the listview tpo create a list of themes
        theme.setAdapter(myAdapter);
        // place a listener on the list to act when clicked
        theme.setOnItemClickListener(this);

        voice=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }

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

    @Override//ensure the user is logged in else go to login
    public void onResume(){
        super.onResume();
        SharedPreferences shared = getSharedPreferences(SHARED, 0);
        String user = shared.getString("name", "nancy");
        this.setTitle("Logged in:" + user);
        if (shared.getString("amLogged","false").equalsIgnoreCase("false")){
            finish();
        }

    }

    //A listener that listens for clicks on any button
    @Override
    public void onClick(View v) {
        buttonVibe.vibrate(100);
        if(v==addNewTheme){
            //add a new theme to database and start theme game
            //clear the txt box
            createGame();

        }
    }

    public void createGame(){

        gameTag.setError(null);

        String gameName = gameTag.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        //check that data has been entered
        if (TextUtils.isEmpty(gameName)){
            gameTag.setError(getString(R.string.error_field_required));
            focusView = gameTag;
            cancel = true;
        }


        if(!cancel){
            cancel = false;
            addThisNewGame(gameName);

        }

    }

    // volley request to insert a new theme game to the theme game table
    public void addThisNewGame(String addthisStringToTheGameList){
        Log.d("myTag", getString(R.string.trying_to_login));
        final ProgressDialog waiting = ProgressDialog.show(this, "Creating Game...",
                "Authenticating User details..", false, false);
        // log the time of the theme game and insert it in to the table
        Long systime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yy");
        // the time from milliseconds to a recognisable format
        final String dateString = ((Long) System.currentTimeMillis()).toString();
        final String dateView = sdf.format(systime);


        final String addThis = addthisStringToTheGameList;
        final String checkThisEmail = shared.getString("email", "default.com");
        StringRequest myQuery = new StringRequest(Request.Method.POST,REG_URL,new Response.Listener<String>(){
            @Override
            public void onResponse(String comeBack){
                Log.d("myTag", comeBack);
                if(comeBack.trim().equalsIgnoreCase("success")){
                    Log.d("myTag", comeBack);
                    gameTag.setText(null);
                    waiting.dismiss();
                }else{
                    Log.d("myTag", comeBack);
                    gameTag.setText(null);
                    waiting.dismiss();
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        voice.speak("There may be a network error...", TextToSpeech.QUEUE_FLUSH, null);
                        Log.d("myTag", "VolleyError Addgame");
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put(GAMENAME, addThis);
                map.put(EMAIL, checkThisEmail);
                map.put(TIME, dateString);
                map.put(DATE,dateView);
                return map;
            }
        };

        RequestQueue thisQ = Volley.newRequestQueue(this);
        thisQ.add(myQuery);
    }

    //volley request to get a list of previous games from the database and the results
    public void getJsonArrayOfGames (String mail){
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Authenticating User details..", false, false);
        String finalURL= GAMES_URL+mail;

        JsonArrayRequest jsonQuery = new JsonArrayRequest(JsonArrayRequest.Method.GET, finalURL,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        waiting.dismiss();
                        displayGameList(response);
                        Log.d("myTag", "success gamesGet");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        waiting.dismiss();
                        Log.d("myTag", "Error gamesGet");
                    }
                }
        ) {
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }

    // parse all the results from the volley query into an array that will be used to populate the llist view
    public void displayGameList(JSONArray listOfGamesToBeParsed){

        try{
            for(int i=0;i<listOfGamesToBeParsed.length();i++) {
                JSONObject obj;
                obj = listOfGamesToBeParsed.getJSONObject(i);
                Themes themeRow = new Themes();
                themeRow.id = obj.getString("id");
                themeRow.gametheme = obj.getString("name");
                themeRow.correct = obj.getString("correct");
                themeRow.incorrect = obj.getString("incorrect");
                themeRow.datetime = obj.getString("datetime");
                themeRow.currentGame = obj.getString("currentgame");
                themeRow.datestring = obj.getString("datestring");

                listOfThemes.add(themeRow);
            }


        }catch(JSONException e){
            e.printStackTrace();
        }
        //populateListView();
    }

}


