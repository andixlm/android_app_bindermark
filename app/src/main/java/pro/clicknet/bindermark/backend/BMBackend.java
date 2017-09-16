package pro.clicknet.bindermark.backend;

import android.content.Context;
import android.widget.Toast;

import pro.clicknet.bindermark.BinderMark;

public class BMBackend {

    private Context mContext;

    private int mSize;
    private int mTransactionsAmount;
    private boolean mNativeMethod;

    private NativeBackend mNativeBackend;
    private VirtualBackend mVirtualBackend;

    private OnCreateListener mOnCreateListener;
    private OnCompleteListener mOnCompleteListener;

    public BMBackend(Context context) {
        mContext = context;
        mSize = BinderMark.DEFAULT_SIZE;
        mTransactionsAmount = BinderMark.DEFAULT_TRANSACTIONS_AMOUNT;
        mNativeMethod = BinderMark.DEFAULT_NATIVE_METHOD;

        mNativeBackend = new NativeBackend();
        mVirtualBackend = new VirtualBackend(context);
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
                mVirtualBackend.setSize(mSize);
                mVirtualBackend.setTransactionsAmount(mTransactionsAmount);

                mVirtualBackend.create();
            }
        } catch (InstantiationException exc) {
            Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (mOnCreateListener != null) {
            mOnCreateListener.onCreate();
        }
    }

    public void perform() throws IllegalStateException {
        BMResult result = mNativeMethod ? mNativeBackend.perform() : mVirtualBackend.perform();

        if (mOnCompleteListener != null) {
            mOnCompleteListener.onComplete(result);
        }
    }

    public void destroy() {
        if (mNativeMethod) {
            mNativeBackend.destroy();
        } else {
            mVirtualBackend.destroy();
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

    public interface OnCreateListener {

        void onCreate();

    }

    public interface OnCompleteListener {

        void onComplete(BMResult result);

    }

}
