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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("deprcated")
public class ParentView extends AppCompatActivity implements View.OnClickListener{

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

        shared = getSharedPreferences(SHARED, 0);

        LinearLayout themeBtnLayout = (LinearLayout) findViewById(R.id.linearLayout4);


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
    @Override
    public void onResume(){
        super.onResume();
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
            // Creates an Intent that will load a map of San Francisco
            Uri gmmIntentUri = Uri.parse(location);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
        else if(v==closeView){
            finish();
        }
        else if (v==editCurrentImage){
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
                        waiting.dismiss();
                        Log.d("myTag", "volley Error");
                    }
                }
        ) {
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }

    public void displayCurrentImage(JSONArray photosFromServer){

        messageFromSophia = "There are no new images from Sophia";
        try{
            JSONObject obj = null;
            obj = photosFromServer.getJSONObject(0);

            imID = obj.getString("id");
            imURL = obj.getString("photo");
            messageFromSophia= obj.getString("tag");
            location = obj.getString("locate");
            description= obj.getString("wordSound");
            imagethemeboolean = obj.getString("theme");
            Toast.makeText(ParentView.this,imagethemeboolean,Toast.LENGTH_LONG).show();
            uploadCommand(VIEWED.concat(ID).concat(imID));
            changeButtonViewVisible();


        }catch(JSONException e){
            e.printStackTrace();
            voice.speak(messageFromSophia, TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    public void changeButtonViewInvisible(){
        closeView.setVisibility(View.INVISIBLE);
        showLocation.setVisibility(View.INVISIBLE);
        nxt.setVisibility(View.INVISIBLE);
    }

    public void changeButtonViewVisible(){
        if(!imagethemeboolean.equalsIgnoreCase("no")&&(notCorrected)){
            correctTick.setVisibility(View.VISIBLE);
            incorrectTick.setVisibility(View.VISIBLE);
            editCurrentImage.setVisibility(View.INVISIBLE);
            getNextImage.setVisibility(View.INVISIBLE);
        }else{
            correctTick.setVisibility(View.INVISIBLE);
            incorrectTick.setVisibility(View.INVISIBLE);
            editCurrentImage.setVisibility(View.VISIBLE);
            getNextImage.setVisibility(View.VISIBLE);




        }
        closeView.setVisibility(View.VISIBLE);
        showLocation.setVisibility(View.VISIBLE);
        nxt.setVisibility(View.VISIBLE);
        notCorrected=true;
    }

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
                Picasso.with(ParentView.this).load(imURL).into(nxt);
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        //Toast.makeText(ParentView.this,"SOME KIND OF ERROR",Toast.LENGTH_LONG).show();
                    }
                }
        );

        RequestQueue thisQ = Volley.newRequestQueue(this);
        thisQ.add(myQuery);
    }

    public void volleyTheme(String state) {
        final String yes_no = state;
        StringRequest myQuery = new StringRequest(Request.Method.POST, THEME_URL, new Response.Listener<String>() {

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

    public void getNextImageAndDisplay(){

        getNextimage(shared.getString("email", "email"));
    }
}
