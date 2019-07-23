package org.tcl.mvn.onap.model;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class JerseyRestClient {
    private final String HOST_BASE_URL;
    private final WebTarget WEB_TARGET;

    public JerseyRestClient(String host_base_url) {
        HOST_BASE_URL = host_base_url;
        ClientConfig clientConfig = new ClientConfig()
                .property(ClientProperties.READ_TIMEOUT, 30000)
                .property(ClientProperties.CONNECT_TIMEOUT, 50000);
        WEB_TARGET = ClientBuilder
                .newBuilder()
                .build()
                .register(clientConfig)
                .register(new LoggingFeature())
                .target(HOST_BASE_URL);
    }

    public WebTarget getWEB_TARGET() {
        return WEB_TARGET;
    }
}
