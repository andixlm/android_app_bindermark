package pro.clicknet.bindermarkclientservice;

import android.os.RemoteException;

import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBinderMarkClientService;

public class BMClientService extends IBinderMarkClientService.Stub {

    // TODO: Store server.

    @Override
    public BMResponse perform(int size) throws RemoteException {
        long startTime = System.currentTimeMillis();
        // TODO: Get response from server.
        BMResponse response = null;
        long endTime = System.currentTimeMillis();

        return response;
    }

}
