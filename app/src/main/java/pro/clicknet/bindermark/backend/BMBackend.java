package pro.clicknet.bindermark.backend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import pro.clicknet.bindermark.BinderMark;
import pro.clicknet.bindermark.backend.virtual.BMClientService;
import pro.clicknet.bindermark.backend.virtual.BMServerService;
import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBMClientService;
import pro.clicknet.bindermarkcommon.IBMServerService;

public class BMBackend {

    private int mSize;
    private boolean mNativeMethod;

    private Context mContext;

    private Native mNativeBackend;
    private Virtual mVirtualBackend;

    private OnCreateListener mOnCreateListener;
    private OnCompleteListener mOnCompleteListener;

    public BMBackend(Context context) {
        mContext = context;
        mSize = BinderMark.DEFAULT_SIZE;
        mNativeMethod = BinderMark.DEFAULT_NATIVE_METHOD;

        mNativeBackend = new Native();
        mVirtualBackend = new Virtual(context);
    }

    public BMBackend(Context context, int size, boolean nativeMethod) {
        this(context);

        mSize = size;
        mNativeMethod = nativeMethod;
    }

    public void create() {
        try {
            if (mNativeMethod) {
                mNativeBackend.create();
            } else {
                mVirtualBackend.create();
            }
        } catch (InstantiationException exc) {
            Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (mOnCreateListener != null) {
            mOnCreateListener.onCreate();
        }
    }

    public void destroy() {
        if (mNativeMethod) {
            mNativeBackend.destroy();
        } else {
            mVirtualBackend.destroy();
        }
    }

    public void perform() throws IllegalStateException {
        if (mSize < BinderMark.MINIMUM_SIZE || mSize > BinderMark.MAXIMUM_SIZE) {
            throw new IllegalStateException("Size is out of allowed bounds");
        }

        BMResponse response = mNativeMethod ? mNativeBackend.perform() : mVirtualBackend.perform();
        if (mOnCompleteListener != null) {
            mOnCompleteListener.onComplete(response);
        }
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

    private class Native {

        public void create() {

        }

        public void destroy() {

        }

        public BMResponse perform() {
            return null;
        }

    }

    private class Virtual {

        private Context mContext;

        private IBMServerService mServerService;
        private IBMClientService mClientService;

        public Virtual(Context context) {
            mContext = context;

            mServerService = null;
            mClientService = null;
        }

        public void create() throws InstantiationException {
            Intent serverIntent = new Intent(mContext, BMServerService.class);
            if (!mContext.bindService(serverIntent, mServerServiceConnection,
                    Context.BIND_AUTO_CREATE)) {
                throw new InstantiationException("Can't create server");
            }

            Intent clientIntent = new Intent(mContext, BMClientService.class);
            if (!mContext.bindService(clientIntent, mClientServiceConnection,
                    Context.BIND_AUTO_CREATE)) {
                throw new InstantiationException("Can't create client");
            }
        }

        public void destroy() {
            if (mClientService != null) {
                mContext.unbindService(mClientServiceConnection);
                mClientService = null;
            }

            if (mServerService != null) {
                mContext.unbindService(mServerServiceConnection);
                mServerService = null;
            }
        }

        public BMResponse perform() {
            BMResponse response;

            try {
                response = mClientService.perform(mSize);
            } catch (RemoteException exc) {
                response = null;
                Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }

            return response;
        }

        private ServiceConnection mServerServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                mServerService = IBMServerService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mServerService = null;
            }

        };

        private ServiceConnection mClientServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                mClientService = IBMClientService.Stub.asInterface(service);

                try {
                    // Server must be created in advance.
                    mClientService.setServer(mServerService);
                } catch (RemoteException exc) {
                    Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mClientService = null;
            }

        };

    }

    public interface OnCreateListener {

        void onCreate();

    }

    public interface OnCompleteListener {

        void onComplete(BMResponse response);

    }

}
