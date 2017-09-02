package pro.clicknet.bindermark.backend;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import pro.clicknet.bindermarkserverservice.IBMServerServiceImpl;

public class BMServerService extends Service {

    private IBMServerServiceImpl mService;

    @Override
    public void onCreate() {
        super.onCreate();

        mService = new IBMServerServiceImpl();
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
