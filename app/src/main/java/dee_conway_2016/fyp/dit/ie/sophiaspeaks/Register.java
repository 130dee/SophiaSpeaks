package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Register extends AppCompatActivity implements View.OnClickListener{

    TextToSpeech voice;
    public static final String USERNAME = "user";
    public static final String PASSWORD = "password";
    public static final String FNAME = "first";
    public static final String LNAME = "last";
    public static final String EMAIL = "email";
    public static final String ACCESSCODE = "code";
    public static final String REG_URL = "http://52.50.76.1/sophiaFYP/remotereg.php";

    public static final String SHARED = "globals";

    private String mUsername;
    private String mPassword;
    private String mFname;
    private String mLname;
    private String mEmailAddress;
    private String mAccessCode = "parent";
    SharedPreferences shared;

    private EditText uNameView;
    private EditText passwordView;
    private EditText fNameView;
    private EditText lNameView;
    private EditText emailView;

    private TextView error,codeText,codeInstructions;

    Button submitReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shared = getSharedPreferences(SHARED, 0);

        Intent intent = getIntent();

        String type = intent.getExtras().getString("type");

        if(type.equalsIgnoreCase("support")||type.equalsIgnoreCase("child")){
            mAccessCode = type;
        }




        uNameView = (EditText)findViewById(R.id.uNameBox);
        passwordView = (EditText)findViewById(R.id.passwordBox);
        fNameView = (EditText)findViewById(R.id.fNameBox);
        lNameView = (EditText)findViewById(R.id.lNameBox);
        emailView= (EditText)findViewById(R.id.emailBox);
        codeText = (TextView)findViewById(R.id.codeBox);
        codeInstructions = (TextView)findViewById(R.id.codeInstructions);

        if(type.equalsIgnoreCase("child")){
            
            emailView.setVisibility(View.INVISIBLE);
            lNameView.setVisibility(View.INVISIBLE);
            codeText.setVisibility(View.VISIBLE);
            codeText.setText(shared.getString("access_code", "Code"));
            codeInstructions.setVisibility(View.VISIBLE);
        }

        submitReg = (Button) findViewById(R.id.submitRegister);
        submitReg.setText("Add "+mAccessCode);
        submitReg.setOnClickListener(this);


        error = (TextView) findViewById(R.id.errorView);


    }

    public void registerFormSubmit(){

        uNameView.setError(null);
        passwordView.setError(null);
        fNameView.setError(null);
        lNameView.setError(null);
        emailView.setError(null);

        mUsername = uNameView.getText().toString().trim();
        mPassword = passwordView.getText().toString().trim();
        mFname = fNameView.getText().toString().trim();
        mLname = shared.getString("lname", "lastName");
        mEmailAddress = shared.getString("email","emailAddress");
        Toast.makeText(Register.this,mAccessCode+":" + mLname,Toast.LENGTH_LONG).show();






        boolean cancel = false;
        View focusView = null;

        //check if data has been entered, that it is a valid
        //and that it has not been registered before


        if (TextUtils.isEmpty(mUsername)){
            uNameView.setError(getString(R.string.error_field_required));
            focusView = uNameView;
            cancel = true;
        }
        // check that password has been entered and that it matches email address
        if (TextUtils.isEmpty(mPassword)){
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(mFname)){
            fNameView.setError(getString(R.string.error_field_required));
            focusView = fNameView;
            cancel = true;
        }
        if(mAccessCode.equalsIgnoreCase("parent")){
            mLname =lNameView.getText().toString().trim();
            mEmailAddress = emailView.getText().toString().trim();
            Toast.makeText(Register.this,mAccessCode+":"+  mLname,Toast.LENGTH_LONG).show();
            if (TextUtils.isEmpty(mLname)){
                lNameView.setError(getString(R.string.error_field_required));
                focusView = lNameView;
                cancel = true;
            }
            if (TextUtils.isEmpty(mEmailAddress)){
                emailView.setError(getString(R.string.error_field_required));
                focusView = emailView;
                cancel = true;
            }
            else if(!isEmailValid(mEmailAddress)) {
                emailView.setError(getString(R.string.error_invalid_email));
                focusView = emailView;
                cancel = true;
            }
        }


        //if email and password are entered
        if(!cancel){
            cancel = false;
            regMe(mUsername,mPassword,mFname,mLname,mEmailAddress, mAccessCode);
        }

    }
    //Boolean Methods to check if the fields are valid(NOT ROBUST)
    private boolean isEmailValid(String email) {
        return email.contains("@")&& email.contains(".");
    }

    private void regMe(String a, String b,String c,String d, String e, String f){
        Log.d("myTag", "Trying To login");

        final String checkThisUsername = a;
        final String checkThisPword = b;
        final String checkThisFname = c;
        final String checkThisLname = d;
        final String checkThisEmail = e;
        final String checkThisAccessCode = f;

        StringRequest myQuery = new StringRequest(Request.Method.POST,REG_URL,new Response.Listener<String>(){
            @Override
            public void onResponse(String comeBack){
                String type = comeBack.trim();
                if(type.equalsIgnoreCase("successful")){
                    Log.d("myTag", "found");
                   // Toast.makeText(Register.this,comeBack,Toast.LENGTH_LONG).show();

                }else{
                    //error.setText(type);
                    //error.setVisibility(View.VISIBLE);
                    Log.d("myTag", "notfound");
                  //  Toast.makeText(Register.this, comeBack, Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                       // Toast.makeText(Register.this,"ERROR",Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(USERNAME, checkThisUsername);
                map.put(PASSWORD, checkThisPword);
                map.put(FNAME, checkThisFname);
                map.put(LNAME, mLname);
                map.put(EMAIL, checkThisEmail);
                map.put(ACCESSCODE, mAccessCode);
                return map;
            }
        };

        RequestQueue thisQ = Volley.newRequestQueue(this);
        thisQ.add(myQuery);
    }

    @Override
    public void onClick(View v) {
        if (v==submitReg) {
            registerFormSubmit();
        }
    }
}
