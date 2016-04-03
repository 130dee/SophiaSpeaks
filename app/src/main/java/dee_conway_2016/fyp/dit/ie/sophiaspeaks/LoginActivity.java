package dee_conway_2016.fyp.dit.ie.sophiaspeaks;


import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    TextToSpeech voice;
    public static final String SHARED = "globals";
    public static final String myUsername = "username";
    public static final String myEmail = "email";
    public static final String mypwordKey = "password";
    public static final String LoginURL = "http://52.50.76.1/sophia/remotelogin.php";


    private String mUsername;
    private String mEmailAddress;
    private String mPword;
    private String amLoggedIn = "false";
    private String myName;

    private EditText userNameView;
    private EditText emailLoginView;
    private EditText passwordLoginView;
    private TextView error;



    Button logMeIn;
    Button registerMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameView = (EditText)findViewById(R.id.editUserName);
        emailLoginView = (EditText) findViewById(R.id.loginEmail);
        passwordLoginView = (EditText) findViewById(R.id.loginPassword);

        logMeIn = (Button) findViewById(R.id.submitLogin);
        logMeIn.setOnClickListener(this);
        registerMe = (Button)findViewById(R.id.register);
        registerMe.setOnClickListener(this);

        error = (TextView) findViewById(R.id.loginError);
        assert error != null;
        assert error != null;
        error.setVisibility(View.INVISIBLE);

        //for testing

        emailLoginView.setText("130dee@gmail.com");
        passwordLoginView.setText("super");


    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first


        emailLoginView.setText("130dee@gmail.com");
        passwordLoginView.setText("super4");
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

    public void login(){

        userNameView.setError(null);
        emailLoginView.setError(null);
        passwordLoginView.setError(null);

        mUsername = userNameView.getText().toString().trim();
        mEmailAddress = emailLoginView.getText().toString().trim();
        mPword = passwordLoginView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        //check if email has been entered, that it is a valid email address
        //and that it has not been registered before
        if (TextUtils.isEmpty(mUsername)){
            userNameView.setError(getString(R.string.error_field_required));
            focusView = userNameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(mEmailAddress)){
            emailLoginView.setError(getString(R.string.error_field_required));
            focusView = emailLoginView;
            cancel = true;
        }
        else if(!isEmailValid(mEmailAddress)) {
            emailLoginView.setError(getString(R.string.error_invalid_email));
            focusView = emailLoginView;
            cancel = true;
        }


        // check that password has been entered and that it matches email address
        if (TextUtils.isEmpty(mPword)){
            passwordLoginView.setError(getString(R.string.error_field_required));
            focusView = passwordLoginView;
            cancel = true;
        }
        //if email and password are entered
       if(!cancel){
           cancel = false;
           logMeIn(mUsername,mEmailAddress,mPword);



       }

    }
    //Boolean Methods to check if the fields are valid(NOT ROBUST)
    private boolean isEmailValid(String email) {
        return email.contains("@")&& email.contains(".");
    }






    private void logMeIn(String a, String b,String c){
        Log.d("myTag", "Trying To login");
        final String checkThisUname =a;
        final String checkThisEmail = b;
        final String checkThisPword = c;

        StringRequest myQuery = new StringRequest(Request.Method.POST,LoginURL,new Response.Listener<String>(){
            @Override
            public void onResponse(String comeBack){
                String type = comeBack.trim();
                if(!type.equalsIgnoreCase("not found")){
                    Log.d("myTag", "found");
                    SharedPreferences shared = getSharedPreferences(SHARED,0);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("name",checkThisUname);
                    editor.putString("email",checkThisEmail);
                    editor.putString("amLogged","true");
                    editor.putString("type",type);
                    editor.commit();
                    finish();
                }else{
                    error.setVisibility(View.VISIBLE);
                    Log.d("myTag", "notfound");
                    Toast.makeText(LoginActivity.this,comeBack,Toast.LENGTH_LONG).show();
                }
            }
        },


                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(LoginActivity.this,"ERROR",Toast.LENGTH_LONG).show();
                    }
                }


        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> map = new HashMap<String, String>();
                map.put(myUsername, checkThisUname);
                map.put(myEmail, checkThisEmail);
                map.put(mypwordKey, checkThisPword);
                return map;
            }
        };

        RequestQueue thisQ = Volley.newRequestQueue(this);
        thisQ.add(myQuery);
    }
}
