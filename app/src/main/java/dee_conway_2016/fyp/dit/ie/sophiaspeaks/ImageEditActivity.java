package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ImageEditActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView backgroundImageDispplay;
    EditText addQuestionText, addDescriptionText;
    Button questionAddBtn,descriptionAddBtn,getDescriptionVoiceBtn,getQuestionVoiceBtn,saveQuestionBtn,saveDescriptionBtn,deleteImageBtn;
    TextToSpeech voice;
    RelativeLayout editdescription,editquestion;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public static final String IMAGE_EDIT_URL = "http://52.50.76.1/sophia/imageedit.php";
    Intent intent;
    boolean first = true;
    String saveQ,saveD,delete,imageid,question,description;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        saveQ = "saveQ";
        saveD = "saveD";
        delete = "delete";

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();




        Bundle b = new Bundle();
        b = getIntent().getExtras();
        String image = b.getString("image");
        imageid = b.getString("id");

        try {
            question = getIntent().getExtras().getString("question");
        } catch (NullPointerException e ) {
            question = "";
        }

        try {
            question = getIntent().getExtras().getString("description");
        } catch (NullPointerException e ) {
            description = "";
        }

        backgroundImageDispplay = (ImageView)findViewById(R.id.backgroundImage);

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



        deleteImageBtn = (Button)findViewById(R.id.deleteThis);
        deleteImageBtn.setOnClickListener(this);

        editdescription.setVisibility(View.INVISIBLE);
        editquestion.setVisibility(View.INVISIBLE);


        Picasso.with(this).load(image).into(backgroundImageDispplay);


    }

    @Override
    public void onClick(View v) {
        if(v==descriptionAddBtn){
            editquestion.setVisibility(View.INVISIBLE);
            editdescription.setVisibility(View.VISIBLE);
            first=true;
            addQuestionText.setText("");
            addDescriptionText.setText("");

        }
        if(v==questionAddBtn){
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
            question = addQuestionText.getText().toString();
            volleyPut(saveQ);
        }
        if(v==saveDescriptionBtn){
            description = addDescriptionText.getText().toString();
            volleyPut(saveD);

        }
        if(v==deleteImageBtn){
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

}
