package com.twtchnz.OneClickPhoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera extends FragmentActivity {

    private OneClickPhotoMain parentActivity;

    private Bitmap imageBitmap;

    private String currentPhotoPath;

    private AlbumStorageDirFactory albumStorageDirFactory;

    public Camera(OneClickPhotoMain parentActivity) {
        this.parentActivity = parentActivity;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
            albumStorageDirFactory = new FroyoAlbumDirFactory();
        else
            albumStorageDirFactory = new BaseAlbumDirFactory();
    }

    public void takePicture() {
        dispatchTakePictureIntent(OneClickPhotoUtils.ACTION_TAKE_PHOTO_BIG);
    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case OneClickPhotoUtils.ACTION_TAKE_PHOTO_BIG:
                File file;

                try {
                    file = setUpPhotoFile();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                } catch (IOException e) {
                    Log.d(OneClickPhotoUtils.APPTAG, e.toString());
                }
                break;
            default:
                break;
        }

        parentActivity.startActivityForResult(takePictureIntent, actionCode);
    }

    private File setUpPhotoFile() throws IOException {
        File file = createImageFile();
        currentPhotoPath = file.getAbsolutePath();

        return file;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFilename = OneClickPhotoUtils.JPEG_FILE_PREFIX + timeStamp + "_";
        File albumFile = getAlbumDir();
        File imageFile = File.createTempFile(imageFilename, OneClickPhotoUtils.JPEG_FILE_SUFFIX, albumFile);
        return imageFile;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = albumStorageDirFactory.getAlbumStorageDir(OneClickPhotoUtils.ALBUM_NAME);

            if (storageDir != null && !storageDir.mkdirs() && !storageDir.exists()) {
                Log.d(OneClickPhotoUtils.APPTAG, "Failed to create directory");
                return null;
            }
        } else
            Log.v(OneClickPhotoUtils.APPTAG, "External storage is not mounted READ/WRITE.");

        return storageDir;
    }

    public void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        imageBitmap = (Bitmap) extras.get("data");
        parentActivity.setImageView(imageBitmap);
    }

    public void handleBigCameraPhoto(Intent intent) {
        if (currentPhotoPath != null) {
            setPic();
            sendToProcess();
            currentPhotoPath = null;
        }

    }

    private void setPic() {
        int targetW = parentActivity.getImageView().getWidth();
        int targetH = parentActivity.getImageView().getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0))
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        parentActivity.setImageView(bitmap);
    }

    private void sendToProcess() {
        parentActivity.setImgUriToSend(currentPhotoPath);
    }

}
