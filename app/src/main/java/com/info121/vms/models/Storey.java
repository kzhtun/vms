package com.info121.vms.models;

import com.orm.SugarRecord;

/**
 * Created by KZHTUN on 3/1/2018.
 */

public class Storey extends SugarRecord{

    private String block;
    private String storey;

    public Storey() {
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getStorey() {
        return storey;
    }

    public void setStorey(String storey) {
        this.storey = storey;
    }

    @Override
    public String toString() {
        return storey;
    }

}
