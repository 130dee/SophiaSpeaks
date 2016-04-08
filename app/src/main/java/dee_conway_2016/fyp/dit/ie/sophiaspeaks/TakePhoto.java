package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("deprecated")
public class TakePhoto extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String IMAGE = "image";
    public static final String IM_TAG = "tag";
    public static final String EMAIL = "email";
    public static final String TIMESTAMP = "tdate";

    public static final String LOCATION = "location";
    public static final String DB_URL = "http://52.50.76.1/sophia/imageupload.php";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    public static final String SHARED = "globals";
    TextToSpeech voice;
    ImageButton happy, sad, question, cancel;
    ImageView photoView;
    Bitmap bmap;
    static int TAKE_PIC =1;
    Uri outPutfileUri;
    double x,y;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        voice = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });

        question =(ImageButton) findViewById(R.id.what);
        happy = (ImageButton) findViewById(R.id.happy);
        sad = (ImageButton) findViewById(R.id.sad);
        cancel=  (ImageButton) findViewById(R.id.cancel);
        photoView = (ImageView)findViewById(R.id.showThisImage);

        question.setOnClickListener(this);
        happy.setOnClickListener(this);
        sad.setOnClickListener(this);
        cancel.setOnClickListener(this);

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                "MyPhoto.jpg");
        outPutfileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
        startActivityForResult(intent, TAKE_PIC);


        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
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
        editor.putString("email", null);
        editor.putString("amLogged", "false");
        editor.putString("type", null);
        editor.commit();

        finish();
        return true;


    }

    @Override
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
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if (requestCode == TAKE_PIC && resultCode==RESULT_OK){
            bmap = BitmapFactory.decodeFile(outPutfileUri.getPath());
            photoView.setImageBitmap(bmap);
            Toast.makeText(this, outPutfileUri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        String toSpeak = "";
        Boolean upload = false;
        if (v == cancel) {

            toSpeak = "Not sending this photo to mommy";
        }
        if (v == question) {
            toSpeak = "Hey mommy, what is this";
            upload =true;
        }
        if (v == sad) {
            toSpeak = "This image makes me sad";
            upload = true;
        }
        if (v == happy) {
            toSpeak = "I really love this photo";
            upload = true;
        }
        SharedPreferences shared = getSharedPreferences(SHARED, 0);
        String tagOn = shared.getString("email", "");
        String xyCo_ords = "geo:0,0?q="+x+","+y+"loc";
        String time_date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Toast.makeText(TakePhoto.this, time_date+": "+xyCo_ords, Toast.LENGTH_LONG).show();
        voice.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        if(upload == true){
            uploadThis(toSpeak ,tagOn, xyCo_ords, time_date);
        }


    }
    public String convertBmap(Bitmap imageBitmap){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        byte[] bytes = out.toByteArray();
        String imageIsReady = Base64.encodeToString(bytes, Base64.DEFAULT);
        return imageIsReady;
    }


    public void uploadThis(String tag , String mailtag, String xy, String td){
        final String tdate = td;
        final String myLoc = xy;
        final String addMyMail = mailtag;
        final String maidenTag = tag;
        final ProgressDialog doingShit = ProgressDialog.show(this,"Uploading image...",
                "Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DB_URL,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doingShit.dismiss();
                        Toast.makeText(TakePhoto.this, response, Toast.LENGTH_LONG).show();
                        if(response.equalsIgnoreCase("success")){
                            voice.speak("that was sent to mommy", TextToSpeech.QUEUE_FLUSH, null);
                            finish();
                        }else{
                            voice.speak(response, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                },


                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        doingShit.dismiss();
                        Toast.makeText(TakePhoto.this, error.toString(), Toast.LENGTH_LONG).show();
                        voice.speak("there may be an internet error", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }

        ){
            protected Map<String,String>getParams() throws AuthFailureError{
                String image = convertBmap(bmap);


                Map<String,String> params = new Hashtable<String, String>();

                params.put(IMAGE, image);
                params.put(IM_TAG, maidenTag);
                params.put(EMAIL, addMyMail);
                params.put(LOCATION, myLoc);
                params.put(TIMESTAMP, tdate);


                return params;
            }
        };
        RequestQueue que = Volley.newRequestQueue(this);
        que.add(stringRequest);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            x =  mLastLocation.getLatitude();
            y = mLastLocation.getLongitude();
        } else {
            Toast.makeText(this, "Nathin found like", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
