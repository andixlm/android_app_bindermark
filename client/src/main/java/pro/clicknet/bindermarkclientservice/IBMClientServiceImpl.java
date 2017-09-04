package pro.clicknet.bindermarkclientservice;

import android.os.RemoteException;

import pro.clicknet.bindermarkcommon.BMRequest;
import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBMClientService;
import pro.clicknet.bindermarkcommon.IBMServerService;

public class IBMClientServiceImpl extends IBMClientService.Stub {

    private IBMServerService mServer;

    public IBMClientServiceImpl() {
        mServer = null;
    }

    @Override
    public BMResponse perform(int size) throws RemoteException {
        if (mServer == null) {
            throw new RemoteException("Server is not set");
        }

        BMRequest request = new BMRequest(size);

        long startTime = System.currentTimeMillis();
        BMResponse response = mServer.get(request);
        long endTime = System.currentTimeMillis();

        return response;
    }

    public IBMServerService getServer() {
        return mServer;
    }

    @Override
    public void setServer(IBMServerService server) throws IllegalArgumentException {
        if (server == null) {
            throw new IllegalArgumentException("Server must be non-null");
        }

        mServer = server;
    }

}
