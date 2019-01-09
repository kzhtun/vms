package com.info121.vms.models;

import com.orm.SugarRecord;

/**
 * Created by KZHTUN on 3/6/2018.
 */

public class Block extends SugarRecord{


    private String block;

    public Block() {

    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    @Override
    public String toString() {
        return block;
    }
}
