package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
@SuppressWarnings("deprecation")// speak is deprecated, but the testing device is an old device so needs that version
public class AlbumActivity extends AppCompatActivity implements View.OnClickListener{

    //attributes needed to complete the activity
    //URLs used in the volley requests
    public static final String SHOW_THIS_URL = "http://52.50.76.1/sophiaFYP/getalltaggedimages.php?email=";
    //imageview used to display the current image
    ImageView displayMe;
    //buttons that will be availible to the user to add sounds
    ImageButton funny,like,what,want,next,last;
    // the voice to read out messages when required
    TextToSpeech voice;
    //counter to keep track of the current image in the view
    int counter=0;
    //shared pref file
    public static final String SHARED = "globals";
    SharedPreferences shared;
    //vibrator to vibrate the screen on a button click
    Vibrator buttonVibe;

    //array list to store all the image data returned by the volley request
    ArrayList<GamesImage> imageList;

    @Override//run the current activity and instantiate all the fields required
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //get the shared preferences file
        shared = getSharedPreferences(SHARED, 0);
        //make an instance of the imagelist
        imageList = new ArrayList<>();
        //assign all the buttons and the view
        displayMe = (ImageView) findViewById(R.id.photograph);
        funny =(ImageButton) findViewById(R.id.funnyyes);
        like = (ImageButton) findViewById(R.id.likethis);
        want = (ImageButton) findViewById(R.id.wantthis);
        what= (ImageButton) findViewById(R.id.what);
        next = (ImageButton) findViewById(R.id.nextphoto);
        last = (ImageButton) findViewById(R.id.lastphoto);
        //put listeners on the button
        funny.setOnClickListener(this);
        like.setOnClickListener(this);
        want.setOnClickListener(this);
        what.setOnClickListener(this);
        next.setOnClickListener(this);
        last.setOnClickListener(this);

        //build a TextTospeech service
        voice = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });

        //volley request to retrieve all the availible images
        getMyAlbum(shared.getString("email","130dee@gmail.com"));


    }

    @Override//ensure the user is logged in else go to login
    public void onResume(){
        super.onResume();
        SharedPreferences shared = getSharedPreferences(SHARED, 0);
        String user = shared.getString("name", "nancy");
        this.setTitle("Logged in:" +user);
        if (shared.getString("amLogged","false").equalsIgnoreCase("false")){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override// listen for any button clicks and run the code contained within that button
    public void onClick(View v) {
        //every button click will make the mobile device vibrate for 100 milliseconds.
        buttonVibe.vibrate(100);
        //check if either of the buttons to change image has been clicked
        //if yes check which button and load the image to the view
        if((v==next)||(v==last)){
            {
                if(v==next){
                    counter++;
                }
                if(v==last){
                    if(counter==0){
                        counter = counter+ imageList.size();
                    }
                    counter--;
                }
                //set the imageview to empty, before loading
                displayMe.setImageResource(0);
                Picasso.with(AlbumActivity.this)
                        .load(imageList.get(counter %imageList.size() ).game_image)//the counter is modulo the size of the image list
                        .noFade()
                        .into(displayMe);

            }
        }else{//check which icon button has been pressed and make a sound, including the image tag

            if(v==funny){
                voice.speak(imageList.get(counter%imageList.size()).game_description+"... is funny", TextToSpeech.QUEUE_FLUSH, null);

            }
            if(v==like){
                voice.speak("I like..." + imageList.get(counter%imageList.size()).game_description, TextToSpeech.QUEUE_FLUSH, null);

            }
            if(v==want){
                voice.speak("I want... "+ imageList.get(counter%imageList.size()).game_description, TextToSpeech.QUEUE_FLUSH, null);

            }
            if(v==what){
                voice.speak(imageList.get(counter%imageList.size()).game_description, TextToSpeech.QUEUE_FLUSH, null);

            }


        }



    }
    //class of gamesImage that will be used as list items in the album list
    class GamesImage{
        public String game_image_id;
        public String game_image;
        public String game_description;
    }
    //volley request to get the availible images from the database
    public void getMyAlbum(String email){
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Getting Next Image..", false, false);
        //as it is a get request the url will contain the email address of the owner of the images
        String finalURL= SHOW_THIS_URL+email;
        //volley JOSN arrary request will return an array of JSON objects
        JsonArrayRequest jsonQuery = new JsonArrayRequest(JsonArrayRequest.Method.GET, finalURL,null,
                new Response.Listener<JSONArray>() {

                    @Override// whatever is returned is sent to be parsed
                    public void onResponse(JSONArray response) {
                        waiting.dismiss();
                        parseJSONtoList(response);
                    }

                },//if there is an error infor the user and log it
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        waiting.dismiss();
                        voice.speak("There may be a network error...", TextToSpeech.QUEUE_FLUSH, null);
                        Log.d("myTag", "Volley Error getMyAlbum");
                    }
                }
        ) {
        };//add the request to the request que
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }

    //parse the returned data from the volley query to the list of images
    public void parseJSONtoList(JSONArray photosFromServer) {
        if (photosFromServer.length() < 1) {
            //if the array is empty, inform the user that there is nothing to show
            voice.speak("Sorry Sophia... There are no images in the album... ", TextToSpeech.QUEUE_FLUSH, null);
            finish();
        } else {
            try {//for every item in the returned data parse to the imagelist
                for (int i = 0; i < photosFromServer.length(); i++) {
                    JSONObject obj;
                    obj = photosFromServer.getJSONObject(i);
                    GamesImage gameElement = new GamesImage();
                    gameElement.game_image_id = obj.getString("id");
                    gameElement.game_image = obj.getString("photo");
                    gameElement.game_description = obj.getString("description");



                    //add a new row to the list
                    imageList.add(gameElement);


                }// catch any error
            } catch (JSONException e) {
                e.printStackTrace();
            }
           // put the first image on the screen
            loadUpDisplayScreen();
        }

    }




    //method to put the first image on the screen
    public void loadUpDisplayScreen(){
        displayMe.setImageResource(0);
        Picasso.with(AlbumActivity.this)
                .load(imageList.get(0).game_image)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .noFade()
                .into(displayMe);

    }

}
