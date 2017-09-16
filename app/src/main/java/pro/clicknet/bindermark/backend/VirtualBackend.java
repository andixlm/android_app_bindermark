package pro.clicknet.bindermark.backend;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import pro.clicknet.bindermark.BinderMark;
import pro.clicknet.bindermark.services.BMClientService;
import pro.clicknet.bindermark.services.BMServerService;
import pro.clicknet.bindermarkcommon.IBMClientService;
import pro.clicknet.bindermarkcommon.IBMServerService;

class VirtualBackend {

    private Context mContext;

    private int mSize;
    private int mTransactionsAmount;

    private long mResult;
    private long mDeviation;
    private static long[] mResults;

    private final BMResult mBMResult;

    private IBMServerService mServerService;
    private IBMClientService mClientService;

    static {
        mResults = new long[BinderMark.MAXIMUM_TRANSACTIONS_AMOUNT];
    }

    public VirtualBackend(Context context) {
        mContext = context;
        mSize = BinderMark.DEFAULT_SIZE;
        mTransactionsAmount = BinderMark.DEFAULT_TRANSACTIONS_AMOUNT;

        mResult = 0;
        mDeviation = 0;

        mBMResult = new BMResult();

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

    public BMResult perform() {
        long totalResult = 0;
        int realTransactionsAmount = mTransactionsAmount;

        try {
            for (int idx = 0; idx < mTransactionsAmount; ++idx) {
                mResults[idx] = mClientService.perform().getReceiptTime();
                totalResult += mResults[idx];
            }

            long initialAverage = totalResult / mTransactionsAmount;
            for (int idx = 0; idx < mTransactionsAmount; ++idx) {
                // TODO: Use statistics rule to determine fault.
                if ((double) mResults[idx] / (double) initialAverage > 10.0) {
                    totalResult -= mResults[idx];
                    mResults[idx] = -1;

                    --realTransactionsAmount;
                }
            }

            long realAverage = totalResult / realTransactionsAmount;

            double deviationSum = 0.0;
            for (int idx = 0; idx < mTransactionsAmount; ++idx) {
                if (mResults[idx] != -1)
                    deviationSum += Math.pow(mResults[idx] - realAverage, 2.0);
            }

            mResult = realAverage;
            mDeviation = Math.round(Math.sqrt(deviationSum / (double) realTransactionsAmount));
        } catch (RemoteException exc) {
            mResult = mDeviation = 0;
            Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

        mBMResult.setResult(mResult);
        mBMResult.setDeviation(mDeviation);
        mBMResult.setFaultsAmount(mTransactionsAmount - realTransactionsAmount);

        return mBMResult;
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
