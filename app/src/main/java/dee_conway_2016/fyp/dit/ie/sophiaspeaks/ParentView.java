package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParentView extends AppCompatActivity implements View.OnClickListener{

    Button addChild, addSupport,addGame,editImages,viewImages, editMyDetails,showNext;
    WebView nxt;

    TextToSpeech voice;

    public static final String SHARED = "globals";
    public static final String SHOW_THIS_URL = "http://52.50.76.1/sophia/getimages.php";
    public static final String MESSAGE = "message";
    private ArrayList<String> image_details;
    private ArrayList<String> image_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addChild = (Button)findViewById(R.id.addChild);
        addSupport = (Button)findViewById(R.id.addSupport);
        addGame = (Button)findViewById(R.id.addGame);
        editImages = (Button)findViewById(R.id.editImages);
        viewImages = (Button)findViewById(R.id.viewImage);
        editMyDetails = (Button)findViewById(R.id.editDetails);
        showNext = (Button) findViewById(R.id.nextImage);

        addChild.setOnClickListener(this);
        addSupport.setOnClickListener(this);
        addGame.setOnClickListener(this);
        editImages.setOnClickListener(this);
        viewImages.setOnClickListener(this);
        editMyDetails.setOnClickListener(this);
        showNext.setOnClickListener(this);

        nxt = (WebView)findViewById(R.id.currentImage);
        nxt.getSettings().setLoadWithOverviewMode(true);
        nxt.getSettings().setUseWideViewPort(true);


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
        editor.putString("email",null);
        editor.putString("amLogged", "false");
        editor.putString("type", null);
        editor.commit();


        finish();
        return true;

    }

    @Override
    public void onClick(View v) {
        if (v==addChild){
            Intent intent = new Intent(this, Register.class);
            intent.putExtra("type","child");
            startActivity(intent);
        }

        if (v==addSupport){
            Intent intent = new Intent(this, Register.class);
            intent.putExtra("type","support");
            startActivity(intent);
        }

        if (v==addGame){
            Intent intent = new Intent(this, Register.class);

            startActivity(intent);

        }
        if (v==editImages){
            Intent intent = new Intent(this, Register.class);

            startActivity(intent);

        }
        if (v==editMyDetails){
            Intent intent = new Intent(this, Register.class);
            intent.putExtra("message","sub");
            startActivity(intent);

        }
        if (v==viewImages){
            Intent intent = new Intent(this, Register.class);
            intent.putExtra("message","sub");
            startActivity(intent);

        }
        if (v==showNext){
            //get list of images that have not been viewed yet order by upload time
            getNextimage("130dee@gmail.com");
        }
    }

    public void getNextimage(String email){
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Getting Next Image..", false, false);

        JsonArrayRequest jsonQuery = new JsonArrayRequest(SHOW_THIS_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        waiting.dismiss();

                        displayCurrentImage(response);
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        waiting.dismiss();
                        Toast.makeText(ParentView.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }

        );
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }

    public void displayCurrentImage(JSONArray photosFromServer){
        Toast.makeText(ParentView.this,"got this far",Toast.LENGTH_LONG).show();

        try{

            JSONObject obj = null;
            obj = photosFromServer.getJSONObject(0);
            nxt.loadUrl(obj.getString("photo"));
        }catch(JSONException e){
            e.printStackTrace();
        }



    }
}
