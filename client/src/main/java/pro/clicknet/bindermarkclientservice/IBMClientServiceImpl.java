package pro.clicknet.bindermarkclientservice;

import android.os.RemoteException;

import pro.clicknet.bindermarkcommon.BMRequest;
import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBMClientService;
import pro.clicknet.bindermarkcommon.IBMServerService;

public class IBMClientServiceImpl extends IBMClientService.Stub {

    private int mSize;
    private BMRequest mRequest;

    private IBMServerService mServer;

    public IBMClientServiceImpl() {
        mServer = null;
    }

    @Override
    public void setup(int size, IBMServerService server) {
        mSize = size;
        mServer = server;

        mRequest = new BMRequest(mSize);
    }

    @Override
    public BMResponse perform() throws RemoteException {
        if (mServer == null) {
            throw new RemoteException("Server is not set");
        }

        long startTime = System.nanoTime();
        BMResponse response = mServer.get(mRequest);

        response.setReceiptTime(response.getReceiptTime() - startTime);

        return response;
    }

}
