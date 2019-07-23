package org.tcl.mvn.onap.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "username",
        "tenant",
        "tenant_context",
        "password",
        "timeout",
        "inactive_expiration_time"
})
public class UserLogin {

    @JsonProperty("username")
    private String username;
    @JsonProperty("tenant")
    private String tenant;
    @JsonProperty("tenant_context")
    private String tenantContext;
    @JsonProperty("password")
    private String password;
    @JsonProperty("timeout")
    private Integer timeout;
    @JsonProperty("inactive_expiration_time")
    private String inactiveExpirationTime;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("tenant")
    public String getTenant() {
        return tenant;
    }

    @JsonProperty("tenant")
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @JsonProperty("tenant_context")
    public String getTenantContext() {
        return tenantContext;
    }

    @JsonProperty("tenant_context")
    public void setTenantContext(String tenantContext) {
        this.tenantContext = tenantContext;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("timeout")
    public Integer getTimeout() {
        return timeout;
    }

    @JsonProperty("timeout")
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @JsonProperty("inactive_expiration_time")
    public String getInactiveExpirationTime() {
        return inactiveExpirationTime;
    }

    @JsonProperty("inactive_expiration_time")
    public void setInactiveExpirationTime(String inactiveExpirationTime) {
        this.inactiveExpirationTime = inactiveExpirationTime;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
