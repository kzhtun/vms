package com.info121.vms.models;

import com.orm.SugarRecord;

/**
 * Created by KZHTUN on 3/1/2018.
 */

public class Purpose extends SugarRecord {

    private String purposeofvisit;

    public Purpose() {
    }

    public String getPurposeofvisit() {
        return purposeofvisit;
    }

    public void setPurposeofvisit(String purposeofvisit) {
        this.purposeofvisit = purposeofvisit;
    }

    @Override
    public String toString() {
        return purposeofvisit;
    }
}
