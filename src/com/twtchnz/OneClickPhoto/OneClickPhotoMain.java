package com.twtchnz.OneClickPhoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.*;

public class OneClickPhotoMain extends FragmentActivity {

    private ImageView viewImage;

    private Button buttonSend;
    private Button buttonTakePicture;

    private ProgressBar progressBar;

    private GoogleServicesChecker googleServicesChecker;

    private LocationTracker locationTracker;

    private Camera camera;

    private SocketConnectionActivity socketConnection;

    private String imgUriToSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        viewImage = (ImageView) findViewById(R.id.viewImage);

        buttonSend = (Button) findViewById(R.id.sendButton);
        buttonSend.setVisibility(View.INVISIBLE);

        buttonTakePicture = (Button) findViewById(R.id.takePictureButton);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        googleServicesChecker = new GoogleServicesChecker(this);
        locationTracker = LocationTrackerFactory.build(this);
        camera = new Camera(this);

        socketConnection = new SocketConnectionActivity(this);

        if(!googleServicesChecker.isServicesAvailable())
            Log.d(OneClickPhotoUtils.APPTAG, getString(R.string.services_unavailable));
    }

    @Override
    public void onStart() {
        super.onStart();
        locationTracker.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationTracker.resume();
    }

    @Override
    public void onStop() {
        locationTracker.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationTracker.pause();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case OneClickPhotoUtils.ACTION_TAKE_PHOTO_SMALL:
                    camera.handleSmallCameraPhoto(data);
                    break;
                case OneClickPhotoUtils.ACTION_TAKE_PHOTO_BIG:
                    camera.handleBigCameraPhoto(data);
                    break;
            }
        }
    }

    public void sendPicture(View view) {
        socketConnection.start();
        buttonSend.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Uploading.", Toast.LENGTH_SHORT).show();
    }

    public void takePicture(View view) {
        locationTracker.start();
        camera.takePicture();
    }

    public void setImgUriToSend(String photoPath) {
        this.imgUriToSend = photoPath;
        buttonSend.setVisibility(View.VISIBLE);
    }

    public void processBitmapToSend() {
        final int BUFFER_SIZE = 128 * 1024;
        try {

            byte[] tempBuff = new byte[BUFFER_SIZE];

            Log.d(OneClickPhotoUtils.APPTAG, imgUriToSend);
            File file = new File(imgUriToSend);
            FileInputStream fin = new FileInputStream(file);
            BufferedInputStream buf = new BufferedInputStream(fin, BUFFER_SIZE);

            String address = locationTracker.getAddress();

            socketConnection.sendBinaryMessage(address.getBytes("UTF-8"));

            int count;
            while ((count = buf.read(tempBuff, 0, BUFFER_SIZE)) > -1) {
                if(count == BUFFER_SIZE) {
                    socketConnection.sendBinaryMessage(tempBuff);
                    tempBuff = new byte[BUFFER_SIZE];
                } else {
                    byte[] buff = new byte[count];
                    System.arraycopy(tempBuff, 0, buff, 0, count);
                    Log.d(OneClickPhotoUtils.APPTAG, String.valueOf(buff.length));
                    socketConnection.sendBinaryMessage(buff);
                }
            }

            fin.close();
            buf.close();

        } catch (FileNotFoundException e) {
            Log.d(OneClickPhotoUtils.APPTAG, e.toString());
        } catch (IOException e) {
            Log.d(OneClickPhotoUtils.APPTAG, e.toString());
        }

        socketConnection.disconnect();
    }

    public void pictureSent() {
        Toast.makeText(this, "Uploading completed.", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.INVISIBLE);
    }

    public ImageView getImageView() {
        return this.viewImage;
    }

    public void setImageView(Bitmap imageBitmap) {
        this.viewImage.setImageBitmap(imageBitmap);
    }

}
