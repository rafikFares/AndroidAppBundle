package com.rafik.bundle.bundleproto;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.splitinstall.SplitInstallException;
import com.google.android.play.core.splitinstall.SplitInstallHelper;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.google.android.play.core.splitinstall.model.SplitInstallErrorCode.ACCESS_DENIED;
import static com.google.android.play.core.splitinstall.model.SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED;
import static com.google.android.play.core.splitinstall.model.SplitInstallErrorCode.API_NOT_AVAILABLE;
import static com.google.android.play.core.splitinstall.model.SplitInstallErrorCode.INCOMPATIBLE_WITH_EXISTING_SESSION;
import static com.google.android.play.core.splitinstall.model.SplitInstallErrorCode.INTERNAL_ERROR;
import static com.google.android.play.core.splitinstall.model.SplitInstallErrorCode.INVALID_REQUEST;
import static com.google.android.play.core.splitinstall.model.SplitInstallErrorCode.MODULE_UNAVAILABLE;
import static com.google.android.play.core.splitinstall.model.SplitInstallErrorCode.NETWORK_ERROR;
import static com.google.android.play.core.splitinstall.model.SplitInstallErrorCode.NO_ERROR;
import static com.google.android.play.core.splitinstall.model.SplitInstallErrorCode.SERVICE_DIED;
import static com.google.android.play.core.splitinstall.model.SplitInstallErrorCode.SESSION_NOT_FOUND;
import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.CANCELED;
import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.CANCELING;
import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.DOWNLOADED;
import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.DOWNLOADING;
import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.FAILED;
import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.INSTALLED;
import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.INSTALLING;
import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.PENDING;
import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String ALERT_FEATURE_MODULE_NAME = "dynamic_alert_feature";
    private static final String Activity_FEATURE_MODULE_NAME = "dynamic_acitivity_feature";

    private static final String CHANNEL_ID = "test.base.bundle.rafik.bundleapptest.662001";
    private static final String CHANNEL_NAME = "test.base.bundle.rafik.bundleapptest";
    private static final int NOTIFICATION_ID = 662001;

    private static final String FEATURE_MODULE_MAIN_LAUNCH_ACTIVITY = "test.base.bundle.rafik.dynamic_activity_feature.FeatureModuleApp";
    private static final String FEATURE_MODULE_MAIN_LAUNCH_SERVICE = "test.base.bundle.rafik.dynamic_alert_feature.FeatureModuleService";
    private static final int SERVICE_TYPE_MODULE = 0;
    private static final int ACTIVITY_TYPE_MODULE = 1;

    private static final String BUNDLE_IMAGE_EXTERNAL_RESOURCE = "bundle_image.jpg";
    private static final String BUNDLE_ALERT_ICON_EXTERNAL_RESOURCE = "alert_icon.png";

    private static Context mContext;
    private static AlertDialog mAlertDialog;

    @BindView(R.id.tv_info_alert)
    TextView mTvInfoAlert;
    @BindView(R.id.tv_ressouce)
    TextView mTvAlertRes;
    @BindView(R.id.bt_install_alert)
    Button mBtInstallAlert;
    @BindView(R.id.bt_enable_alert)
    Button mBtEnableAlert;
    @BindView(R.id.tv_info_feature)
    TextView mTvInfoFeature;
    @BindView(R.id.bt_install_feature)
    Button mBtInstallFeature;
    @BindView(R.id.bt_enable_feature)
    Button mBtEnableFeature;
    @BindView(R.id.bt_get_ressouce)
    Button mBtGetRes;
    @BindView(R.id.imageView)
    ImageView mImageView;

    private SplitInstallManager splitInstallManager;

    private static Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
    }

    @OnClick(R.id.bt_install_alert)
    public void installAlert(View view) {
        doInstall(ALERT_FEATURE_MODULE_NAME, mTvInfoAlert);
    }

    @OnClick(R.id.bt_enable_alert)
    public void enableAlert(View view) {
        enableAlertFeatureModule();
    }

    @OnClick(R.id.bt_install_feature)
    public void installActivityFeature(View view) {
        doInstall(Activity_FEATURE_MODULE_NAME, mTvInfoFeature);
    }

    @OnClick(R.id.bt_enable_feature)
    public void enableActivityFeature(View view) {
        runActivityFeatureModule();
    }

    @OnClick(R.id.bt_get_ressouce)
    public void getResourcesFromModule(View view) {
        new Handler().post(() -> {
            mImageView.setImageDrawable(
                    Drawable.createFromStream(getExternalRessources(BUNDLE_IMAGE_EXTERNAL_RESOURCE)
                            , null));
        });
    }

    @OnTextChanged(R.id.tv_info_alert)
    protected void onAlertTextChanged(CharSequence text) {
        if (text.toString().contains("INSTALLED")){
            mBtEnableAlert.setVisibility(View.VISIBLE);
            mBtGetRes.setVisibility(View.VISIBLE);
            mTvAlertRes.setVisibility(View.VISIBLE);
        }
    }

    @OnTextChanged(R.id.tv_info_feature)
    protected void onActivityFeatureTextChanged(CharSequence text) {
        if (text.toString().contains("INSTALLED")){
            mBtEnableFeature.setVisibility(View.VISIBLE);
            showInstallationDoneNotif();
        }
    }

    private void showInstallationDoneNotif() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder;
        Intent intent = null;

        try {
            intent = new Intent(this, Class.forName(FEATURE_MODULE_MAIN_LAUNCH_ACTIVITY));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Android Bundle Notification")
                .setContentText("Installation is done")
                .addAction(android.R.drawable.star_on, "Start", PendingIntent.getActivity(
                        getApplicationContext(),
                        1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void runActivityFeatureModule() {
        startModule(Activity_FEATURE_MODULE_NAME, ACTIVITY_TYPE_MODULE, true);
    }

    private void enableAlertFeatureModule() {
        startModule(ALERT_FEATURE_MODULE_NAME, SERVICE_TYPE_MODULE, true);
    }

    private void doInstall(String moduleName, TextView tvInfo) {
        splitInstallManager = SplitInstallManagerFactory.create(this);

        /*
            check if the module is already installed or not
             */
        if (splitInstallManager.getInstalledModules().contains(moduleName)) {
            /*
            if it's already installed launch the module
             */
            tvInfo.setText("Already INSTALLED");
        } else {

            SplitInstallRequest request = SplitInstallRequest
                    .newBuilder()
                    .addModule(moduleName)
                    // we can do multiple .addModule(ModuleName) at the same time
                    .build();


            splitInstallManager.startInstall(request)
                    .addOnSuccessListener(sessionId -> {
                        Log.d(TAG, "Succes for Session : >>>>>>> " + sessionId);
                    })
                    .addOnFailureListener(exception -> {
                        switch (((SplitInstallException) exception).getErrorCode()) {
                            case ACCESS_DENIED: {
                            /*
                            The app is unable to register the request because of insufficient permissions.
                            This typically occurs when the app is in the background.
                            Attempt the request when the app returns to the foreground.
                             */
                                Log.d(TAG, "addOnSuccessListener: >>>>>>> ACCESS_DENIED");
                            }
                            case ACTIVE_SESSIONS_LIMIT_EXCEEDED: {
                            /*
                            The request is rejected because there is at least one existing request
                            that is currently downloading.
                            Check if there are any requests that are still downloading,
                            as shown in the sample above.
                             */
                                Log.d(TAG, "Failure: >>>>>>> ACTIVE_SESSIONS_LIMIT_EXCEEDED");
                            }
                            case API_NOT_AVAILABLE: {
                            /*
                            The Play Core Library is not supported on the current device.
                            That is, the device is not able to download and install features on demand.
                            For devices running Android 4.4 (API level 20) or lower,
                            you should include dynamic feature modules at install time
                            using the dist:fusing manifest property. To learn more,
                            read about the Dynamic feature module manifest.
                             */
                                Log.d(TAG, "Failure: >>>>>>> API_NOT_AVAILABLE");
                            }
                            case INCOMPATIBLE_WITH_EXISTING_SESSION: {
                            /*
                            The request contains one or more modules that have already been
                            requested but have not yet been installed.
                            Either create a new request that does not include modules that
                            your app has already requested, or wait for all currently
                            requested modules to finish installing before retrying the request.
                            Keep in mind, requesting a module that has already been installed
                            does not resolve in an error.
                             */
                                Log.d(TAG, "Failure: >>>>>>> INCOMPATIBLE_WITH_EXISTING_SESSION");
                            }
                            case INTERNAL_ERROR: {
                                Log.d(TAG, "Failure: >>>>>>> INTERNAL_ERROR");
                            }
                            case INVALID_REQUEST: {
                            /*
                            Google Play received the request, but the request is not valid.
                            	Verify that the information included in the request is complete and accurate.
                             */
                                Log.d(TAG, "Failure: >>>>>>> INVALID_REQUEST");
                            }
                            case MODULE_UNAVAILABLE: {
                            /*
                            Google Play is unable to find the requested module based
                            on the current installed version of the app, device,
                             and user’s Google Play account.
                             If the user does not have access to the module, notify them.
                             */
                                Log.d(TAG, "Failure: >>>>>>> MODULE_UNAVAILABLE");
                            }
                            case NETWORK_ERROR: {
                            /*
                            The request failed because of a network error.
                            Prompt the user to either establish a network
                             connection or change to a different network.
                             */
                                Log.d(TAG, "Failure: >>>>>>> NETWORK_ERROR");
                            }
                            case NO_ERROR: {
                                Log.d(TAG, "Failure: >>>>>>> NO_ERROR");
                            }
                            case SERVICE_DIED: {
                            /*
                            The service responsible for handling the request has died.
                            Retry the request.
                            This error code is be exposed as an update to your
                            SplitInstallStateUpdatedListener with status FAILED and session ID -1.
                             */
                                Log.d(TAG, "Failure: >>>>>>> SERVICE_DIED");
                            }
                            case SESSION_NOT_FOUND: {
                            /*
                            A session for a given session ID was not found.
                            If you’re trying to monitor the state of a request
                            by its session ID, make sure that the session ID is correct.
                             */
                                Log.d(TAG, "Failure: >>>>>>> SESSION_NOT_FOUND");
                            }
                        }
                        Log.w(TAG, " Failure message >>>>>>> " + exception);
                    })
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            Log.d(TAG, " Complete >>>>>>> Success");
                        else
                            Log.d(TAG, " Complete >>>>>>> Fail");

                    });

            SplitInstallStateUpdatedListener stateListener = state -> {
                switch (state.status()) {
                    case DOWNLOADING: {
                        Log.d(TAG, "UpdateStated: >>>>>>> Download is in progress");
                        tvInfo.setText("DOWNLOADING");

                        // to update ui or progress bar
                        long totalBytes = state.totalBytesToDownload();
                        long progress = state.bytesDownloaded();
                    }
                    case INSTALLING: {
                        Log.d(TAG, "UpdateStated: >>>>>>> The device is currently installing the module.");
                        tvInfo.setText("INSTALLING");
                    }
                    case INSTALLED: {
                        Log.d(TAG, "UpdateStated: >>>>>>> The module is installed on the device.");
                        tvInfo.setText("INSTALLED");

                        //update the current context to use the new installed resources
                        if (Build.VERSION.SDK_INT > 25 && Build.VERSION.SDK_INT < 28) {
                            SplitInstallHelper.updateAppInfo(this);
                        } else {
                            try {
                                mContext.createPackageContext(this.getPackageName(), 0);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    case PENDING: {
                    /*
                    The request has been accepted and the download should start soon.
                    Initialize UI components, such as a progress bar, to provide the
                    user feedback on the download.
                     */
                    }
                    case REQUIRES_USER_CONFIRMATION: {
                    /*
                    The download requires user confirmation. This is most likely due to
                    the size of the download being larger than 10 MB.
                    Prompt the user to accept the download request. To learn more,
                    go to the section about how to obtain user confirmation.
                     */
                    }
                    case DOWNLOADED: {
                    /*
                     	The device has downloaded the module but installation has no yet begun.
                     	Apps should enable SplitCompat to have immediate access
                     	to downloaded modules and avoid seeing this state.
                     	Otherwise, the download transitions to INSTALLED,
                     	and your app access to its code and resources,
                     	only at some point after the app enters the background.
                     */
                    }
                    case FAILED: {
                    /*
                    The request failed before the module was installed on the device.
                    Prompt the user to either retry the request or cancel it.
                     */
                    }
                    case CANCELING: {
                    /*
                    The device is in the process of cancelling the request.
                    To learn more, go to the section about how to cancel an install request.
                     */
                    }
                    case CANCELED: {
                    /*
                     	The request has been cancelled.
                     */
                    }
                }
            };

            splitInstallManager.registerListener(stateListener);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            stopService(new Intent(this, Class.forName(FEATURE_MODULE_MAIN_LAUNCH_SERVICE)));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void startModule(String moduleName, int type, boolean launch) {
        if (launch) {
            /*
            launching the module's activity or the module's service to do stuff
             */
            Toast.makeText(this, "launching " + moduleName, Toast.LENGTH_LONG).show();
            try {
                switch (type){
                    case SERVICE_TYPE_MODULE:{
                        startService(new Intent(this, Class.forName(FEATURE_MODULE_MAIN_LAUNCH_SERVICE)));
                        break;
                    }
                    case ACTIVITY_TYPE_MODULE:{
                        startActivity(new Intent(this, Class.forName(FEATURE_MODULE_MAIN_LAUNCH_ACTIVITY)));
                        break;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static InputStream getExternalRessources(String resName){
        /*
        getting data sources from the module need to be called on a new thread
         */
        InputStream tmp = null;
        // Loads contents from the app module using AssetManager
        AssetManager assetManager = mContext.getAssets();
        try {
            tmp =  assetManager.open(resName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmp;
    }

    public static void showAlert(Location where) {
        mHandler.post(() -> {
            if (mAlertDialog == null) {
                Drawable alertIcon = Drawable.createFromStream(
                        getExternalRessources(BUNDLE_ALERT_ICON_EXTERNAL_RESOURCE)
                        , null);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                mAlertDialog = builder.setTitle("Alert Box")
                        .setMessage("your location is : " + where.toString())
                        .setCancelable(false)
                        .setIcon(alertIcon)
                        .show();
            }else{
                mAlertDialog.setMessage("your location is : " + where.toString());
            }
        });
    }

    public static void dismissAlert(){
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
