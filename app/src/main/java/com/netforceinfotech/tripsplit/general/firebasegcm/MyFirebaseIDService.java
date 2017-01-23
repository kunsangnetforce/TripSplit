package com.netforceinfotech.tripsplit.general.firebasegcm;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;


public class MyFirebaseIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    UserSessionManager userSessionManager;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        userSessionManager = new UserSessionManager(getApplicationContext());
        Log.i(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        //services.php?opt=updateregid&user_id=11&reg_id=11
        String url = getResources().getString(R.string.url);
        String pushurl = "services.php?opt=updateregid&user_id=" + userSessionManager.getUserId() + "&reg_id=" + token;
        userSessionManager.setRegId(token);
        Log.i(TAG,token);
        if (!userSessionManager.getIsLogedIn()) {
            return;
        }
        Ion.with(getApplicationContext())
                .load(url + pushurl)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        if (result == null) {
                            Log.i(TAG, "not sending");
                        } else {

                            String status = result.get("status").getAsString().toLowerCase();
                            if (status.equalsIgnoreCase("success")) {
                                Log.i(TAG, "successfully registered");

                            } else {
                                Log.i(TAG, "successfully registered");
                            }
                        }

                    }
                });
    }

    private void showMessage(String s) {
        Toast.makeText(MyFirebaseIDService.this, s, Toast.LENGTH_SHORT).show();
    }
}