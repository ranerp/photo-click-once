package com.twtchnz.OneClickPhoto;

import android.os.Environment;

import java.io.File;

public class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                albumName
                );
    }
}
