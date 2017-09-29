package pro.clicknet.bindermark.backend;

public class BMResult {

    private long mResult;
    private long mDeviation;
    private int mFaultsAmount;

    public BMResult() {
        mResult = 0;
        mDeviation = 0;
        mFaultsAmount = 0;
    }

    public BMResult(long result, long deviation, int faultsAmount) {
        mResult = result;
        mDeviation = deviation;
        mFaultsAmount = faultsAmount;
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

    public int getFaultsAmount() {
        return mFaultsAmount;
    }

    public void setFaultsAmount(int faultsAmount) {
        mFaultsAmount = faultsAmount;
    }

}
