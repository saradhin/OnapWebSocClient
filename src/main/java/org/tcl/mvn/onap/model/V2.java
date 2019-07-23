package org.tcl.mvn.onap.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class V2 {

    @SerializedName("ref")
    @Expose
    private Integer ref;
    @SerializedName("operation")
    @Expose
    private String operation;
    @SerializedName("topics")
    @Expose
    private List<Topic> topics = null;

    public Integer getRef() {
        return ref;
    }

    public void setRef(Integer ref) {
        this.ref = ref;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

}
