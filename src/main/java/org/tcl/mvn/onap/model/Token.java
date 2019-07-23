package org.tcl.mvn.onap.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "token",
        "user",
        "loginDetail",
        "createdTime",
        "timeout",
        "inactiveExpirationTime",
        "failedLoginAttempts",
        "lastSuccessIpAddress",
        "lastSuccessLogin"
})
public class Token {

    @JsonProperty("token")
    private String token;
    @JsonProperty("user")
    private String user;
    @JsonProperty("loginDetail")
    private LoginDetail loginDetail;
    @JsonProperty("createdTime")
    private String createdTime;
    @JsonProperty("timeout")
    private Integer timeout;
    @JsonProperty("inactiveExpirationTime")
    private Object inactiveExpirationTime;
    @JsonProperty("failedLoginAttempts")
    private Integer failedLoginAttempts;
    @JsonProperty("lastSuccessIpAddress")
    private String lastSuccessIpAddress;
    @JsonProperty("lastSuccessLogin")
    private String lastSuccessLogin;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("token")
    public String getToken() {
        return token;
    }

    @JsonProperty("token")
    public void setToken(String token) {
        this.token = token;
    }

    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }

    @JsonProperty("loginDetail")
    public LoginDetail getLoginDetail() {
        return loginDetail;
    }

    @JsonProperty("loginDetail")
    public void setLoginDetail(LoginDetail loginDetail) {
        this.loginDetail = loginDetail;
    }

    @JsonProperty("createdTime")
    public String getCreatedTime() {
        return createdTime;
    }

    @JsonProperty("createdTime")
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    @JsonProperty("timeout")
    public Integer getTimeout() {
        return timeout;
    }

    @JsonProperty("timeout")
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @JsonProperty("inactiveExpirationTime")
    public Object getInactiveExpirationTime() {
        return inactiveExpirationTime;
    }

    @JsonProperty("inactiveExpirationTime")
    public void setInactiveExpirationTime(Object inactiveExpirationTime) {
        this.inactiveExpirationTime = inactiveExpirationTime;
    }

    @JsonProperty("failedLoginAttempts")
    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    @JsonProperty("failedLoginAttempts")
    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    @JsonProperty("lastSuccessIpAddress")
    public String getLastSuccessIpAddress() {
        return lastSuccessIpAddress;
    }

    @JsonProperty("lastSuccessIpAddress")
    public void setLastSuccessIpAddress(String lastSuccessIpAddress) {
        this.lastSuccessIpAddress = lastSuccessIpAddress;
    }

    @JsonProperty("lastSuccessLogin")
    public String getLastSuccessLogin() {
        return lastSuccessLogin;
    }

    @JsonProperty("lastSuccessLogin")
    public void setLastSuccessLogin(String lastSuccessLogin) {
        this.lastSuccessLogin = lastSuccessLogin;
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