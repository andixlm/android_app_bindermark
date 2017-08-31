package pro.clicknet.bindermarkcommon;

import pro.clicknet.bindermarkcommon.BMRequest;
import pro.clicknet.bindermarkcommon.BMResponse;

interface IBinderMarkServerService {
    BMResponse get(in BMRequest request);
}
