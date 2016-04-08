package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import java.io.RandomAccessFile;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.nearby.messages.internal.UnpublishRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Map;


public class RecordActivity extends AppCompatActivity implements View.OnClickListener {
    Button play,stop,record,send,cancel;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;

    public static final String SOUND_URL = "http://52.50.76.1/sophia/soundload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        play=(Button)findViewById(R.id.playcontent);
        stop=(Button)findViewById(R.id.stoprecord);
        record=(Button)findViewById(R.id.recordcontent);
        cancel=(Button)findViewById(R.id.cancelcontent);
        send=(Button)findViewById(R.id.sendcontent);
        play.setOnClickListener(this);
        stop.setOnClickListener(this);
        record.setOnClickListener(this);
        cancel.setOnClickListener(this);
        send.setOnClickListener(this);

        setupRecorder();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        if(v==record){
            try {
                myAudioRecorder.prepare();
                myAudioRecorder.start();
            }

            catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            record.setEnabled(false);
            stop.setEnabled(true);

            //Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        }

        if(v==stop){
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder  = null;

            stop.setEnabled(false);
            play.setEnabled(true);

            //Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
        }
        if(v==play){
            MediaPlayer m = new MediaPlayer();

            try {
                m.setDataSource(outputFile);
            }

            catch (IOException e) {
                e.printStackTrace();
            }

            try {
                m.prepare();
            }

            catch (IOException e) {
                e.printStackTrace();
            }

            m.start();
            //Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
        }
        if(v==cancel){
            setupRecorder();

        }
        if(v==send){
            uploadSOUNDBITE();
        }


    }
    public String convertBmap() {
        String imageIsReady = "String";
        try {
            RandomAccessFile f = new RandomAccessFile(outputFile, "r");
            byte[] bytes = new byte[(int) f.length()];
            f.read(bytes);

            imageIsReady = Base64.encodeToString(bytes, Base64.DEFAULT);


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imageIsReady;
    }



    public void uploadSOUNDBITE(){

        final ProgressDialog doingShit = ProgressDialog.show(this,"Uploading soundBite...",
                "Please wait...",false,false);
        //Toast.makeText(getApplicationContext(), SOUND_URL + "::::"+outputFile, Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SOUND_URL,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                doingShit.dismiss();
                if(response.equalsIgnoreCase("success")){
                    Toast.makeText(getApplicationContext(), "SUCESS RESPONE:"+response, Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "ERROR RESPONE:"+response, Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "  :SUMAT WRONG", Toast.LENGTH_LONG).show();
                        doingShit.dismiss();
                    }
                }

        ){
            protected Map<String,String> getParams() throws AuthFailureError {
                String sound = convertBmap();
                //Toast.makeText(getApplicationContext(), sound, Toast.LENGTH_LONG).show();

                Map<String,String> params = new Hashtable<String, String>();

                params.put("sound", sound);
                params.put("type", "question");
                params.put("email", "130dee@gmail.com");
                params.put("imageid", "14");



                return params;
            }
        };
        RequestQueue que = Volley.newRequestQueue(this);
        que.add(stringRequest);
    }

    public void setupRecorder(){
        record.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sound.3gp";;

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }
}