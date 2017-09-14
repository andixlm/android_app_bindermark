package pro.clicknet.bindermark.backend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import pro.clicknet.bindermark.BinderMark;
import pro.clicknet.bindermark.backend.virtual.BMClientService;
import pro.clicknet.bindermark.backend.virtual.BMServerService;
import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBMClientService;
import pro.clicknet.bindermarkcommon.IBMServerService;

public class BMBackend {

    private int mSize;
    private int mTransactionsAmount;
    private boolean mNativeMethod;

    private long mResult;
    private long mDeviation;
    private long[] mResults;

    private Context mContext;

    private Native mNativeBackend;
    private Virtual mVirtualBackend;

    private OnCreateListener mOnCreateListener;
    private OnCompleteListener mOnCompleteListener;

    public BMBackend(Context context) {
        mContext = context;
        mSize = BinderMark.DEFAULT_SIZE;
        mTransactionsAmount = BinderMark.DEFAULT_TRANSACTIONS_AMOUNT;
        mNativeMethod = BinderMark.DEFAULT_NATIVE_METHOD;

        mResult = 0;
        mDeviation = 0;
        mResults = new long[BinderMark.MAXIMUM_TRANSACTIONS_AMOUNT];

        mNativeBackend = new Native();
        mVirtualBackend = new Virtual(context);
    }

    public BMBackend(Context context, int size, int transactionAmount, boolean nativeMethod) {
        this(context);

        if (size < BinderMark.MINIMUM_SIZE || size > BinderMark.MAXIMUM_SIZE) {
            throw new IllegalStateException("Size is out of allowed bounds");
        }

        if (transactionAmount < BinderMark.MINIMUM_TRANSACTIONS_AMOUNT ||
                transactionAmount > BinderMark.MAXIMUM_TRANSACTIONS_AMOUNT) {
            throw new IllegalStateException("Transactions amount is out of allowed bounds");
        }

        mSize = size;
        mTransactionsAmount = transactionAmount;
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
        BMBackend.Result result = mNativeMethod ? mNativeBackend.perform() : mVirtualBackend.perform();

        if (mOnCompleteListener != null) {
            mOnCompleteListener.onComplete(result);
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

    public int getTransactionsAmount() {
        return mTransactionsAmount;
    }

    public void setTransactionsAmount(int transactionsAmount) {
        mTransactionsAmount = transactionsAmount;
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

    public class Result {

        private long mResult;
        private long mDeviation;

        public Result() {

        }

        public Result(long result, long deviation) {
            mResult = result;
            mDeviation = deviation;
        }

        public long getResult() {
            return mResult;
        }

        public void setResult(long result) {
            mResult = result;
        }

        public long getDeviation() {
            return mDeviation;
        }

        public void setDeviation(long deviation) {
            mDeviation = deviation;
        }

    }

    private class Native {

        public void create() {

        }

        public void destroy() {

        }

        public BMBackend.Result perform() {
            return null;
        }

    }

    private class Virtual {

        private final BMBackend.Result mBackendResult;

        private Context mContext;

        private IBMServerService mServerService;
        private IBMClientService mClientService;

        public Virtual(Context context) {
            mBackendResult = new BMBackend.Result();

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

        public BMBackend.Result perform() {
            mResult = 0;

            try {
                for (int idx = 0; idx < mTransactionsAmount; ++idx) {
                    mResults[idx] = mClientService.perform().getReceiptTime();
                    mResult += mResults[idx];
                }

                mResult /= mTransactionsAmount;

                double deviationSum = 0.0;
                for (int idx = 0; idx < mTransactionsAmount; ++idx) {
                    deviationSum += Math.pow(mResults[idx] - mResult, 2.0);
                }

                mDeviation = Math.round(Math.sqrt(deviationSum / (double) mTransactionsAmount));
            } catch (RemoteException exc) {
                mResult = mDeviation = 0;
                Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }

            mBackendResult.setResult(mResult);
            mBackendResult.setDeviation(mDeviation);

            return mBackendResult;
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
                    mClientService.setup(mSize, mServerService);
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

        void onComplete(BMBackend.Result result);

    }

}
