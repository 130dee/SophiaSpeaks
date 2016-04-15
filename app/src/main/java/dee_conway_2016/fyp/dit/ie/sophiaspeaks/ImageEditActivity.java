package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class ImageEditActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String SHOW_THIS_URL = "http://52.50.76.1/sophiaFYP/getalltaggedimages.php?email=";
    public static final String UPDATE_GUESS_URL = "http://52.50.76.1/sophiaFYP/updatefeelings.php?email=";
    public static final String IMAGE_EDIT_URL = "http://52.50.76.1/sophiaFYP/imageedit.php";
    public static final String NEED_EDIT_URL = "http://52.50.76.1/sophiaFYP/needsedit.php?email=";

    TextToSpeech voice;
    ImageView backgroundImageDispplay;
    int counter=0;
    public static final String SHARED = "globals";
    SharedPreferences shared;

    ArrayList<EditImage> imageList;


    EditText addQuestionText, addDescriptionText;
    Button questionAddBtn,descriptionAddBtn,getDescriptionVoiceBtn,getQuestionVoiceBtn
            ,saveQuestionBtn,saveDescriptionBtn;
    ImageButton deleteImageBtn,nextImage,lastImage;

    RelativeLayout editdescription,editquestion;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    Intent intent;
    boolean first = true;
    String saveQ,saveD,delete,imageid,question,description;
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
        shared = getSharedPreferences(SHARED, 0);
        imageList = new ArrayList<EditImage>();
        saveQ = "saveQ";
        saveD = "saveD";
        delete = "delete";

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

        editquestion = (RelativeLayout) findViewById(R.id.questionLayout);
        descriptionAddBtn = (Button)findViewById(R.id.addDescription);
        descriptionAddBtn.setOnClickListener(this);
        addDescriptionText= (EditText)findViewById(R.id.descriptionEdit);
        getDescriptionVoiceBtn = (Button)findViewById(R.id.vBtn1);
        getDescriptionVoiceBtn.setOnClickListener(this);
        saveDescriptionBtn = (Button)findViewById(R.id.save1);
        saveDescriptionBtn.setOnClickListener(this);

        editdescription = (RelativeLayout) findViewById(R.id.descriptionLayout);
        questionAddBtn = (Button)findViewById(R.id.addquestion);
        questionAddBtn.setOnClickListener(this);
        addQuestionText= (EditText)findViewById(R.id.questionEdit);
        getQuestionVoiceBtn = (Button)findViewById(R.id.vBtn2);
        getQuestionVoiceBtn.setOnClickListener(this);
        saveQuestionBtn = (Button)findViewById(R.id.save2);
        saveQuestionBtn.setOnClickListener(this);

        deleteImageBtn = (ImageButton)findViewById(R.id.deleteThis);
        deleteImageBtn.setOnClickListener(this);
        nextImage = (ImageButton) findViewById(R.id.nexteditphoto);
        nextImage.setOnClickListener(this);
        lastImage = (ImageButton) findViewById(R.id.lasteditphoto);
        lastImage.setOnClickListener(this);

        editdescription.setVisibility(View.INVISIBLE);
        editquestion.setVisibility(View.INVISIBLE);

        backgroundImageDispplay = (ImageView)findViewById(R.id.backgroundImage);



        if(this.getIntent().getExtras()!=null){
            String displayimage;
            try {
                displayimage = getIntent().getExtras().getString("image");
                Picasso.with(ImageEditActivity.this).load(displayimage).into(backgroundImageDispplay);
            } catch (NullPointerException e ) {
                displayimage = "";
            }
            final String image = displayimage;

            //Toast.makeText(ImageEditActivity.this,image,Toast.LENGTH_LONG).show();
            //imageid = b.getString("id");

            try {
                imageid = getIntent().getExtras().getString("id");


            } catch (NullPointerException e ) {
                imageid = "";
            }

            try {
                question = getIntent().getExtras().getString("question");
            } catch (NullPointerException e ) {
                question = "";
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

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

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

    @Override
    public void onClick(View v) {
        if(v==descriptionAddBtn){
            deleteImageBtn.setVisibility(View.INVISIBLE);
            editquestion.setVisibility(View.INVISIBLE);
            editdescription.setVisibility(View.VISIBLE);
            first=true;
            addQuestionText.setText("");
            addDescriptionText.setText("");

        }
        if(v==questionAddBtn){
            deleteImageBtn.setVisibility(View.INVISIBLE);
            editdescription.setVisibility(View.INVISIBLE);
            editquestion.setVisibility(View.VISIBLE);
            addQuestionText.setText("");
            addDescriptionText.setText("");
            first=false;
        }
        if(v==getDescriptionVoiceBtn){
            promptSpeechInput();
        }
        if(v==getQuestionVoiceBtn){
            promptSpeechInput();
        }
        if(v==saveQuestionBtn){
            deleteImageBtn.setVisibility(View.VISIBLE);
            question = addQuestionText.getText().toString();
            volleyPut(saveQ);
            editquestion.setVisibility(View.INVISIBLE);
        }
        if(v==saveDescriptionBtn){
            deleteImageBtn.setVisibility(View.VISIBLE);
            description = addDescriptionText.getText().toString();
            editdescription.setVisibility(View.INVISIBLE);
            volleyPut(saveD);

        }
        if(v==nextImage){
            counter++;
            if(counter>= imageList.size()){
                counter=imageList.size();
                voice.speak("No More images waiting to be edited", TextToSpeech.QUEUE_FLUSH, null);
                finish();
            }else{
                loadUpDisplayScreen();
            }

        }
        if(v==lastImage){
            counter--;
            if(counter<= 0){
                counter=0;
                voice.speak("No More images waiting to be edited", TextToSpeech.QUEUE_FLUSH, null);
                finish();
            }else{
                loadUpDisplayScreen();
            }

        }
        if (v == deleteImageBtn) {

            volleyPut(delete);
        }

    }


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
            Toast.makeText(getApplicationContext(),
                    "speech_not_supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(first){
                        addDescriptionText.setText(result.get(0).trim().toString());
                    }else{
                        addQuestionText.setText(result.get(0).trim().toString());
                    }

                } else if (null == data) {
                    finish();
                }
            }
        }
    }

    @Override
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

    @Override
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

    public void volleyPut(String task){
        final ProgressDialog waiting = ProgressDialog.show(this,"Editing image details...",
                "Uploading changes..", false, false);
        final String thisJob = task;

        Toast.makeText(ImageEditActivity.this,thisJob+" : "+imageid+" : "+question+" : "+description+" : "+IMAGE_EDIT_URL,Toast.LENGTH_LONG).show();
        StringRequest myQuery = new StringRequest(Request.Method.POST,IMAGE_EDIT_URL,new Response.Listener<String>(){
            @Override
            public void onResponse(String comeBack){
                String type = comeBack.trim();
                editquestion.setVisibility(View.INVISIBLE);
                editdescription.setVisibility(View.INVISIBLE);
                if(type.equalsIgnoreCase("successful")){
                    waiting.dismiss();
                    Log.d("myTag", "found");
                    Toast.makeText(ImageEditActivity.this,comeBack,Toast.LENGTH_LONG).show();
                    editquestion.setVisibility(View.INVISIBLE);
                    editdescription.setVisibility(View.INVISIBLE);

                }else{
                    waiting.dismiss();
                    Log.d("myTag", "notfound");
                    Toast.makeText(ImageEditActivity.this, comeBack, Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        waiting.dismiss();
                        Toast.makeText(ImageEditActivity.this,"ERROR",Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", imageid);
                map.put("job",thisJob);
                if(thisJob.equalsIgnoreCase("saveQ")) {
                    map.put("question", question);
                }
                if(thisJob.equalsIgnoreCase("saveD")) {
                    map.put("description",description);
                }



                return map;
            }
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(myQuery);

    }

    public void getEditPhotos(String email){
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Getting Next Image..", false, false);
        String finalURL= NEED_EDIT_URL+email;
        Toast.makeText(ImageEditActivity.this,finalURL, Toast.LENGTH_LONG).show();

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
                        Toast.makeText(ImageEditActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
            voice.speak("There are no images waiting to be edited", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            try {
                for (int i = 0; i < photosFromServer.length(); i++) {
                    JSONObject obj = null;
                    obj = photosFromServer.getJSONObject(i);
                    EditImage editElement = new EditImage();
                    editElement.edit_image_id = obj.getString("id");
                    editElement.edit_image = obj.getString("photo");
                    //editElement.edit_description = obj.getString("description");
                    //editElement.edit_question = obj.getString("description");




                    imageList.add(editElement);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(ImageEditActivity.this,print,Toast.LENGTH_LONG).show();
            loadUpDisplayScreen();
        }

    }

    public void loadUpDisplayScreen(){
        //setUpEmotionBar();
        Picasso.with(ImageEditActivity.this)
                .load(imageList.get(counter).edit_image)
                .skipMemoryCache()
                .into(backgroundImageDispplay);
        imageid= imageList.get(counter).edit_image_id;

    }




}
