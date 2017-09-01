package pro.clicknet.bindermark.backend;

import pro.clicknet.bindermarkcommon.BMResponse;

public class BMBackend {

    private static final boolean DEFAULT_NATIVE_METHOD = false;

    private boolean mNativeMethod;

    private OnCompleteListener mOnCompleteListener;

    public BMBackend() {
        mNativeMethod = DEFAULT_NATIVE_METHOD;
    }

    public BMBackend(boolean nativeMethod) {
        setNativeMethod(nativeMethod);
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
