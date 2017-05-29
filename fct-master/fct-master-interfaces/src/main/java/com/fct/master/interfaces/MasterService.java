package com.fct.master.interfaces;

import com.fct.master.dto.MasterBrief;
import com.fct.master.dto.MasterDto;
import com.fct.master.dto.MasterResponse;


/**
 * Created by nick on 2017/5/24.
 */
public interface MasterService {

    MasterDto insertMaster(MasterDto masterDto);

    MasterResponse getMasters(int start, int size);

    void openMasterLive();

    MasterBrief getMasterBrief(int masterId);
}
