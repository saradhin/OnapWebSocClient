package org.tcl.mvn.onap;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.tcl.mvn.onap.model.Token;
import org.tcl.mvn.onap.model.UserLogin;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class McpAuthSecureJerseyClient implements IMcpAuthSecureRestClient {
    private static final String MCP_ENDPOINT_POST_TOKEN = "/tron/api/v1/tokens";
    private final WebTarget webTarget;
    private final CustomHostnameVerifier allHostsValid;
    private final String MCP_BASE_URL;
    private final UserLogin userLogin;

    static Logger logger = Logger.getLogger(McpAuthSecureJerseyClient.class);
    public McpAuthSecureJerseyClient(String mcpHost, UserLogin userLogin) {
        ClientConfig clientConfig = new ClientConfig()
                .property(ClientProperties.READ_TIMEOUT, 30000)
                .property(ClientProperties.CONNECT_TIMEOUT, 50000);

        this.allHostsValid = new CustomHostnameVerifier();
        this.MCP_BASE_URL = mcpHost;
        this.userLogin = userLogin;

        SSLContext cSSLCtx = new CustomSSLContext()
                .getCustomSSLContext();

        if(cSSLCtx == null) {
            logger.error("Found null SSL context.");
            logger.error("Setting null web target.\n Check previous error logs.");
            webTarget = null;
        } else {
            webTarget = ClientBuilder
                    .newBuilder()
                    .sslContext(cSSLCtx)
                    .hostnameVerifier(allHostsValid)
                    .build()
                    .register(clientConfig)
                    .register(new LoggingFeature())
                    .target(MCP_BASE_URL);
        }
    }

    @Override
    public String getToken() {
        String mcpAuthToken;

        if(webTarget == null) {
            logger.error("Found null web target. Check previous error logs!");
            mcpAuthToken = "";
            return mcpAuthToken;
        }
        if(userLogin.getUsername().equals("")
                || userLogin.getPassword().equals("")
                || userLogin.getTenant().equals("")) {
            logger.error("Provide valid user login info. Can't request for token without user authentication!");
            mcpAuthToken = "";
            return mcpAuthToken;
        }

        logger.info("Sending authentication request to " + MCP_BASE_URL + MCP_ENDPOINT_POST_TOKEN);
        Response response = webTarget
                .path(MCP_ENDPOINT_POST_TOKEN)
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(userLogin));

        logger.info("Received " + response.getStatus() + " " + response.getStatusInfo() + " from authentication server.");
        if(response.getStatus() >= 200 && response.getStatus() < 300) {
            Token userToken = response.readEntity(Token.class);
            mcpAuthToken = userToken.getToken();
        } else {
            logger.error("Token request is unsuccessful. Verify user credentials with server administrator!");
            mcpAuthToken = "";
        }
        return mcpAuthToken;

    }
}
