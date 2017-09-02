package pro.clicknet.bindermarkcommon;

import pro.clicknet.bindermarkcommon.BMRequest;
import pro.clicknet.bindermarkcommon.BMResponse;

interface IBMServerService {
    BMResponse get(in BMRequest request);
}
