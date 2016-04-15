package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class AlbumActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String SHOW_THIS_URL = "http://52.50.76.1/sophiaFYP/getalltaggedimages.php?email=";
    public static final String UPDATE_GUESS_URL = "http://52.50.76.1/sophiaFYP/updatefeelings.php?email=";
    ImageView displayMe;
    ImageButton funny,like,what,want,next,last;
    TextToSpeech voice;
    int counter=0;
    public static final String SHARED = "globals";
    SharedPreferences shared;


    ArrayList<GamesImage> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shared = getSharedPreferences(SHARED, 0);

        imageList = new ArrayList<GamesImage>();
        displayMe = (ImageView) findViewById(R.id.photograph);
        funny =(ImageButton) findViewById(R.id.funnyyes);
        like = (ImageButton) findViewById(R.id.likethis);
        want = (ImageButton) findViewById(R.id.wantthis);
        what= (ImageButton) findViewById(R.id.what);
        next = (ImageButton) findViewById(R.id.nextphoto);
        last = (ImageButton) findViewById(R.id.lastphoto);
        funny.setOnClickListener(this);
        like.setOnClickListener(this);
        want.setOnClickListener(this);
        what.setOnClickListener(this);
        next.setOnClickListener(this);
        last.setOnClickListener(this);


        voice = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });

        getMyAlbum(shared.getString("email","130dee@gmail.com"));


    }

    @Override
    public void onClick(View v) {

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
                displayMe.setImageResource(0);
                Picasso.with(AlbumActivity.this)
                        .load(imageList.get(counter %imageList.size() ).game_image)
                        .noFade()
                        .skipMemoryCache().into(displayMe);

            }
        }else{

            if(v==funny){
                voice.speak("This is funny", TextToSpeech.QUEUE_FLUSH, null);

            }
            if(v==like){
                voice.speak("I like this", TextToSpeech.QUEUE_FLUSH, null);

            }
            if(v==want){
                voice.speak("I want this", TextToSpeech.QUEUE_FLUSH, null);

            }
            if(v==what){
                voice.speak(imageList.get(counter%imageList.size()).game_description, TextToSpeech.QUEUE_FLUSH, null);

            }


        }



    }

    class GamesImage{
        public String game_image_id;
        public String game_image;
        public String game_description;
    }

    public void getMyAlbum(String email){
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Getting Next Image..", false, false);
        String finalURL= SHOW_THIS_URL+email;
        //Toast.makeText(AlbumActivity.this,finalURL, Toast.LENGTH_LONG).show();

        JsonArrayRequest jsonQuery = new JsonArrayRequest(JsonArrayRequest.Method.GET, finalURL,null,
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
                        Toast.makeText(AlbumActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }

    public void parseJSONtoList(JSONArray photosFromServer) {
        String print ="Stuff:"+photosFromServer.length();
        if (photosFromServer.length() < 1) {
            voice.speak("There are not enough photos to play a matching game", TextToSpeech.QUEUE_FLUSH, null);
            voice.speak("Please try to play again later", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            try {
                for (int i = 0; i < photosFromServer.length(); i++) {
                    JSONObject obj = null;
                    obj = photosFromServer.getJSONObject(i);
                    GamesImage gameElement = new GamesImage();
                    gameElement.game_image_id = obj.getString("id");
                    gameElement.game_image = obj.getString("photo");
                    gameElement.game_description = obj.getString("description");

                    print.concat(i + ": ");


                    imageList.add(gameElement);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(AlbumActivity.this,print,Toast.LENGTH_LONG).show();
            loadUpDisplayScreen();
        }

    }





    public void loadUpDisplayScreen(){
        //setUpEmotionBar();
        Picasso.with(AlbumActivity.this)
                .load(imageList.get(0).game_image)
                .skipMemoryCache()
                .into(displayMe);

    }

}
