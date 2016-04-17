package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.Map;

public class AddGame extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    public static final String GAMENAME = "game";
    public static final String EMAIL = "email";
    public static final String TIME = "time";
    public static final String DATE = "datestring";
    public static final String SHARED = "globals";
    SharedPreferences shared;
    public static final String REG_URL = "http://52.50.76.1/sophiaFYP/makegame.php";
    public static final String GAMES_URL = "http://52.50.76.1/sophiaFYP/getgames.php?email=";

    public Button addNewTheme,viewPreviousGame;
    EditText gameTag;
    ListView theme;
    ListAdapter adapter;
    Vibrator buttonVibe;


    ArrayList<Themes> listOfThemes = new ArrayList<Themes>();
    //GamesAdapter myGameListAdapter = null;

    static ArrayList<String> gameRow;

    @Override
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


    class Themes{
        public String id;
        public String gametheme;
        public String correct;
        public String incorrect;
        public String datetime;
        public String currentGame;
        public String datestring;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        shared = getSharedPreferences(SHARED, 0);

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

        //check if email has been entered, that it is a valid email address
        //and that it has not been registered before
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

    public void addThisNewGame(String addthisStringToTheGameList){
        Log.d("myTag", getString(R.string.trying_to_login));
        final ProgressDialog waiting = ProgressDialog.show(this, "Creating Game...",
                "Authenticating User details..", false, false);
        Long systime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yy");
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


