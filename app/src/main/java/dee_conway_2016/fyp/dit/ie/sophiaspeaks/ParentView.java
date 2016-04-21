package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("deprcated")
public class ParentView extends AppCompatActivity implements View.OnClickListener{
    //attributes to allow the activity to run
    Button showNext,editCurrentImage,getNextImage;
    ImageButton showLocation, closeView;
    ImageButton correctTick,incorrectTick;
    ImageView nxt;
    LinearLayout themeBtnLayout;
    EditText tagThis;
    Boolean notCorrected = true;
    Vibrator buttonVibe;

    TextToSpeech voice;
    public static String messageFromSophia = "Say this";
    public static String imURL;
    public static String location;
    public static String imID;
    public static String question;
    public static String imagethemeboolean;
    public static String description;
    public static final String SHARED = "globals";
    SharedPreferences shared;

    //Strings used as url parameters for GET requests
    public static final String SHOW_THIS_URL = "http://52.50.76.1/sophiaFYP/getimages.php?email=";
    public static final String UPDATE_THIS_URL = "http://52.50.76.1/sophiaFYP/update.php?job=";
    public static final String THEME_URL = "http://52.50.76.1/sophiaFYP/themetableupdate.php?";

    public static final String ID ="id=";

    public static final String VIEWED ="viewed&";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //get access to the shared pref content
        shared = getSharedPreferences(SHARED, 0);

        LinearLayout themeBtnLayout = (LinearLayout) findViewById(R.id.linearLayout4);

        //assign the buttons to the xml buttons and give them listeners
        showLocation = (ImageButton) findViewById(R.id.whereWasITaken);
        closeView = (ImageButton)findViewById(R.id.close);
        editCurrentImage = (Button)findViewById(R.id.editImage);
        getNextImage = (Button) findViewById(R.id.nextImage);
        correctTick = (ImageButton) findViewById(R.id.correctTheme);
        incorrectTick = (ImageButton) findViewById(R.id.wrongTheme);
        showLocation.setOnClickListener(this);
        closeView.setOnClickListener(this);
        getNextImage.setOnClickListener(this);
        editCurrentImage.setOnClickListener(this);
        correctTick.setOnClickListener(this);
        incorrectTick.setOnClickListener(this);


        nxt = (ImageView)findViewById(R.id.currentImage);


