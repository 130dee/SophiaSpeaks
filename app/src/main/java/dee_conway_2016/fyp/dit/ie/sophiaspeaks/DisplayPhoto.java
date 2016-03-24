package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class DisplayPhoto extends AppCompatActivity implements  View.OnClickListener{

    ImageView displayArea;
    ImageButton kill;
    TextView txtBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        File imgFile = new  File(intent.getStringExtra("photo"));


        if(imgFile.exists())
        {
            displayArea = (ImageView) findViewById(R.id.imageView);
            Bitmap bmap = BitmapFactory.decodeFile(intent.getStringExtra("photo"));//Uri.fromFile(imgFile)
            displayArea.setImageBitmap(bmap);

        }




        kill = (ImageButton) findViewById(R.id.happyButton);
        kill.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
