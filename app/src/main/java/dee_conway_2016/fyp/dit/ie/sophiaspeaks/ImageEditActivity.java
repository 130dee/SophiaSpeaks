package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
@SuppressWarnings("deprecation")// speak is deprecated, but the testing device is an old device so needs that version
public class ImageEditActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String IMAGE_EDIT_URL = "http://52.50.76.1/sophiaFYP/imageedit.php";
    public static final String NEED_EDIT_URL = "http://52.50.76.1/sophiaFYP/needsedit.php?email=";
    Vibrator buttonVibe;

    TextToSpeech voice;
    ImageView backgroundImageDispplay;
    int counter=0;
    public static final String SHARED = "globals";
    SharedPreferences shared;

    ArrayList<EditImage> imageList;


    EditText addQuestionText, addDescriptionText;
    Button descriptionAddBtn,getDescriptionVoiceBtn,saveDescriptionBtn,discard1;
    ImageButton deleteImageBtn,nextImage,lastImage;

    RelativeLayout editdescription,editquestion;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    Intent intent;
    String saveD,delete,imageid,description;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    class EditImage{
        public String edit_image_id;
        public String edit_image;
        public String edit_description;
        public String edit_question;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // get a vibrator to notify the user of button clicks
        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        shared = getSharedPreferences(SHARED, 0);
        imageList = new ArrayList<EditImage>();
        //strings to attach to the volley request to set the type of request
        saveD = "saveD";
        delete = "delete";
        //get an instance of the text to speech
        voice = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        //assign all the editboxes and buttons
        editdescription = (RelativeLayout) findViewById(R.id.descriptionLayout);
        descriptionAddBtn = (Button)findViewById(R.id.addDescription);
        descriptionAddBtn.setOnClickListener(this);
        addDescriptionText= (EditText)findViewById(R.id.descriptionEdit);
        getDescriptionVoiceBtn= (Button)findViewById(R.id.vBtn1);
        getDescriptionVoiceBtn.setOnClickListener(this);
        saveDescriptionBtn = (Button)findViewById(R.id.save1);
        saveDescriptionBtn.setOnClickListener(this);
        discard1 = (Button) findViewById(R.id.discard);
        discard1.setOnClickListener(this);

        deleteImageBtn = (ImageButton)findViewById(R.id.deleteThis);
        deleteImageBtn.setOnClickListener(this);
        nextImage = (ImageButton) findViewById(R.id.nexteditphoto);
        nextImage.setOnClickListener(this);
        lastImage = (ImageButton) findViewById(R.id.lasteditphoto);
        lastImage.setOnClickListener(this);

        editdescription.setVisibility(View.INVISIBLE);

        backgroundImageDispplay = (ImageView)findViewById(R.id.backgroundImage);


        //check to see if the image has been edited already, if yes display the text in the text box
        if(this.getIntent().getExtras()!=null){
            String displayimage;
            try {
                displayimage = getIntent().getExtras().getString("image");
                Picasso.with(ImageEditActivity.this).load(displayimage).into(backgroundImageDispplay);
            } catch (NullPointerException e ) {
                displayimage = "";
            }
            final String image = displayimage;

            try {
                imageid = getIntent().getExtras().getString("id");
            } catch (NullPointerException e ) {
                imageid = "";
            }

            try {
                description = getIntent().getExtras().getString("description");
            } catch (NullPointerException e ) {
                description = "";
            }

        }else{
            getEditPhotos(shared.getString("email","email"));
        }


    }


    @Override//ensure the user is logged in else go to login
    public void onResume(){
        super.onResume();
        SharedPreferences shared = getSharedPreferences(SHARED, 0);
        String user = shared.getString("name", "nancy");
        this.setTitle("Logged in:" + user);
        if (shared.getString("amLogged","false").equalsIgnoreCase("true")){


        }
        else{
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

    }



    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        if (backgroundImageDispplay != null) {
            backgroundImageDispplay = null;

        }
    }

    @Override// listener on the buttons
    public void onClick(View v) {
        //vibrate the device
        buttonVibe.vibrate(100);
        if (v == discard1) {
            //if not saved replace the buttons on the screen
            putButtonsBack();
        }
        if (v == descriptionAddBtn) {
            // hide all unescessary buttons
            deleteButtonsView();
            //bring text box into view
            editdescription.setVisibility(View.VISIBLE);
            // set the text in the box to be empty
            addDescriptionText.setText("");
        }
        //get a voiceinput to text
        if (v == getDescriptionVoiceBtn) {
            promptSpeechInput();
        }

        //save the text in the text box
        if (v == saveDescriptionBtn) {
            description = addDescriptionText.getText().toString();
            volleyPut(saveD);
            putButtonsBack();

        }
        //go to next image
        if (v == nextImage) {
            counter++;
            if (counter >= imageList.size()) {
                counter = imageList.size();
                voice.speak("No More images waiting to be edited", TextToSpeech.QUEUE_FLUSH, null);
                finish();
            } else {
                loadUpDisplayScreen();
            }

        }//go to last image
        if (v == lastImage) {
            counter--;
            if (counter <= 0) {
                counter = 0;
                voice.speak("No More images waiting to be edited", TextToSpeech.QUEUE_FLUSH, null);
                finish();
            } else {
                loadUpDisplayScreen();
            }

        }
        //mark the image as deleted in the image store
        if (v == deleteImageBtn) {

            volleyPut(delete);
        }

    }
    //clear the screen of unnecessary buttons
    public void deleteButtonsView(){
        descriptionAddBtn.setVisibility(View.INVISIBLE);
        deleteImageBtn.setVisibility(View.INVISIBLE);
        nextImage.setVisibility(View.INVISIBLE);
        lastImage.setVisibility(View.INVISIBLE);
    }
    //put the buttons back
    public void putButtonsBack(){
        editdescription.setVisibility(View.INVISIBLE);
        descriptionAddBtn.setVisibility(View.VISIBLE);
        deleteImageBtn.setVisibility(View.VISIBLE);
        nextImage.setVisibility(View.VISIBLE);
        lastImage.setVisibility(View.VISIBLE);
    }

    //display the voice input
    private void promptSpeechInput() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "speech promt");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Log.d("myTag", "camera error");
        }
    }

    @Override//when voice input has been complete
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    addDescriptionText.setText(result.get(0).trim().toString());

                } else if (null == data) {
                    finish();
                }
            }
        }
    }

    @Override//auto generated code to be run when the voice input starts
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Insert Text", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://dee_conway_2016.fyp.dit.ie.sophiaspeaks/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override//auto generated code to be run when the voice input starts
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Insert Data", // TODO: Define a title for the content shown.
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
    //volley  request to insert the edited tag
    public void volleyPut(String task){
        final ProgressDialog waiting = ProgressDialog.show(this,"Editing image details...",
                "Uploading changes..", false, false);
        final String thisJob = task;

        StringRequest myQuery = new StringRequest(Request.Method.POST,IMAGE_EDIT_URL,new Response.Listener<String>(){
            @Override
            public void onResponse(String comeBack){
                String type = comeBack.trim();
                editdescription.setVisibility(View.INVISIBLE);
                if(type.equalsIgnoreCase("successful")){
                    waiting.dismiss();
                    Log.d("myTag", "found");
                    editdescription.setVisibility(View.INVISIBLE);

                }else{
                    waiting.dismiss();
                    Log.d("myTag", "notfound");
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        voice.speak("There may be a network error", TextToSpeech.QUEUE_FLUSH, null);
                        waiting.dismiss();
                        Log.d("myTag", "volley error");
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", imageid);
                map.put("job",thisJob);
                map.put("description",description);

                return map;
            }
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(myQuery);

    }
    //volley request to get images that have not been edited
    public void getEditPhotos(String email){
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Getting Next Image..", false, false);
        String finalURL= NEED_EDIT_URL+email;

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
                        voice.speak("There may be a network error", TextToSpeech.QUEUE_FLUSH, null);
                        waiting.dismiss();
                    }
                }
        ) {
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }
    //parse the JSON response to an array
    public void parseJSONtoList(JSONArray photosFromServer) {

        if (photosFromServer.length() < 1) {
            voice.speak("There are no images waiting to be edited", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            try {
                for (int i = 0; i < photosFromServer.length(); i++) {
                    JSONObject obj = null;
                    obj = photosFromServer.getJSONObject(i);
                    EditImage editElement = new EditImage();
                    editElement.edit_image_id = obj.getString("id");
                    editElement.edit_image = obj.getString("photo");

                    imageList.add(editElement);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadUpDisplayScreen();
        }

    }
    //display the selected image on the screen using Picasso
    public void loadUpDisplayScreen(){
        backgroundImageDispplay.setImageResource(0);
        Picasso.with(ImageEditActivity.this)
                .load(imageList.get(counter).edit_image)
                .skipMemoryCache()
                .into(backgroundImageDispplay);
        imageid= imageList.get(counter).edit_image_id;

    }




}
