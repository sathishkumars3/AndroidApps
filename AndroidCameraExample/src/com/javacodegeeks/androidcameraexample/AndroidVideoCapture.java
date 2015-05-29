package com.javacodegeeks.androidcameraexample;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.display.DisplayManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class AndroidVideoCapture extends Activity  implements SurfaceHolder.Callback{
	Button myButton;
	MediaRecorder mediaRecorder;
	SurfaceHolder surfaceHolder;
	boolean recording;
	Camera mycamera;

	   /** Called when the activity is first created. */
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       recording = false;
	       DisplayMetrics metrics=new DisplayMetrics();
	      //requestWindowFeature(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
	       getWindowManager().getDefaultDisplay().getMetrics(metrics);
	       System.out.println("Width And Height  :"+metrics.widthPixels+"  "+metrics.heightPixels);
	      Toast.makeText(getApplicationContext(), ""+metrics.widthPixels+" "+metrics.heightPixels, Toast.LENGTH_SHORT).show();
	       mediaRecorder = new MediaRecorder();
	       initMediaRecorder();
	      
	       setContentView(R.layout.custom_cam);
	      
	       SurfaceView myVideoView = (SurfaceView)findViewById(R.id.videoview);
	       surfaceHolder = myVideoView.getHolder();
	       surfaceHolder.addCallback(this);
	       surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	      
	       myButton = (Button)findViewById(R.id.mybutton);
	       myButton.setOnClickListener(myButtonOnClickListener);
	      
	   }
	  
	   private Button.OnClickListener myButtonOnClickListener= new Button.OnClickListener(){

	 @Override
	 public void onClick(View arg0) {
	  // TODO Auto-generated method stub
	  if(recording){
	   mediaRecorder.stop();
	   mediaRecorder.release();
	   finish();
	  }else{
	   mediaRecorder.start();
	   recording = true;
	   myButton.setText("STOP");
	  }
	 }};
	  
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	 // TODO Auto-generated method stub

	}
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
	 // TODO Auto-generated method stub
	 prepareMediaRecorder();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
	 // TODO Auto-generated method stub

	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	Display man=((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
	int rotation=man.getRotation();
	System.out.println(""+rotation);
	Toast.makeText(getApplicationContext(), ""+rotation, Toast.LENGTH_SHORT).show();
	}

	private void initMediaRecorder(){
		 	
	       CamcorderProfile camcorderProfile_HQ = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
	     
	      
	       
	       mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	       mediaRecorder.setProfile(camcorderProfile_HQ);
	       mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory()+File.separator+"myvideo.mp4");
	       //mediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.
	       //mediaRecorder.setMaxFileSize(5000000); // Set max file size 5M
	       mediaRecorder.setVideoSize(640, 480);
	    //   mediaRecorder.setOrientationHint(90);
	}

	private void prepareMediaRecorder(){
	 mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
	 try {
	  mediaRecorder.prepare();
	 } catch (IllegalStateException e) {
	  // TODO Auto-generated catch block
	  e.printStackTrace();
	 } catch (IOException e) {
	  // TODO Auto-generated catch block
	  e.printStackTrace();
	 }
	}
	/* public void onOrientationChanged(int orientation) {
	     if (orientation == ORIENTATION_UNKNOWN) return;
	     android.hardware.Camera.CameraInfo info =
	            new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     orientation = (orientation + 45) / 90 * 90;
	     int rotation = 0;
	     if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	         rotation = (info.orientation - orientation + 360) % 360;
	     } else {  // back-facing camera
	         rotation = (info.orientation + orientation) % 360;
	     }
	     mParameters.setRotation(rotation);
	 }*/
}

