package pro.clicknet.bindermarkcommon;

import android.os.Parcel;
import android.os.Parcelable;

public class BMRequest implements Parcelable {

    private byte[] mData;

    public BMRequest(byte[] data) {
        mData = data;
    }

    public BMRequest(int size) {
        mData = generateByteArray(size);
    }

    /*
     * Creates byte array of @size in KB.
     */
    public static byte[] generateByteArray(int size) {
        return new byte[1024 * size];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel in, int flags) {
        in.writeByteArray(mData);
    }

    public byte[] getData() {
        return mData;
    }

    public void setData(byte[] data) {
        mData = data;
    }

    public static final Creator<BMRequest> CREATOR =
            new Creator<BMRequest>() {

                @Override
                public BMRequest createFromParcel(Parcel in) {
                    return new BMRequest(in.createByteArray());
                }

                @Override
                public BMRequest[] newArray(int size) {
                    return new BMRequest[size];
                }

            };

}
