package dee_conway_2016.fyp.dit.ie.sophiaspeaks;


import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

    public static final String myEmail = "email";
    public static final String mypwordKey = "password";
    public static final String LoginURL = "http://52.50.76.1/fyp/remotelogin.php";

    private EditText emailLoginView;
    private EditText passwordLoginView;

    ImageButton logMeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLoginView = (EditText) findViewById(R.id.loginEmail);
        passwordLoginView = (EditText) findViewById(R.id.loginPassword);

        logMeIn = (ImageButton) findViewById(R.id.SubmitLogin);
        logMeIn.setOnClickListener(this);

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

        LogMeIn();

    }

    public void LogMeIn(){

        emailLoginView.setError(null);
        passwordLoginView.setError(null);

        String email = emailLoginView.getText().toString().trim();
        String pWord = passwordLoginView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        //check if email has been entered, that it is a valid email address
        //and that it has not been registered before
        if (TextUtils.isEmpty(email)){
            emailLoginView.setError(getString(R.string.error_field_required));
            focusView = emailLoginView;
            cancel = true;
        }
        else if(!isEmailValid(email)) {
            emailLoginView.setError(getString(R.string.error_invalid_email));
            focusView = emailLoginView;
            cancel = true;
        }


        // check that password has been entered and that it matches email address
        if (TextUtils.isEmpty(pWord)){
            passwordLoginView.setError(getString(R.string.error_field_required));
            focusView = passwordLoginView;
            cancel = true;
        }
        //if email and password are entered
       if(!cancel){
           cancel = false;
           logMeIn(email,pWord);
           passwordLoginView.setError(getString(R.string.no_user_found));
           focusView = passwordLoginView;


       }

    }
    //Boolean Methods to check if the fields are valid(NOT ROBUST)
    private boolean isEmailValid(String email) {
        return email.contains("@")&& email.contains(".");
    }

    private void logMeIn(String a, String b){
        Log.d("myTag", "Trying To login");
        final String checkThisEmail = a;
        final String checkThisPword = b;

        StringRequest myQuery = new StringRequest(Request.Method.POST,LoginURL,new Response.Listener<String>(){
            @Override
            public void onResponse(String comeBack){
                if(comeBack.trim().equals("exists")){
                    Log.d("myTag", "found");
                    finish();
                }else{

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
                map.put(myEmail, checkThisEmail);
                map.put(mypwordKey, checkThisPword);
                return map;
            }
        };

        RequestQueue thisQ = Volley.newRequestQueue(this);
        thisQ.add(myQuery);
    }


}
