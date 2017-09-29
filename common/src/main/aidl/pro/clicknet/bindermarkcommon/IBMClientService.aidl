package pro.clicknet.bindermarkcommon;

import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBMServerService;

interface IBMClientService {
    void setup(in int size, in IBMServerService server);

    BMResponse perform();
}
