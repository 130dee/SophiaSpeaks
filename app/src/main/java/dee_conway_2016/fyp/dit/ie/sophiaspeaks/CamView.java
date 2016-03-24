package dee_conway_2016.fyp.dit.ie.sophiaspeaks;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by 130de on 23/03/2016.
 */
@SuppressWarnings("deprecat")
public class CamView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder screenDisplay;
    private Camera sophiaCam;

    public CamView(Context context, Camera camera) {
        super(context);
        sophiaCam = camera;
        screenDisplay = getHolder();
        screenDisplay.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        screenDisplay.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            // create the surface and start camera preview
            if (sophiaCam == null) {
                sophiaCam.setPreviewDisplay(holder);
                sophiaCam.startPreview();
            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        showCameraScreen(sophiaCam);
    }

    public void showCameraScreen(Camera camera) {
        if (screenDisplay.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            sophiaCam.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);
        try {
            sophiaCam.setPreviewDisplay(screenDisplay);
            sophiaCam.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    public void setCamera(Camera camera) {
        //method to set a camera instance
        sophiaCam = camera;
    }
}
