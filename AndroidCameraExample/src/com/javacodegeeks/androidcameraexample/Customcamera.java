package com.javacodegeeks.androidcameraexample;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

public class Customcamera extends Activity implements OnClickListener {

private SurfaceView preview = null;
private SurfaceHolder previewHolder = null;
private Camera camera = null;
private boolean inPreview = false;
Bitmap bmp;
static Bitmap mutableBitmap;
PointF start = new PointF();
PointF mid = new PointF();
float oldDist = 1f;
File imageFileName = null;
File imageFileFolder = null;
private MediaScannerConnection msConn;
Display d;
int screenhgt, screenwdh;
ProgressDialog dialog;
boolean oAllow;
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.custom_cam);

    preview = (SurfaceView) findViewById(R.id.videoview);

    previewHolder = preview.getHolder();
    previewHolder.addCallback(surfaceCallback);
    previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    previewHolder.setFixedSize(getWindow().getWindowManager()
            .getDefaultDisplay().getWidth(), getWindow().getWindowManager()
            .getDefaultDisplay().getHeight());

}

@Override
public void onResume() {
    super.onResume();
    camera = Camera.open();
    //setCameraDisplayOrientation(this, 0, camera);
}
@Override
	public void onConfigurationChanged(Configuration newConfig) {
		//super.onConfigurationChanged(newConfig);
		  Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		    int orientation = display.getOrientation(); 
		    switch(orientation) {
		        case Configuration.ORIENTATION_PORTRAIT:
		        	Toast.makeText(getApplicationContext(), "Orientation Portrait", Toast.LENGTH_SHORT).show();
		      //      if(!oAllow) {
		                    setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		                    
		                    
		                    camera.setDisplayOrientation(90);
		      //      }
		            break;
		        case Configuration.ORIENTATION_LANDSCAPE:
		        	Toast.makeText(getApplicationContext(), "Orientation LandScape", Toast.LENGTH_SHORT).show();
		     //       if(!oAllow) {
		                    setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		                    camera.setDisplayOrientation(0);
		     //       }
		            break;
		    }
	}

@Override
public void onPause() {
    if (inPreview) {
        camera.stopPreview();
    }

    camera.release();
    camera = null;
    inPreview = false;
    super.onPause();
}

private Camera.Size getBestPreviewSize(int width, int height,
        Camera.Parameters parameters) {
    Camera.Size result = null;
    for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
        if (size.width <= width && size.height <= height) {
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;
                if (newArea > resultArea) {
                    result = size;
                }
            }
        }
    }
    return (result);
}

SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(previewHolder);
        } catch (Throwable t) {
            Log.e("PreviewDemo-surfaceCallback",
                    "Exception in setPreviewDisplay()", t);
            Toast.makeText(Customcamera.this, t.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
    	
        Camera.Parameters parameters = camera.getParameters();
       // parameters.set("orientation", "landscape");
        Camera.Size size = getBestPreviewSize(width, height, parameters);
        System.out.println("width :"+size.width+" height :"+size.height);
        if (size != null) {
            parameters.setPreviewSize(size.width, size.height);
          //  parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            //camera.setDisplayOrientation(90);
         //   setCameraDisplayOrientation(Customcamera.this, 0, camera);
           // parameters.setRotation(0);
         //   parameters.setSceneMode(parameters.getSceneMode());
            
            camera.setParameters(parameters);
            
            camera.startPreview();
            inPreview = true;
        }
    }
    
    

    public void surfaceDestroyed(SurfaceHolder holder) {
        // no-op
    }
};

Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
    public void onPictureTaken(final byte[] data, final Camera camera) {
        Log.i("onPictureTaken", "onPictureTaken");
        dialog = ProgressDialog.show(Customcamera.this, "", "Saving Photo");
        /*
         * new Thread() { public void run() { try { // Thread.sleep(1000); }
         * catch (Exception ex) { } onPictureTake(data, camera); }
         * }.start();
         */
        onPictureTake(data, camera);

    }
};

public void onPictureTake(byte[] data, Camera camera) {
    if (mutableBitmap != null) {
        mutableBitmap.recycle();
    }
    bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

    mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
    savePhoto(mutableBitmap);
    showPhoto();
    dialog.dismiss();
}

private void showPhoto() {
    //Intent intent = new Intent(this, EditAndPostActivity.class);
    //startActivity(intent);

}

class SavePhotoTask extends AsyncTask<byte[], String, String> {
    @Override
    protected String doInBackground(byte[]... jpeg) {
        File photo = new File(Environment.getExternalStorageDirectory(),
                "photo.jpg");
        if (photo.exists()) {
            photo.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());
            fos.write(jpeg[0]);
            fos.close();
        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        return (null);
    }
}


public void savePhoto(Bitmap bmp) {
    imageFileFolder = new File(Environment.getExternalStorageDirectory(),
            "Unipyx");
    imageFileFolder.mkdir();
    FileOutputStream out = null;
    Calendar c = Calendar.getInstance();
    String date = fromInt(c.get(Calendar.MONTH))
            + fromInt(c.get(Calendar.DAY_OF_MONTH))
            + fromInt(c.get(Calendar.YEAR))
            + fromInt(c.get(Calendar.HOUR_OF_DAY))
            + fromInt(c.get(Calendar.MINUTE))
            + fromInt(c.get(Calendar.SECOND));
    imageFileName = new File(imageFileFolder, date.toString() + ".jpg");
    try {
        out = new FileOutputStream(imageFileName);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
        scanPhoto(imageFileName.toString());
        out = null;
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public String fromInt(int val) {
    return String.valueOf(val);
}

public void scanPhoto(final String imageFileName) {
    msConn = new MediaScannerConnection(Customcamera.this,
            new MediaScannerConnectionClient() {
                public void onMediaScannerConnected() {
                    msConn.scanFile(imageFileName, null);
                    Log.i("msClient obj  in Photo Utility",
                            "connection established");
                }

              
				@Override
				public void onScanCompleted(String path, Uri uri) {
					 msConn.disconnect();
	                    Log.i("msClient obj in Photo Utility", "scan completed");
					
				}
            });
    msConn.connect();
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
        onBack();
    }
    return super.onKeyDown(keyCode, event);
}

public void onBack() {
    Log.e("onBack :", "yes");
    camera.takePicture(null, null, photoCallback);
    inPreview = false;
}

@Override
public void onClick(View v) {

}

public static void setCameraDisplayOrientation(Activity activity,
        int cameraId, android.hardware.Camera camera) {
    android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
    android.hardware.Camera.getCameraInfo(cameraId, info);
    int rotation = activity.getWindowManager().getDefaultDisplay()
            .getRotation();
    int degrees = 0;
    switch (rotation) {
    case Surface.ROTATION_0:
        degrees = 0;
        break;
    case Surface.ROTATION_90:
        degrees = 90;
        break;
    case Surface.ROTATION_180:
        degrees = 180;
        break;
    case Surface.ROTATION_270:
        degrees = 270;
        break;
    }

    int result;
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        result = (info.orientation + degrees) % 360;
        result = (360 - result) % 360; // compensate the mirror
    } else { // back-facing
        result = (info.orientation - degrees + 360) % 360;
    }
    camera.setDisplayOrientation(result);
}


}
