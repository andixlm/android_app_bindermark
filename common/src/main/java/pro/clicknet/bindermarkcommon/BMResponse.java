package pro.clicknet.bindermarkcommon;

import android.os.Parcel;
import android.os.Parcelable;

public class BMResponse implements Parcelable {

    private long mTime;

    public BMResponse(long time) {
        mTime = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mTime);
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public static final Creator<BMResponse> CREATOR =
            new Creator<BMResponse>() {

                @Override
                public BMResponse createFromParcel(Parcel in) {
                    return new BMResponse(in.readLong());
                }

                @Override
                public BMResponse[] newArray(int size) {
                    return new BMResponse[size];
                }

            };

}
