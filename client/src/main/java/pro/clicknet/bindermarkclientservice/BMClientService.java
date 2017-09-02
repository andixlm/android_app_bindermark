package pro.clicknet.bindermarkclientservice;

import android.os.RemoteException;

import pro.clicknet.bindermarkcommon.BMRequest;
import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBMClientService;
import pro.clicknet.bindermarkserverservice.BMServerService;

public class BMClientService extends IBMClientService.Stub {

    private BMServerService mServer;

    public BMClientService(BMServerService server) throws IllegalArgumentException {
        if (server == null) {
            throw new IllegalArgumentException("Server must be non-null");
        }

        mServer = server;
    }

    @Override
    public BMResponse perform(int size) throws RemoteException {
        BMRequest request = new BMRequest(size);

        long startTime = System.currentTimeMillis();
        BMResponse response = mServer.get(request);
        long endTime = System.currentTimeMillis();

        return response;
    }

}
