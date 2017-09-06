package pro.clicknet.bindermark.backend.virtual;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import pro.clicknet.bindermarkclientservice.IBMClientServiceImpl;

public class BMClientService extends Service {

    private IBMClientServiceImpl mService;

    @Override
    public void onCreate() {
        super.onCreate();

        mService = new IBMClientServiceImpl();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mService = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mService;
    }

}
