package com.twtchnz.OneClickPhoto;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GoogleServicesChecker {

    FragmentActivity parentActivity;

    public GoogleServicesChecker(FragmentActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    private final static int CONNECTION_FAILUER_RESOLUTION_REQUEST = 9000;


    public boolean isServicesAvailable() {

        boolean result;

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(parentActivity);

        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(OneClickPhotoUtils.APPTAG, parentActivity.getString(R.string.services_available));
            result = true;
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, parentActivity, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(parentActivity.getSupportFragmentManager(), OneClickPhotoUtils.APPTAG);
            }
            result = false;
        }

        return result;
    }
}
