package pro.clicknet.bindermarkcommon;

import pro.clicknet.bindermarkcommon.BMResponse;
import pro.clicknet.bindermarkcommon.IBMServerService;

interface IBMClientService {
    BMResponse perform(in int size);

    void setServer(in IBMServerService server);
}
