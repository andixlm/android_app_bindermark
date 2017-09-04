package pro.clicknet.bindermarkcommon;

import android.os.Parcel;
import android.os.Parcelable;

public class BMRequest implements Parcelable {

    private String mData;

    public BMRequest(String data) {
        mData = data;
    }

    public BMRequest(int size) {
        mData = generateString(size);
    }

    /*
     * Creates a String of @size in KB.
     */
    public static String generateString(int size) {
        // Java char is 2 bytes. Length equals to (size / 2 * 1024).
        int length = size * 512;

        return String.format("%0" + length + "d", 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel in, int flags) {
        in.writeString(mData);
    }

    public static final Creator<BMRequest> CREATOR =
            new Creator<BMRequest>() {

                @Override
                public BMRequest createFromParcel(Parcel in) {
                    return new BMRequest(in.readString());
                }

                @Override
                public BMRequest[] newArray(int size) {
                    return new BMRequest[size];
                }

            };

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
    }

}
