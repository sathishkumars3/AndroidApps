package com.javacodegeeks.androidcameraexample;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

/**
 * @author 
 */
public class CameraSurfaceView extends Activity {

    private Preview mPreview;
    private MediaRecorder mrec = new MediaRecorder();
    private int cameraId = 0;
    private Camera mCamera;
    SurfaceView myPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
       
        setContentView(R.layout.main_details);
        myPreview=(SurfaceView) findViewById(R.id.surfaceView);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Start");
        menu.add(0, 1, 0, "Stop");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                try {
                    startRecording();
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    mrec.release();
                }
                break;

            case 1: 
                mrec.stop();
                mrec.release();
                mrec = null;
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void startRecording() throws IOException  {

        mrec = new MediaRecorder();
        mrec.setCamera(mCamera);
        mCamera.unlock();
        File directory = new File(Environment.getExternalStorageDirectory()+"/NICUVideos");
        directory.mkdirs();
        mrec.setAudioSource( MediaRecorder.AudioSource.MIC);
        mrec.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mrec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mrec.setOutputFile(Environment.getExternalStorageDirectory()+"/NICUVideos/"+System.currentTimeMillis()+".mp4"); 
        mrec.setPreviewDisplay(mPreview.getHolder().getSurface());
        mrec.setVideoSize(640, 480);

        Method[] methods = mrec.getClass().getMethods();
        for (Method method: methods){
            try{
                if(method.getName().equals("setAudioEncodingBitRate")){
                    method.invoke(mrec, 12200);
                }
                else if(method.getName().equals("setVideoEncodingBitRate")){
                    method.invoke(mrec, 800000);
                }
                else if(method.getName().equals("setAudioSamplingRate")){
                    method.invoke(mrec, 8000);
                }
                else if(method.getName().equals("setVideoFrameRate")){
                    method.invoke(mrec, 20);
                }
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            } 
            catch (IllegalAccessException e) {
                e.printStackTrace();
            } 
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        mrec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mrec.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        mrec.setMaxDuration(60000); // 60 seconds
        mrec.setMaxFileSize(10000000); // Approximately 10 megabytes
        mrec.prepare();
        mrec.start();
    }

    protected void stopRecording() {
        mrec.stop();
        mrec.release();
        mCamera.release();
    }

    

}
