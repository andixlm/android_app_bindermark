package pro.clicknet.bindermarkcommon;

import pro.clicknet.bindermarkcommon.BMResponse;

interface IBMClientService {
    BMResponse perform(in int size);
}