        voice=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });

        getNextImageAndDisplay();

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
        editor.putString("amLogged", "false");
        editor.putString("type", null);
        editor.commit();


        finish();
        return true;

    }

    @Override
    public void onClick(View v) {
        buttonVibe.vibrate(100);

        if (v == showLocation){
            // open a map and run a query with the coordiates of the image, resulting in a marker being placed
            Uri gmmIntentUri = Uri.parse(location);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
        else if(v==closeView){
            //end the activity
            finish();
        }
        else if (v==editCurrentImage){
            // if the user wants to edit the current image an edit activity will be called
            Intent intent = new Intent(this, ImageEditActivity.class);
            intent.putExtra("image",imURL);
            intent.putExtra("id",imID);
            intent.putExtra("question",question);
            intent.putExtra("description", description);
            startActivity(intent);

        }
        else if (v==getNextImage){
            //get list of images that have not been viewed yet order by upload time
            nxt.setImageResource(0);
            getNextImageAndDisplay();
        }else if (v==correctTick){
            volleyTheme("yes");
            notCorrected=false;
            changeButtonViewVisible();
        }else if(v==incorrectTick){
            volleyTheme("no");
            notCorrected=false;
            changeButtonViewVisible();
        }
    }
    // get the most recently taken, unseen image from the server
    public void getNextimage(String email){
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Getting Next Image..", false, false);
        String finalURL= SHOW_THIS_URL+email;
        JsonArrayRequest jsonQuery = new JsonArrayRequest(JsonArrayRequest.Method.GET, finalURL,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        waiting.dismiss();
                        displayCurrentImage(response);
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
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }
    // parse the response and display it
    public void displayCurrentImage(JSONArray photosFromServer) {
        
        messageFromSophia = "There are no new images from Sophia";
        try {
            JSONObject obj = null;
            obj = photosFromServer.getJSONObject(0);

            imID = obj.getString("id");
            imURL = obj.getString("photo");
            messageFromSophia = obj.getString("tag");
            location = obj.getString("locate");
            description = obj.getString("wordSound");
            imagethemeboolean = obj.getString("theme");
            uploadCommand(VIEWED.concat(ID).concat(imID));
            changeButtonViewVisible();


        } catch (JSONException e) {
            e.printStackTrace();// if there are no new images inform the user and finish.
            voice.speak(messageFromSophia, TextToSpeech.QUEUE_FLUSH, null);
            finish();

        }
        Picasso.with(ParentView.this).load(imURL)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .noFade()
                .into(nxt);
    }




    //only display buttons that are necessary there are two options
    //if the image is a theme image a correct and x will be displayed.
    //else the rest of the buttons will be displayed
    public void changeButtonViewVisible(){
        if(!imagethemeboolean.equalsIgnoreCase("no")&&(notCorrected)){
            correctTick.setVisibility(View.VISIBLE);
            incorrectTick.setVisibility(View.VISIBLE);
            editCurrentImage.setVisibility(View.INVISIBLE);
            getNextImage.setVisibility(View.INVISIBLE);
            closeView.setVisibility(View.INVISIBLE);
            showLocation.setVisibility(View.INVISIBLE);

        }else{
            correctTick.setVisibility(View.INVISIBLE);
            incorrectTick.setVisibility(View.INVISIBLE);
            editCurrentImage.setVisibility(View.VISIBLE);
            getNextImage.setVisibility(View.VISIBLE);
            closeView.setVisibility(View.VISIBLE);
            showLocation.setVisibility(View.VISIBLE);

        }

        nxt.setVisibility(View.VISIBLE);
        notCorrected=true;
    }
    //volley request to insert a viewed tag to an image that has been seen by the user
    private void uploadCommand(String a){
        Log.d("myTag", "Trying To Update");
        final String THIS_JOB = UPDATE_THIS_URL.concat(a);

        //Toast.makeText(ParentView.this,THIS_JOB+":  UPDATING THE DATABASE",Toast.LENGTH_LONG).show();
        StringRequest myQuery = new StringRequest(Request.Method.GET,THIS_JOB,new Response.Listener<String>(){
            @Override
            public void onResponse(String comeBack){
                String type = comeBack.trim();
                if(type.equalsIgnoreCase("updated")){
                    Log.d("myTag", "updated");
                    //Toast.makeText(ParentView.this,type+"WAS UPDATED",Toast.LENGTH_LONG).show();
                }else{
                    Log.d("myTag", "notUpdated");
                    //Toast.makeText(ParentView.this,type+"WAS NOT UPDATED",Toast.LENGTH_LONG).show();
                }voice.speak(messageFromSophia, TextToSpeech.QUEUE_FLUSH, null);

            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        voice.speak("There may be a network error...", TextToSpeech.QUEUE_FLUSH, null);
                        Log.d("myTag", "Network error");
                    }
                }
        );

        RequestQueue thisQ = Volley.newRequestQueue(this);
        thisQ.add(myQuery);
    }
    //volley request to insert the reults of a theme image request
    public void volleyTheme(String state) {
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Getting Next Image..", false, false);
        final String yes_no = state;
        StringRequest myQuery = new StringRequest(Request.Method.POST, THEME_URL, new Response.Listener<String>() {

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
                map.put("id", imID);
                map.put("state", yes_no);
                map.put("email",shared.getString("email","email"));
                map.put("theme",imagethemeboolean);

                return map;
            }
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(myQuery);

    }
    // get the emailaddress of the current user and use it to query the DB for unseen images
    public void getNextImageAndDisplay(){

        getNextimage(shared.getString("email", "email"));
    }
}
