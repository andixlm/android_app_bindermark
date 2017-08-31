package pro.clicknet.bindermarkserverservice;

import android.os.RemoteException;

import pro.clicknet.bindermarkcommon.BMRequest;
import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBinderMarkServerService;

public class BMServerService extends IBinderMarkServerService.Stub {

    @Override
    public BMResponse get(BMRequest request) throws RemoteException {
        return new BMResponse(System.currentTimeMillis());
    }

}
