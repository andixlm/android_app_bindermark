package pro.clicknet.bindermark.backend;

public class BMResult {

    private long mResult;
    private long mDeviation;

    public BMResult() {

    }

    public BMResult(long result, long deviation) {
        mResult = result;
        mDeviation = deviation;
    }

    public long getResult() {
        return mResult;
    }

    public void setResult(long result) {
        mResult = result;
    }

    public long getDeviation() {
        return mDeviation;
    }

    public void setDeviation(long deviation) {
        mDeviation = deviation;
    }

}
