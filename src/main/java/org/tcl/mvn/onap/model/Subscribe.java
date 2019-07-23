package org.tcl.mvn.onap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subscribe {

    @SerializedName("v2")
    @Expose
    private V2 v2;

    public V2 getV2() {
        return v2;
    }

    public void setV2(V2 v2) {
        this.v2 = v2;
    }

}



