package pro.clicknet.bindermarkcommon;

import pro.clicknet.bindermarkcommon.BMResponse;

interface IBinderMarkClientService {
    BMResponse perform(in int size);
}
