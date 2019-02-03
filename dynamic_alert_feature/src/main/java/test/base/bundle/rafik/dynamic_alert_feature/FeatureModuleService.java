package test.base.bundle.rafik.dynamic_alert_feature;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.LocationRequest;
import com.patloew.rxlocation.RxLocation;
import com.rafik.bundle.bundleproto.MainActivity;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class FeatureModuleService extends Service {

    private static final String TAG = FeatureModuleService.class.getSimpleName();

    private static final String CHANNEL_ID = "test.base.bundle.rafik.dynamic_alert_feature.662008";
    private static final String CHANNEL_NAME = "test.base.bundle.rafik.dynamic_alert_feature";
    private static final int NOTIFICATION_ID = 662008;

    private Disposable mCollectorDisposable;

    @SuppressLint("MissingPermission")
    public static Observable<Location> observeUserLocation(Context context, int interval) {
        return new RxLocation(context).location()
                .updates(LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(interval)
                        .setFastestInterval(interval));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        checkPosition();
    }

    @Override
    public void onDestroy() {
        hideNotification();
        mCollectorDisposable.dispose();
        mCollectorDisposable = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;  //No binder
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        showNotification();
        return START_STICKY;
    }

    public void showNotification() {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("Service Notification")
                        .setContentText("is running ...")
                        .setPriority(Notification.PRIORITY_HIGH);

        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void hideNotification() {
        stopForeground(true);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkPosition() {

        mCollectorDisposable = observeUserLocation(this.getApplicationContext(), 1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(
                        location -> {
                            MainActivity.showAlert(location);

                            //MainActivity.dismissAlert();

                        },
                        Throwable::getMessage);
    }

}
