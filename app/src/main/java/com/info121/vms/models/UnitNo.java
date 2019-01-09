package com.info121.vms.models;

import com.orm.SugarRecord;

/**
 * Created by KZHTUN on 3/6/2018.
 */

public class UnitNo extends SugarRecord {

    private String block;
    private String roomno;
    private String storey;
    private String unitno;

    public UnitNo() {
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getRoomno() {
        return roomno;
    }

    public void setRoomno(String roomno) {
        this.roomno = roomno;
    }

    public String getStorey() {
        return storey;
    }

    public void setStorey(String storey) {
        this.storey = storey;
    }

    public String getUnitno() {
        return unitno;
    }

    public void setUnitno(String unitno) {
        this.unitno = unitno;
    }

    @Override
    public String toString() {
        return roomno;
    }
}
