package bykkmouse.lipnet;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, View.OnClickListener {

    TextureView textureView;
    ImageView recordBtn;

    //카메라 객체
    Camera camera;
    //카메라에서 지원하는 preview size 목록
    List<Camera.Size> supportedPreviewSizes;
    //최종 결정된 preview size
    Camera.Size previewSize;

    SurfaceTexture surface;

    MediaRecorder mediaRecorder;
    boolean isRecording;

    Drawable normalDr;
    Drawable activeDr;
    //촬영 방향 각도
    int result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_camera);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 200);

        }else {
            setContentView(R.layout.activity_camera);

            textureView = (TextureView)findViewById(R.id.lab2_textureview);
            recordBtn=(ImageView)findViewById(R.id.lab2_btn);

            textureView.setSurfaceTextureListener(this);
            recordBtn.setOnClickListener(this);

        }

        normalDr= ResourcesCompat.getDrawable(getResources(), R.drawable.ic_btn_normal, null);
        activeDr=ResourcesCompat.getDrawable(getResources(), R.drawable.ic_btn_recording, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==200 && grantResults.length>0) {

            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED) {
                setContentView(R.layout.activity_camera);

                textureView = (TextureView)findViewById(R.id.lab2_textureview);

                textureView.setSurfaceTextureListener(this);


                recordBtn=(ImageView)findViewById(R.id.lab2_btn);
                recordBtn.setOnClickListener(this);
            }
        }
    }



    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //카메라 점유
        camera = Camera.open(1);

        Camera.Parameters parameters = camera.getParameters();

        supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        if (supportedPreviewSizes != null) {
            previewSize = CameraUtil.getOptimalPreviewSize(supportedPreviewSizes, width, height);
            parameters.setPreviewSize(previewSize.width, previewSize.height);
        }

        int result = CameraUtil.setCameraDisplayOrientation(this, 1);
        this.result=result;
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        //사진 촬영시 방향이 안맞는 데이터 전달
        parameters.setRotation(result);

        //camera.setParameters(parameters);
        //화면에 출력되는 형상의 방향
        camera.setDisplayOrientation(result); // result

        try {
            camera.setPreviewTexture(surface);
        } catch (IOException t) {
        }

        camera.startPreview();

        this.surface=surface;

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, the Camera does all the work for us
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        camera.stopPreview();
        camera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Update your view here!
    }

    @Override
    public void onClick(View v) {
        if (camera != null) {
            //add~~~~~~~~~~~~~~~~~~~~~~~~~
            if(isRecording){
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder=null;

                isRecording=false;

                recordBtn.setImageDrawable(normalDr);
            }else {
                try{
                    //File dir=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myApp");
                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                            + File.separator + "Camera" + File.separator);

                    if(!dir.exists()){
                        dir.mkdir();
                    }
                    File file=File.createTempFile("VIDEO-",".mp4", dir);
                    getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    mediaRecorder=new MediaRecorder();
                    camera.unlock();


                    mediaRecorder.setCamera(camera);
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    //Camera.Parameters params = camera.getParameters();
                    //params.setPreviewFrameRate(25);
                    //params.setPreviewFpsRange(25000,25000);
                   /* mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mediaRecorder.setVideoFrameRate(25); //frame
                    mediaRecorder.setVideoSize(800,480);

                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);*/

                   CamcorderProfile camcorderProfile=CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                   camcorderProfile.videoFrameWidth = 480;//camera.getParameters().getPreviewSize().height;
                   camcorderProfile.videoFrameHeight = 320;//camera.getParameters().getPreviewSize().width;

                    mediaRecorder.setProfile(camcorderProfile);

                    mediaRecorder.setMaxDuration(3000); //3sec
                    //mediaRecorder.setMaxFileSize();
                    //mediaRecorder.setCaptureRate(25); //frame
                    //mediaRecorder.setVideoFrameRate(25); //frame
                    mediaRecorder.setOutputFile(file.getAbsolutePath());
                    if(result == 90) {
                        result = 270;
                    }
                    mediaRecorder.setOrientationHint(result); //(result + 270) % 360
                    //mediaRecorder.setVideoEncodingBitRate(300);
                    mediaRecorder.prepare();
                    //Log.d("사이즈1",camcorderProfile.videoFrameWidth + "");
                    //Log.d("사이즈1",camcorderProfile.videoFrameHeight + "");

                    camera.unlock();

                }catch (Exception e){
                    e.printStackTrace();
                }
                mediaRecorder.start();
                isRecording=true;
                recordBtn.setImageDrawable(activeDr);
            }
        }
    }
}

