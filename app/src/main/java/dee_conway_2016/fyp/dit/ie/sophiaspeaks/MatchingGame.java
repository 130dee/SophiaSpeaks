package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class MatchingGame extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    public static final String SHOW_THIS_URL = "http://52.50.76.1/sophiaFYP/getthreeimages.php?email=";
    public static final String UPDATE_GUESS_URL = "http://52.50.76.1/sophiaFYP/updateguess.php?email=";
    public static final String SHARED = "globals";
    SharedPreferences shared;
    ImageView a, b, c, answerImage;
    ImageButton nextGame;
    LinearLayout view;
    RelativeLayout top;
    TextToSpeech voice;
    int n;
    String correctAnswer, iAmTheId, iAmTheResult, iAmTheRightimage, iAmTheGuessimage, correctQuestion;

    ArrayList<GamesImage> threeImagesForGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shared = getSharedPreferences(SHARED, 0);

        view        = (LinearLayout) findViewById(R.id.gameBackground);
        nextGame    = (ImageButton) findViewById(R.id.nextGameBtn);
        a           = (ImageView) findViewById(R.id.webView);
        b           = (ImageView) findViewById(R.id.webView2);
        c           = (ImageView) findViewById(R.id.webView3);
        nextGame.setOnClickListener(this);
        a.setOnTouchListener(this);
        b.setOnTouchListener(this);
        c.setOnTouchListener(this);


        enableClick();

        threeImagesForGame = new ArrayList<GamesImage>();

        voice = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });

        //getImageListFromServer(shared.getString("email", "130dee@gmail.com"));

        getImageListFromServer("130dee@gmail.com");

    }

    public void getImageListFromServer(String email) {
        final ProgressDialog waiting = ProgressDialog.show(this, "Searching...",
                "Getting Next Image..", false, false);
        String finalURL = SHOW_THIS_URL + email;

        JsonArrayRequest jsonQuery = new JsonArrayRequest(JsonArrayRequest.Method.GET, finalURL, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        waiting.dismiss();
                        parseJSONtoList(response);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        waiting.dismiss();
                        Log.d("myTag", "volley Error");
                    }
                }
        ) {
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }

    public void parseJSONtoList(JSONArray photosFromServer) {
        if (photosFromServer.length() < 3) {
            nextGame.setVisibility(View.INVISIBLE);
            //voice.speak("There are not enough photos to play a matching game", TextToSpeech.QUEUE_FLUSH, null);
            voice.speak("Sorry Sophia... There are not enough photos to play a matching game ..."+
                    "Please try to play again later", TextToSpeech.QUEUE_FLUSH, null);
            finish();
        } else {
            try {
                for (int i = 0; i < photosFromServer.length(); i++) {
                    JSONObject obj = null;
                    obj = photosFromServer.getJSONObject(i);
                    GamesImage gameElement = new GamesImage();
                    gameElement.game_image_id = obj.getString("id");
                    gameElement.game_image = obj.getString("photo");
                    gameElement.game_question = obj.getString("question");
                    gameElement.game_description = obj.getString("description");

                    threeImagesForGame.add(gameElement);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setupNewGame();
        }

    }

    public void setupNewGame() {
        nextGame.setVisibility(View.INVISIBLE);
        Collections.shuffle(threeImagesForGame);

        for (int j = 0; j < 3; j++) {
            if (j == 0) {
                Picasso.with(MatchingGame.this)
                        .load(threeImagesForGame.get(0).game_image)
                        .skipMemoryCache()
                        .noFade()
                        .into(a, new Callback() {

                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Log.d("myTag", "volley Error");

                            }
                        });

            }

            if (j == 1) {
                Picasso.with(MatchingGame.this)
                        .load(threeImagesForGame.get(1).game_image)
                        .skipMemoryCache()
                        .noFade()
                        .into(b, new Callback() {

                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Log.d("myTag", "Picasso Error");

                            }
                        });

            }
            if (j == 2) {
                Picasso.with(MatchingGame.this)
                        .load(threeImagesForGame.get(2).game_image)
                        .skipMemoryCache()
                        .noFade()
                        .into(c, new Callback() {

                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Log.d("myTag", "Picasso Error");
                            }
                        });
                askQuestion();
            }
        }
    }

    public void askQuestion() {

        Random r = new Random();
        n = r.nextInt(3);
        try {
            correctAnswer = threeImagesForGame.get(n).game_image_id.toString();
        } catch (NullPointerException e) {
        }
        correctQuestion = threeImagesForGame.get(n).game_question.toString();
        iAmTheRightimage = threeImagesForGame.get(n).game_image.toString();
        voice.speak(correctQuestion, TextToSpeech.QUEUE_FLUSH, null);
    }

    class GamesImage {
        public String game_image_id;
        public String game_image;
        public String game_question;
        public String game_description;
        public String game_answer = "incorrect";
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == a) {
            checkAnswer(0);

        }
        if (v == b) {
            checkAnswer(1);

        }
        if (v == c) {
            checkAnswer(2);

        }
        disableClick();
        return false;
    }

    public void checkAnswer(int ans) {
        String thisAns = threeImagesForGame.get(ans).game_image_id;
        iAmTheId = thisAns;
        iAmTheGuessimage = threeImagesForGame.get(ans).game_image;
        setResultScreen(ans);

        if (thisAns.equalsIgnoreCase(correctAnswer)) {
            view.setBackgroundColor(getResources().getColor(R.color.green));
            voice.speak("Well done sophia " + threeImagesForGame.get(ans).game_description, TextToSpeech.QUEUE_FLUSH, null);
            iAmTheResult = "correct";
        } else {
            view.setBackgroundColor(getResources().getColor(R.color.red));
            voice.speak("Hard Luck sophia " + threeImagesForGame.get(ans).game_description, TextToSpeech.QUEUE_FLUSH, null);
            iAmTheResult = "wrong";

        }
        volleyGuess();
    }

    public void disableClick() {
        a.setEnabled(false);
        b.setEnabled(false);
        c.setEnabled(false);
    }

    public void enableClick() {
        a.setEnabled(true);
        b.setEnabled(true);
        c.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        view.setBackgroundColor(getResources().getColor(R.color.pink));
        enableClick();
        setupNewGame();
    }





    public void setUpTheScreen() {

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
        enableClick();

    }



    public void volleyGuess() {
        StringRequest myQuery = new StringRequest(Request.Method.POST, UPDATE_GUESS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String comeBack) {
                String type = comeBack.trim();
                if (type.equalsIgnoreCase("successful")) {

                    Log.d("myTag", "found");


                } else {
                    Log.d("myTag", "notfound");
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

    public void setResultScreen(int number) {
        a.setImageResource(0);
        b.setImageResource(0);
        c.setImageResource(0);
        Picasso.with(this)
                .load(threeImagesForGame.get(number).game_image)
                .skipMemoryCache()
                .into(b);

        nextGame.setVisibility(View.VISIBLE);


    }
}

