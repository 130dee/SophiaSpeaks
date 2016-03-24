package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton goToCamera,shownAlbum;

    static int TAKE_PIC =1;
    Uri outPutfileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        goToCamera = (ImageButton) findViewById(R.id.cameraButton);
        shownAlbum = (ImageButton) findViewById(R.id.albumButton);

        goToCamera.setOnClickListener(this);
        shownAlbum.setOnClickListener(this);
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
        if(v == goToCamera) {
            Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(Environment.getExternalStorageDirectory(),
                    "MyPhoto.jpg");
            outPutfileUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
            startActivityForResult(intent, TAKE_PIC);
        }

        if(v == shownAlbum) {
            Intent intent = new Intent(this,AlbumActivity.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if (requestCode == TAKE_PIC && resultCode==RESULT_OK){
            showImage(outPutfileUri);
            Toast.makeText(this, outPutfileUri.toString(),Toast.LENGTH_LONG).show();
        }
    }

    protected void showImage(Uri image){
        Intent intent = new Intent(this,DisplayPhoto.class);
        String showThisPhoto = image.getPath().toString();
        intent.putExtra("photo",showThisPhoto);
        startActivity(intent);

    }
}
