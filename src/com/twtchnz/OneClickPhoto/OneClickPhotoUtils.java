package com.twtchnz.OneClickPhoto;

public final class OneClickPhotoUtils {

    public static final String APPTAG = "OneClickPhoto";

    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    public static final int FAST_CEILING_IN_SECONDS = 1;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

    public static final String KEY_UPDATES_REQUESTED =
            "com.twtchnz.OneClickPhoto.KEY_UPDATES_REQUESTED";

    public static final String SHARED_PREFERENCES =
            "com.twtchnz.OneClickPhoto.SHARED_PREFERENCES";

    public static final int ACTION_TAKE_PHOTO_BIG = 1;
    public static final int ACTION_TAKE_PHOTO_SMALL = 2;
    public static final int ACTION_TAKE_VIDEO = 3;

    public static final String JPEG_FILE_PREFIX = "IMG_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";

    public static final String WEB_SOCKET_URL = "ws://twtchnz-project-cream.herokuapp.com/image/receive";

    public static final String ALBUM_NAME = "OneClickPhoto";
}
