package pro.clicknet.bindermark.backend;

import android.content.Context;

import pro.clicknet.bindermark.BinderMark;
import pro.clicknet.bindermarkcommon.BMResponse;

public class BMBackend {

    private int mSize;
    private boolean mNativeMethod;

    private Context mContext;

    private OnCompleteListener mOnCompleteListener;

    public BMBackend(Context context) {
        setContext(context);
        setSize(BinderMark.DEFAULT_SIZE);
        setNativeMethod(BinderMark.DEFAULT_NATIVE_METHOD);
    }

    public BMBackend(Context context, int size, boolean nativeMethod) {
        this(context);

        setSize(size);
        setNativeMethod(nativeMethod);
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
        return null;
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

    public OnCompleteListener getOnCompleteListener() {
        return mOnCompleteListener;
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        mOnCompleteListener = listener;
    }

    public interface OnCompleteListener {

        void onComplete(BMResponse response);

    }

}
