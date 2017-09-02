package pro.clicknet.bindermarkserverservice;

import android.os.RemoteException;

import pro.clicknet.bindermarkcommon.BMRequest;
import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBMServerService;

public class IBMServerServiceImpl extends IBMServerService.Stub {

    @Override
    public BMResponse get(BMRequest request) throws RemoteException {
        return new BMResponse(System.currentTimeMillis());
    }

}
