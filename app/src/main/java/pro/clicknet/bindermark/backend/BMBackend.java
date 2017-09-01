package pro.clicknet.bindermark.backend;

public class BMBackend {

    private boolean mNativeMethod;

    public BMBackend() {
        mNativeMethod = false;
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

}
