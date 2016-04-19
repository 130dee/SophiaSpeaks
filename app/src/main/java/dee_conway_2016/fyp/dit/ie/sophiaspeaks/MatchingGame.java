package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
@SuppressWarnings("deprecation")// speak is deprecated, but the testing device is an old device so needs that version
public class MatchingGame extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    //attributes needed to successfully implement the activity
    //URLs used in the volley requests
    public static final String SHOW_THIS_URL = "http://52.50.76.1/sophiaFYP/getthreeimages.php?email=";
    public static final String UPDATE_GUESS_URL = "http://52.50.76.1/sophiaFYP/updateguess.php?email=";
    public static final String SHARED = "globals";
    //the shared preference file
    SharedPreferences shared;
    // the image views used to show the three game images
    ImageView a, b, c;
    //button to start a new game
    ImageButton nextGame;
    //layout used to change the background colour on success or not
    LinearLayout view;
    //instance of the voice used to relay data
    TextToSpeech voice;
    // counter to keep track of the correct image
    int n;
    //strings used to store the image data that will be used to update the game data
    String correctAnswer, iAmTheId, iAmTheResult, iAmTheRightimage, iAmTheGuessimage, correctQuestion;
    Vibrator buttonVibe;
    // array used to store the three images
    ArrayList<GamesImage> threeImagesForGame;

    @Override//method to run the activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //instance of the vibrator to make the screen vibrate on button activity
        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // used to acces the shared prefence file
        shared = getSharedPreferences(SHARED, 0);
        //assign the views and button
        view        = (LinearLayout) findViewById(R.id.gameBackground);
        nextGame    = (ImageButton) findViewById(R.id.nextGameBtn);
        a           = (ImageView) findViewById(R.id.webView);
        b           = (ImageView) findViewById(R.id.webView2);
        c           = (ImageView) findViewById(R.id.webView3);
        //put click listeners on all relavant areas
        nextGame.setOnClickListener(this);
        a.setOnTouchListener(this);
        b.setOnTouchListener(this);
        c.setOnTouchListener(this);

        //enable the imageclick used to disable buttons after clicking so multiple images cannot be selected
        enableClick();
        //list of images used in the game
        threeImagesForGame = new ArrayList<GamesImage>();
        // instantiate a voice
        voice = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });
        //welcome the user to the game
        voice.speak("welcome to the matching game...",TextToSpeech.QUEUE_FLUSH,null);
        // used to get the images form the server,
        getImageListFromServer(shared.getString("email", "130dee@gmail.com"));

    }
    //volley request to get images from the server
    public void getImageListFromServer(String email) {
        final ProgressDialog waiting = ProgressDialog.show(this, "Searching...",
                "Getting Next Image..", false, false);
        String finalURL = SHOW_THIS_URL + email;

        JsonArrayRequest jsonQuery = new JsonArrayRequest(JsonArrayRequest.Method.GET, finalURL, null,
                new Response.Listener<JSONArray>() {

                    @Override//on success, parse the data
                    public void onResponse(JSONArray response) {
                        waiting.dismiss();
                        parseJSONtoList(response);
                    }

                },//on error report and log the error
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        voice.speak("There may be a network error", TextToSpeech.QUEUE_FLUSH, null);
                        waiting.dismiss();
                        Log.d("myTag", "volley Error");
                    }
                }
        ) {
        };//add the request to the request queue
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }

    public void parseJSONtoList(JSONArray photosFromServer) {
        //if there are not enough images to play, inform the user to try again later and kill the activity
        //reduced to 3 for testing, would normally be 10 to ensure some sort of variety
        if (photosFromServer.length() < 3) {
            //hide the button that would start the next game as it is not needed
            //until this game has been completed
            nextGame.setVisibility(View.INVISIBLE);
            voice.speak("Sorry Sophia... There are not enough photos to play a matching game ..."+
                    "Please try to play again later", TextToSpeech.QUEUE_FLUSH, null);
            finish();
        } else {
            try {//parse the returned data for inclusion in the game
                for (int i = 0; i < photosFromServer.length(); i++) {
                    JSONObject obj = null;
                    obj = photosFromServer.getJSONObject(i);
                    GamesImage gameElement = new GamesImage();
                    gameElement.game_image_id = obj.getString("id");
                    gameElement.game_image = obj.getString("photo");
                    gameElement.game_description = obj.getString("description");

                    threeImagesForGame.add(gameElement);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }//when parseing is complete start a new game
            setupNewGame();
        }

    }

    public void setupNewGame() {
        //ensure that the views and buttons have not been garbage collected
        nextGame = (ImageButton) findViewById(R.id.nextGameBtn);
        nextGame.setOnClickListener(this);
        nextGame.setVisibility(View.INVISIBLE);


        view = (LinearLayout) findViewById(R.id.gameBackground);
        view.setBackgroundColor(getResources().getColor(R.color.pink));
        a = (ImageView) findViewById(R.id.webView);
        a.setOnTouchListener(this);
        b = (ImageView) findViewById(R.id.webView2);
        b.setOnTouchListener(this);
        c = (ImageView) findViewById(R.id.webView3);
        c.setOnTouchListener(this);
        //hide the next game button.
        nextGame.setVisibility(View.INVISIBLE);
        //shuffle the array of images and pick the first three to be displayed
        Collections.shuffle(threeImagesForGame);
        //put the three image into the views
        Picasso.with(MatchingGame.this)
                .load(threeImagesForGame.get(0).game_image)
                .noFade()
                .into(a);
        Picasso.with(MatchingGame.this)
                .load(threeImagesForGame.get(1).game_image)
                .noFade()
                .into(b);
        Picasso.with(MatchingGame.this)
                .load(threeImagesForGame.get(2).game_image)
                .noFade()
                .into(c);

        askQuestion();

        }

    //pick a random image from the three, mark it as the correct image and ask a question to identify it
    public void askQuestion() {
        //generate a random number between 0-3 (0,1,2)
        Random r = new Random();
        n = r.nextInt(3);
        try {// set the correct answer
            correctAnswer = threeImagesForGame.get(n).game_image_id;
        } catch (NullPointerException e) {
            Log.d("myTag", "null pointer askQuestion");
        }
        //set the question, this will be asked, and logged with the image if the guess is incorrect
        correctQuestion = "can you see the photograph of..."+threeImagesForGame.get(n).game_description;
        // get the url of the correct image, to be logged with the question if the guess is incorrect
        iAmTheRightimage = threeImagesForGame.get(n).game_image;
        //pass the question to the text to speech and read it
        voice.speak(correctQuestion, TextToSpeech.QUEUE_FLUSH, null);
    }
    // games image class used as list items in the list of three images
    class GamesImage {
        public String game_image_id;
        public String game_image;
        public String game_description;
        public String game_answer = "incorrect";
    }

    @Override//the listener placed on the image views
    public boolean onTouch(View v, MotionEvent event) {
        //if an image is pressed vibrate
        buttonVibe.vibrate(100);
        // which ever image is selected , check it against the correct answer
        if (v == a) {
            checkAnswer(0);

        }
        if (v == b) {
            checkAnswer(1);

        }
        if (v == c) {
            checkAnswer(2);

        }
        //if an image is clicked disable all clicks on the until a new game is started
        disableClick();
        //return false to allow the button click to start the next game
        return false;
    }
    //methood to check the answer
    public void checkAnswer(int ans) {
        // set the answer string from the id of the image that was pressed
        String thisAns = threeImagesForGame.get(ans).game_image_id;
        //set the image id from the gameimage id at the location of the answer
        iAmTheId = thisAns;
        // get the URL of the image that was guesssed and display it
        iAmTheGuessimage = threeImagesForGame.get(ans).game_image;
        setResultScreen(ans);
        // if the answer is correct change the back ground to green and congratulate the child
        if (thisAns.equalsIgnoreCase(correctAnswer)) {
            view.setBackgroundColor(getResources().getColor(R.color.green));
            voice.speak("Well done sophia... This is a photo of... " + threeImagesForGame.get(ans).game_description, TextToSpeech.QUEUE_FLUSH, null);
            iAmTheResult = "correct";
        } else {// if the guess is wrong set the background red, infor the child and log the result to the database
            view.setBackgroundColor(getResources().getColor(R.color.red));
            voice.speak("Hard Luck sophia... This is a photo of... " + threeImagesForGame.get(ans).game_description, TextToSpeech.QUEUE_FLUSH, null);
            iAmTheResult = "wrong";

        }//method call to log the result
        volleyGuess();
    }
    //method to disable the clicks
    public void disableClick() {
        a.setEnabled(false);
        b.setEnabled(false);
        c.setEnabled(false);
    }
    //method to reenable the click lister
    public void enableClick() {
        a.setEnabled(true);
        b.setEnabled(true);
        c.setEnabled(true);
    }

    @Override//listener placed on the game restart button
    public void onClick(View v) {
        //vibrate for 100 milliseconds
        buttonVibe.vibrate(100);
        //reset the background colour to pink
        view.setBackgroundColor(getResources().getColor(R.color.pink));
        enableClick();
        setupNewGame();
    }








    //volley post request to insert the incorrect guess and the images linked to the guess
    public void volleyGuess() {
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Authenticating User details..", false, false);
        StringRequest myQuery = new StringRequest(Request.Method.POST, UPDATE_GUESS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String comeBack) {
                String type = comeBack.trim();
                if (type.equalsIgnoreCase("successful")) {
                    waiting.dismiss();
                    Log.d("myTag", "found");
                } else {
                    waiting.dismiss();
                    Log.d("myTag", "notfound");
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        voice.speak("There may be a network error", TextToSpeech.QUEUE_FLUSH, null);
                        waiting.dismiss();
                        Log.d("myTag", "volley Error");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", correctAnswer);
                map.put("guess", iAmTheResult);
                if (iAmTheResult.equalsIgnoreCase("wrong")) {
                    map.put("wrongimage", iAmTheGuessimage);
                    map.put("rightimage", iAmTheRightimage);
                    map.put("question", correctQuestion);
                }
                return map;
            }
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(myQuery);

    }
    //place the image guessed in the center of the screen
    public void setResultScreen(int number) {
        a.setImageResource(0);
        b.setImageResource(0);
        c.setImageResource(0);
        Picasso.with(this)
                .load(threeImagesForGame.get(number).game_image)
                .skipMemoryCache()
                .into(b);
        //display the next game button
        nextGame.setVisibility(View.VISIBLE);


    }
}

