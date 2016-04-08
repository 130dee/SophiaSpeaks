package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;
import android.webkit.WebSettings.ZoomDensity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MatchingGame extends AppCompatActivity {

    public static final String SHOW_THIS_URL = "http://52.50.76.1/sophia/getthreeimages.php?email=";
    ImageView a,b,c;

    private class MyWebViewClient extends WebViewClient {
        public void onPageFinished(WebView view, String url) {
            view.setInitialScale((0));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        a=(ImageView)findViewById(R.id.webView);
        b=(ImageView)findViewById(R.id.webView2);
        c=(ImageView)findViewById(R.id.webView3);



        getthreeImages("130dee@gmail.com");
    }

    public void getthreeImages(String email){
        final ProgressDialog waiting = ProgressDialog.show(this,"Searching...",
                "Getting Next Image..", false, false);
        String finalURL= SHOW_THIS_URL+email;

        Toast.makeText(MatchingGame.this, finalURL, Toast.LENGTH_LONG).show();
        JsonArrayRequest jsonQuery = new JsonArrayRequest(JsonArrayRequest.Method.GET, finalURL,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        waiting.dismiss();
                        displayImages(response);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        waiting.dismiss();
                        Toast.makeText(MatchingGame.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
        };
        RequestQueue rQue = Volley.newRequestQueue(this);
        rQue.add(jsonQuery);

    }

    public void displayImages(JSONArray photosFromServer){


        try{
            for(int i=0;i<7;i++) {
                JSONObject obj = null;
                obj = photosFromServer.getJSONObject(i);
                if(i==0) {
                    new DownloadImageTask(a).execute(obj.getString("photo"));
                }else if(i==3) {
                    new DownloadImageTask(b).execute(obj.getString("photo"));
                }else if(i==5)
                    new DownloadImageTask(c).execute(obj.getString("photo"));

            }


        }catch(JSONException e){
            e.printStackTrace();
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
}
