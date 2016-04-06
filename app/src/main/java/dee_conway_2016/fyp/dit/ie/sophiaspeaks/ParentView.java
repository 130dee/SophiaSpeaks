package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("deprcated")
public class ParentView extends AppCompatActivity implements View.OnClickListener{

    Button addChild, addSupport,addGame,editImages,viewImages, editMyDetails,showNext,closeView;
    Button showLocation, addThistag;
    WebView nxt;
    EditText tagThis;

    TextToSpeech voice;
    public static String messageFromSophia = "Say this";
    public static String location;
    public static String imID;
    public static final String SHARED = "globals";
    SharedPreferences shared;

    //Strings used as url parameters for GET requests
    public static final String SHOW_THIS_URL = "http://52.50.76.1/sophia/getimages.php?email=";
    public static final String UPDATE_THIS_URL = "http://52.50.76.1/sophia/update.php?job=";
    public static final String IM_TAG = "&attach=";
    public static final String ID ="id=";
    public static final String DATA="data=";


    public static final String GAME = "game&";
    public static final String WORD = "word&";
    public static final String VIEWED ="viewed&";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shared = getSharedPreferences(SHARED, 0);

        addChild = (Button)findViewById(R.id.addChild);
        addSupport = (Button)findViewById(R.id.addSupport);
        addGame = (Button)findViewById(R.id.addGame);
        editImages = (Button)findViewById(R.id.editImages);
        viewImages = (Button)findViewById(R.id.viewImage);
        editMyDetails = (Button)findViewById(R.id.editDetails);
        showNext = (Button) findViewById(R.id.nextImage);
        showLocation = (Button) findViewById(R.id.whereWasITaken);
        closeView = (Button)findViewById(R.id.close);
        addThistag = (Button) findViewById(R.id.tagButton);

        tagThis = (EditText) findViewById(R.id.tagBox);


        addChild.setOnClickListener(this);
        addSupport.setOnClickListener(this);
        addGame.setOnClickListener(this);
        editImages.setOnClickListener(this);
        viewImages.setOnClickListener(this);
        editMyDetails.setOnClickListener(this);
        showNext.setOnClickListener(this);
        showLocation.setOnClickListener(this);
        showLocation.setVisibility(View.INVISIBLE);
        closeView.setOnClickListener(this);
        closeView.setVisibility(View.INVISIBLE);
        tagThis.setOnClickListener(this);
        tagThis.setVisibility(View.INVISIBLE);
        addThistag.setOnClickListener(this);
        addThistag.setVisibility(View.INVISIBLE);

        nxt = (WebView)findViewById(R.id.currentImage);
        nxt.setVisibility(View.INVISIBLE);
        nxt.getSettings().setLoadWithOverviewMode(true);
        nxt.getSettings().setUseWideViewPort(true);

        voice=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });


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
        if (v==addChild){
            Intent intent = new Intent(this, Register.class);
            intent.putExtra("type", "child");
            startActivity(intent);

        }

        if (v==addSupport){
            Intent intent = new Intent(this, Register.class);
            intent.putExtra("type","support");
            startActivity(intent);
        }

        if (v==addGame){
            voice.speak("child", TextToSpeech.QUEUE_FLUSH, null);
            Intent intent = new Intent(this,AddGame.class);
            startActivity(intent);

        }
        if (v==editImages){
            Intent intent = new Intent(this, Register.class);

            startActivity(intent);

        }
        if (v==editMyDetails){
            Intent intent = new Intent(this, Register.class);
            intent.putExtra("message","sub");
            startActivity(intent);

        }
        if (v==viewImages){
            Intent intent = new Intent(this, Register.class);
            intent.putExtra("message","sub");
            startActivity(intent);

        }
        if (v==showNext){
            //get list of images that have not been viewed yet order by upload time
            changeButtonViewVisible();
            getNextimage(shared.getString("email","default"));


        }
        if (v == showLocation){
            // Creates an Intent that will load a map of San Francisco
            Uri gmmIntentUri = Uri.parse(location);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
        if(v==closeView){
            changeButtonViewInvisible();
        }
        if(v==addThistag){

            checkInput();
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
                        Toast.makeText(ParentView.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        ) {
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }

    public void displayCurrentImage(JSONArray photosFromServer){

        messageFromSophia = "";
        try{
            JSONObject obj = null;
            obj = photosFromServer.getJSONObject(0);
            nxt.loadUrl(obj.getString("photo"));
            imID=obj.getString("id");
            messageFromSophia= obj.getString("tag");
            location = obj.getString("locate");
            uploadCommand(VIEWED.concat(ID).concat(imID));

        }catch(JSONException e){
            e.printStackTrace();
        }
        if(!messageFromSophia.equalsIgnoreCase("")){
            voice.speak(messageFromSophia, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void changeButtonViewInvisible(){
        closeView.setVisibility(View.INVISIBLE);
        showLocation.setVisibility(View.INVISIBLE);
        nxt.setVisibility(View.INVISIBLE);
        tagThis.setVisibility(View.INVISIBLE);
        addThistag.setVisibility(View.INVISIBLE);
    }
    public void changeButtonViewVisible(){
        closeView.setVisibility(View.VISIBLE);
        showLocation.setVisibility(View.VISIBLE);
        nxt.setVisibility(View.VISIBLE);
        tagThis.setVisibility(View.VISIBLE);
        addThistag.setVisibility(View.VISIBLE);
    }

    private void uploadCommand(String a){
        Log.d("myTag", "Trying To Update");
        final String THIS_JOB = UPDATE_THIS_URL.concat(a);

        Toast.makeText(ParentView.this,THIS_JOB+":  RETURN",Toast.LENGTH_LONG).show();
        StringRequest myQuery = new StringRequest(Request.Method.GET,THIS_JOB,new Response.Listener<String>(){
            @Override
            public void onResponse(String comeBack){
                String type = comeBack.trim();
                if(type.equalsIgnoreCase("updated")){
                    Log.d("myTag", "updated");
                    Toast.makeText(ParentView.this,type+"RETURN",Toast.LENGTH_LONG).show();
                }else{
                    Log.d("myTag", "notUpdated");
                    Toast.makeText(ParentView.this,type+"WRONG",Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(ParentView.this,"ERROR",Toast.LENGTH_LONG).show();
                    }
                }
        );

        RequestQueue thisQ = Volley.newRequestQueue(this);
        thisQ.add(myQuery);
    }

    public void checkInput(){

        tagThis.setError(null);
        String thisImageTag = tagThis.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        //check if text has been entered,
        if (TextUtils.isEmpty(thisImageTag)){
            tagThis.setError(getString(R.string.error_field_required));
            focusView = tagThis;
            cancel = true;
        }

        //if email and password are entered
        if(!cancel){
            cancel = false;
            String payload = WORD.concat(ID).concat(imID).concat(IM_TAG).concat(thisImageTag);
            tagThis.setText("");
            uploadCommand(payload);

        }

    }
}
