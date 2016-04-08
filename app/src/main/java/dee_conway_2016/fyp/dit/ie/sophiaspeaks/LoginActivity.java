package dee_conway_2016.fyp.dit.ie.sophiaspeaks;


import android.app.ProgressDialog;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public static final String LoginURL = "http://52.50.76.1/sophia/remotelogin.php?username=";
    SharedPreferences shared;

    private String mUsername;
    private String mPword;

    private EditText userNameView;
    private EditText passwordLoginView;
    private TextView error;



    Button logMeIn;
    Button registerMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        shared = getSharedPreferences(SHARED, 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameView = (EditText)findViewById(R.id.editUserName);
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

        userNameView.setText("onethir");
        passwordLoginView.setText("super");


    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first


        userNameView.setText("onethirty");
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
        passwordLoginView.setError(null);

        mUsername = userNameView.getText().toString().trim();
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


        // check that password has been entered and that it matches email address
        if (TextUtils.isEmpty(mPword)){
            passwordLoginView.setError(getString(R.string.error_field_required));
            focusView = passwordLoginView;
            cancel = true;
        }
        //if email and password are entered
       if(!cancel){
           cancel = false;
           logMeIn(mUsername,mPword);
       }
    }

    public void logMeIn(String user, String pass){
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Authenticating User details..", false, false);
        String finalURL= LoginURL+user+"&password="+pass;
        JsonArrayRequest jsonQuery = new JsonArrayRequest(JsonArrayRequest.Method.GET, finalURL,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        waiting.dismiss();
                        if(response.length()==1){
                            Toast.makeText(LoginActivity.this, response.length()+"",Toast.LENGTH_LONG).show();
                            setcredentials(response);
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"NOT FOUND",Toast.LENGTH_LONG).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        waiting.dismiss();
                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        ) {
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }

    public void setcredentials(JSONArray userCredentials){
        SharedPreferences shared = getSharedPreferences(SHARED, 0);
        SharedPreferences.Editor editor = shared.edit();
        Toast.makeText(LoginActivity.this,"INSIDE:"+shared.getString("usertype","type"),Toast.LENGTH_LONG).show();
        try{
            JSONObject obj = null;
            obj = userCredentials.getJSONObject(0);
            editor.putString("name", obj.getString("fname"));
            editor.putString("email", obj.getString("email"));
            editor.putString("lname", obj.getString(("lname")));
            editor.putString("usertype", obj.getString("usertype"));
            editor.putString("amLogged", "true");
            editor.commit();
            if(obj.getString("usertype").equalsIgnoreCase("parent"))
                gotoParent();
            else
                finish();


        }catch(JSONException e){
            e.printStackTrace();
        }


    }
    public void gotoParent(){
        Intent intent = new Intent(this, ParentView.class);
        startActivity(intent);
    }
}
