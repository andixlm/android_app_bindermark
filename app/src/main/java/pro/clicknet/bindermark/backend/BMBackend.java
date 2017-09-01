package pro.clicknet.bindermark.backend;

import pro.clicknet.bindermarkcommon.BMResponse;

public class BMBackend {

    private static final int MINIMUM_SIZE = 1;
    private static final int MAXIMUM_SIZE = 512;

    // Size must be set to positive value before performing test.
    private static final int DEFAULT_SIZE = -1;
    private static final boolean DEFAULT_NATIVE_METHOD = false;

    private int mSize;
    private boolean mNativeMethod;

    private OnCompleteListener mOnCompleteListener;

    public BMBackend() {
        setSize(DEFAULT_SIZE);
        setNativeMethod(DEFAULT_NATIVE_METHOD);
    }

    public BMBackend(int size, boolean nativeMethod) {
        setSize(size);
        setNativeMethod(nativeMethod);
    }

    public void perform() throws IllegalStateException {
        if (mSize < MINIMUM_SIZE || mSize > MAXIMUM_SIZE) {
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
