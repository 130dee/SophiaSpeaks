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

import org.json.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ImageEditActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView backgroundImageDispplay;
    EditText addQuestion, addDescription;
    Button getVoice1,getVoice2,saveImage,deleteImage;
    TextToSpeech voice;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public static final String IMAGE_EDIT_URL = "http://52.50.76.1/sophia/imageedit.php";
    Intent intent;
    boolean first = true;
    String save,delete,imageid,question,description;
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
        save = "save";
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
        addQuestion= (EditText)findViewById(R.id.editText);
        addDescription= (EditText)findViewById(R.id.editText2);
        getVoice1 = (Button)findViewById(R.id.vBtn1);
        getVoice2 = (Button)findViewById(R.id.vBtn2);
        saveImage = (Button)findViewById(R.id.saveMe);
        deleteImage = (Button)findViewById(R.id.deleteMe);

        getVoice1.setOnClickListener(this);
        getVoice2.setOnClickListener(this);
        saveImage.setOnClickListener(this);
        deleteImage.setOnClickListener(this);



            addQuestion.setText(question);


            addDescription.setText(question);


        new DownloadImageTask(backgroundImageDispplay).execute(image);


    }

    @Override
    public void onClick(View v) {
        if(v==getVoice1){
            first = true;

            promptSpeechInput();
        }
        if(v==getVoice2){
            first = false;
            addDescription.setText(getAvoiceInput());
            promptSpeechInput();
        }
        if(v==saveImage){
            volleyPut(save);
        }
        if(v==deleteImage){
            volleyPut(delete);
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public String getAvoiceInput(){
        return "SET THIS TEXT";
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
                        addQuestion.setText(result.get(0).toString());
                    }else{
                        addDescription.setText(result.get(0).toString());
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
                if(type.equalsIgnoreCase("successful")){
                    waiting.dismiss();
                    Log.d("myTag", "found");
                    Toast.makeText(ImageEditActivity.this,comeBack,Toast.LENGTH_LONG).show();

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
                if(thisJob.equalsIgnoreCase("save")){
                    map.put("question",addQuestion.getText().toString());
                    map.put("description",addDescription.getText().toString());
                }



                return map;
            }
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(myQuery);

    }

}
