package pro.clicknet.bindermark.backend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import pro.clicknet.bindermark.BinderMark;
import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBMClientService;
import pro.clicknet.bindermarkcommon.IBMServerService;

public class BMBackend {

    private int mSize;
    private boolean mNativeMethod;

    private Context mContext;

    private IBMServerService mServerService;
    private IBMClientService mClientService;

    private OnCreateListener mOnCreateListener;
    private OnCompleteListener mOnCompleteListener;

    public BMBackend(Context context) {
        mContext = context;
        mSize = BinderMark.DEFAULT_SIZE;
        mNativeMethod = BinderMark.DEFAULT_NATIVE_METHOD;
    }

    public BMBackend(Context context, int size, boolean nativeMethod) {
        mContext = context;
        mSize = size;
        mNativeMethod = nativeMethod;
    }

    public void perform() throws IllegalStateException {
        if (mSize < BinderMark.MINIMUM_SIZE || mSize > BinderMark.MAXIMUM_SIZE) {
            throw new IllegalStateException("Size is out of allowed bounds");
        }

        BMResponse response = mNativeMethod ? performNative() : performVirtual();
        if (mOnCompleteListener != null) {
            mOnCompleteListener.onComplete(response);
        }
    }

    private BMResponse performVirtual() {
        BMResponse response;

        try {
            mClientService.setServer(mServerService);
            response = mClientService.perform(mSize);
        } catch (RemoteException exc) {
            response = null;
            Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return response;
    }

    public void createServices() throws InstantiationException {
        if (!mContext.bindService(new Intent(BMServerService.class.getName()),
                mServerServiceConnection, Context.BIND_AUTO_CREATE)) {
            throw new InstantiationException("Can't create server");
        }

        if (!mContext.bindService(new Intent(BMClientService.class.getName()),
                mClientServiceConnection, Context.BIND_AUTO_CREATE)) {
            throw new InstantiationException("Can't create client");
        }

        if (mOnCreateListener != null) {
            mOnCreateListener.onCreate();
        }
    }

    public void destroyServices() {
        if (mClientService != null) {
            mContext.unbindService(mClientServiceConnection);
        }

        if (mServerService != null) {
            mContext.unbindService(mServerServiceConnection);
        }
    }

    private BMResponse performNative() {
        // TODO: Implement as native method.
        return null;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) throws IllegalArgumentException {
        if (context == null) {
            throw new IllegalArgumentException("Context must be non-null");
        }

        mContext = context;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public boolean getNativeMethod() {
        return mNativeMethod;
    }

    public void setNativeMethod(boolean nativeMethod) {
        mNativeMethod = nativeMethod;
    }

    public OnCreateListener getOnCreateListener() {
        return mOnCreateListener;
    }

    public void setOnCreateListener(OnCreateListener listener) {
        mOnCreateListener = listener;
    }

    public OnCompleteListener getOnCompleteListener() {
        return mOnCompleteListener;
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        mOnCompleteListener = listener;
    }

    private ServiceConnection mServerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServerService = IBMServerService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServerService = null;
        }

    };

    private ServiceConnection mClientServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mClientService = IBMClientService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mClientService = null;
        }

    };

    public interface OnCreateListener {

        void onCreate();

    }

    public interface OnCompleteListener {

        void onComplete(BMResponse response);

    }

}
