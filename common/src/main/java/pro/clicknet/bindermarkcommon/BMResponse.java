package pro.clicknet.bindermarkcommon;

import android.os.Parcel;
import android.os.Parcelable;

public class BMResponse implements Parcelable {

    private long mReceiptTime;

    public BMResponse(long receiptTime) {
        mReceiptTime = receiptTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mReceiptTime);
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

    public long getReceiptTime() {
        return mReceiptTime;
    }

    public void setReceiptTime(long receiptTime) {
        mReceiptTime = receiptTime;
    }

}
