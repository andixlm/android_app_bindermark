package pro.clicknet.bindermarkclientservice;

import android.os.RemoteException;

import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBinderMarkClientService;
import pro.clicknet.bindermarkserverservice.BMServerService;

public class BMClientService extends IBinderMarkClientService.Stub {

    private BMServerService mServer;

    public BMClientService(BMServerService server) throws IllegalArgumentException {
        if (server == null) {
            throw new IllegalArgumentException("Server must be non-null");
        }

        mServer = server;
    }

    @Override
    public BMResponse perform(int size) throws RemoteException {
        long startTime = System.currentTimeMillis();
        // TODO: Get response from server.
        BMResponse response = null;
        long endTime = System.currentTimeMillis();

        return response;
    }

}
