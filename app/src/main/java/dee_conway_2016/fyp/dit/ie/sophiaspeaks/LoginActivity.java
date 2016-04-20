package dee_conway_2016.fyp.dit.ie.sophiaspeaks;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


/**
 * A login screen that offers login via email/password.
 */
@SuppressWarnings("deprecation")// speak is deprecated, but the testing device is an old device so needs that version
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    //attributes to enable the activity to complete
    TextToSpeech voice;
    public static final String SHARED = "globals";

    //URLs for the volley requests
    public static final String LoginURL = "http://52.50.76.1/sophiaFYP/remotelogin.php?username=";
    public static final String WORKER_URL = "http://52.50.76.1/sophiaFYP/remoteworker.php?code=";
    SharedPreferences shared;
    Vibrator buttonVibe;

    String mUsername;
    String mPword;
    TextView error;

    private EditText userNameView;
    private EditText passwordLoginView;
    private EditText supportView;




    Button logMeIn;
    Button registerMe;

    @Override// run the activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //get the vibrator to notify the user of button clicks
        buttonVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // get the sahred pref file
        shared = getSharedPreferences(SHARED, 0);
        // set up the editboxes to accept input
        userNameView = (EditText)findViewById(R.id.editUserName);
        passwordLoginView = (EditText) findViewById(R.id.loginPassword);
        supportView =(EditText) findViewById(R.id.supportWorker);
        //assign the buttons and click listeners
        logMeIn = (Button) findViewById(R.id.submitLogin);

        registerMe = (Button)findViewById(R.id.register);
        try{
            logMeIn.setOnClickListener(this);
            registerMe.setOnClickListener(this);
        }
        catch(NullPointerException e){
            Log.d("myTag", "button null exception");
        }
        registerMe.setOnClickListener(this);
        assert registerMe !=null;

        error = (TextView) findViewById(R.id.loginError);
        assert error != null;

        error.setVisibility(View.INVISIBLE);

        //build a TextTospeech service
        voice = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    voice.setLanguage(Locale.UK);
                }
            }
        });



    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        buttonVibe.vibrate(100);
        if(v==logMeIn){
            login();
        }
        if(v==registerMe){
            register();
        }



    }
    public void register(){
        Intent intent = new Intent(this, Register.class);
        intent.putExtra("type","parent");
        startActivity(intent);
    }

    public void login() {
        if (!supportView.getText().toString().equals("")) {
            checkIfSupportLogin();
        } else {
            userNameView.setError(null);
            passwordLoginView.setError(null);


            mUsername = userNameView.getText().toString().trim();
            mPword = passwordLoginView.getText().toString().trim();

            boolean cancel = false;

            //check if email has been entered, that it is a valid email address
            //and that it has not been registered before
            if (TextUtils.isEmpty(mUsername)) {
                userNameView.setError(getString(R.string.error_field_required));

                cancel = true;
            }


            // check that password has been entered and that it matches email address
            if (TextUtils.isEmpty(mPword)) {
                passwordLoginView.setError(getString(R.string.error_field_required));

                cancel = true;
            }
            //if email and password are entered
            if (!cancel) {

                logMeIn(mUsername, mPword);
            }
        }
    }
    //volley login attemept
    public void logMeIn(String user, String pass){
        //progress dialog display
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Authenticating User details..", false, false);
        String finalURL= LoginURL+user+"&password="+pass;
        JsonArrayRequest jsonQuery = new JsonArrayRequest(JsonArrayRequest.Method.GET, finalURL,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        waiting.dismiss();
                        if(response.length()==1){
                            //if response is 1, then a user matching
                            // the credentials has been found in the database
                            // so set the credentials to the shared preferences file.
                            setcredentials(response);
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"NOT FOUND",Toast.LENGTH_LONG).show();
                        }
                    }

                },//if there is an invalid login attempt
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        voice.speak("Those credentials don't match any user...", TextToSpeech.QUEUE_FLUSH, null);
                        waiting.dismiss();
                    }
                }
        ) {
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }
    // volley worker login
    public void logMeIn(String worker){
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Authenticating User details..", false, false);
        String finalURL= WORKER_URL+worker;
        JsonArrayRequest jsonQuery = new JsonArrayRequest(JsonArrayRequest.Method.GET, finalURL,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        waiting.dismiss();
                        if(response.length()==1){
                            setWorkerCredentials(response);
                        }
                        else{// toast display that the worker credentials are wrong
                            Toast.makeText(LoginActivity.this,"WORKER NOT FOUND",Toast.LENGTH_LONG).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        waiting.dismiss();
                        Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_LONG).show();
                    }
                }
        ) {
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }
    //if login successful set the credentials to the shared preference file
    // so the data can be acccessed across all activities
    public void setcredentials(JSONArray userCredentials){

        SharedPreferences.Editor editor = shared.edit();
        try{
            JSONObject obj;// parse all data from the JSON response
            // to an editor instance for writing to the file
            obj = userCredentials.getJSONObject(0);
            editor.putString("name", obj.getString("fname"));
            editor.putString("email", obj.getString("email"));
            editor.putString("lname", obj.getString(("lname")));
            editor.putString("usertype", obj.getString("usertype"));
            editor.putString("access_code", obj.getString("access_code"));
            editor.putString("amLogged", "true");
            editor.apply();
            if(obj.getString("usertype").equalsIgnoreCase("parent"))
                gotoParent();
            else
                finish();


        }catch(JSONException e){
            e.printStackTrace();
        }

    }
    //if a worker has successfully logged in apply the credentials to the shared pref
    public void setWorkerCredentials(JSONArray userCredentials){

        SharedPreferences.Editor editor = shared.edit();
        try{
            JSONObject obj;
            obj = userCredentials.getJSONObject(0);
            editor.putString("email", obj.getString("email"));
            editor.putString("usertype","worker");
            editor.putString("amLogged", "true");
            editor.apply();

            goToGamesPage();

        }catch(JSONException e){
            e.printStackTrace();
        }


    }
    //after a successful login go to the home activity associated with your user type
    public void gotoParent(){
        Intent intent = new Intent(this, Parent.class);
        startActivity(intent);
    }
    //workers are take to the games activity
    public void goToGamesPage(){
        Intent intent = new Intent(this, AddGame.class);
        startActivity(intent);
    }
    // if a login is attemepted, check if it is a worker type login
    public void checkIfSupportLogin(){
        String amIaSupportWorker = supportView.getText().toString().trim();
        if (!TextUtils.isEmpty(amIaSupportWorker)){
            logMeIn(amIaSupportWorker);
        }
    }
}
