package org.tcl.mvn.onap;

import org.tcl.mvn.onap.model.UserLogin;

public class McpAuthSecureRestClientBuilder {

    private String host;
    private UserLogin userLogin;

    public McpAuthSecureRestClientBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public McpAuthSecureRestClientBuilder setUserLogin(String username, String password, String tenant) {
        UserLogin userLogin = new UserLogin();
        userLogin.setUsername(username);
        userLogin.setPassword(password);
        userLogin.setTenant(tenant);
        this.userLogin = userLogin;
        return this;
    }

    public McpAuthSecureJerseyClient build() {
        return new McpAuthSecureJerseyClient(host, userLogin);
    }
}
