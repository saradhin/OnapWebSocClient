package org.tcl.mvn.onap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.tcl.mvn.onap.model.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class WSMain
{
    private static final String MCP_HOST = System.getProperty("MCP_HOST");
    private static final String MCP_USERNAME = System.getProperty("MCP_USER_NAME");
    private static final String MCP_PASSWORD = System.getProperty("MCP_PW");
    private static final String ONAP_HOST = System.getProperty("ONAP_HOST");
    private static final String ONAP_DMAAP_PORT = System.getProperty("ONAP_DMAAP_PORT");
    private static final String ONAP_DMAAP_TOPIC = System.getProperty("ONAP_DMAAP_TOPIC");
    private static final String MCP_TENANT_NAME = "master";
    private static String MCP_BASE_HTTPS_URL;
    private static String MCP_BASE_WEBSOCKET_URL;
    private static String ONAP_DMAAP_URL;



    //private static final String ONAP_AAI_TOKEN = "Basic QUFJOkFBSQ==";

    static Logger logger = Logger.getLogger(WSMain.class);
    public static void main( String[] args )
    {

        BasicConfigurator.configure();
        if(MCP_HOST==null ||
                MCP_USERNAME==null ||
                MCP_PASSWORD==null ||
                ONAP_HOST==null ||
                ONAP_DMAAP_PORT==null ||
                ONAP_DMAAP_TOPIC==null) {
            logger.error("Following properties must be set before starting the application: " +
                    "(MCP_HOST, MCP_USER_NAME, MCP_PW, ONAP_HOST, ONAP_DMAAP_PORT, ONAP_DMAAP_TOPIC).");
            logger.info("Exiting from the application..");
            System.exit(1);
        }

        if(!isHostReachable(MCP_HOST, 443, 400)) {
            logger.error("MCP host " + MCP_HOST + " is not reachable. Provide a reachable MCP host address.");
            logger.info("Exiting from the application..");
            System.exit(1);
        }

        if(!isHostReachable(ONAP_HOST, Integer.parseInt(ONAP_DMAAP_PORT), 400)) {
            logger.error("Onap host " + ONAP_HOST + " is not reachable. Provide a reachable Onap host address.");
            logger.info("Exiting from the application..");
            System.exit(1);
        }

        MCP_BASE_HTTPS_URL = getBaseUrl(MCP_HOST, "https", null);
        MCP_BASE_WEBSOCKET_URL = getBaseUrl(MCP_HOST, "wss", null);
        ONAP_DMAAP_URL = getBaseUrl(ONAP_HOST, "http", ONAP_DMAAP_PORT);

        IMcpAuthSecureRestClient client = new McpAuthSecureRestClientBuilder()
                .setHost(MCP_BASE_HTTPS_URL)
                .setUserLogin(MCP_USERNAME, MCP_PASSWORD, MCP_TENANT_NAME)
                .build();
        String authToken = client.getToken();
        if(authToken.equals("")) {
            logger.error("User authentication failed. Verify user credentials and check previous logs for error detail!");
            System.exit(1);
        }

        Map<String,String> httpHeaders = new HashMap<String, String>();
        httpHeaders.put( "Authorization", "Bearer " + authToken );

        try {

            WSClient mcpWSClient = new WSClient(new URI(MCP_BASE_WEBSOCKET_URL + "/kafkacomet/socket/websocket"), httpHeaders)
                    .buildWithCustomSSL(ONAP_DMAAP_URL, ONAP_DMAAP_TOPIC);
            mcpWSClient.connectBlocking();
            Runtime.getRuntime().addShutdownHook(new ClossingProcessorHook().buildWithWSClient(mcpWSClient));

            if(mcpWSClient.isOpen()) {
                if (subscribeTopic("/subscribe.json", mcpWSClient)) {
                    logger.info("Subscription request sent to websocker server.");
                } else {
                    logger.error("Failed to send subscription request to websocket server.");
                    logger.error("Verify that \"subscription.json\" file is in correct format.");
                    mcpWSClient.closeBlocking();
                    System.exit(1);
                }
            }
            else {
                logger.error("Failed to open web socket connection!");
                System.exit(1);
            }

            while ( true ) {
                Thread.sleep(20000);
                sendHeartBeat(mcpWSClient);

            }

        }
        catch (Exception exc) {
            System.out.println("Error: " + exc.getMessage());

        }
    }

    private static boolean subscribeTopic(String fileName, WSClient mcpWSClient) {
        try {
            URL subscriptionFilePath = ClassLoader.getResource(fileName);
            File fileSubscription = new File(subscriptionFilePath.toURI());
            Gson gson = new Gson();
            Subscribe subscribeMSg = gson.fromJson(new FileReader(fileSubscription), Subscribe.class);
            logger.info("Sending subscription request to webscoket server.. " + gson.toJson(subscribeMSg) );
            mcpWSClient.send(gson.toJson(subscribeMSg));
            
        }
        catch(FileNotFoundException fnfExc) {
            logger.error("Json file for subscribing topics is not available.");
            logger.error("Expected a file named \"subscribe.json\" in main working directory");
            fnfExc.printStackTrace();
            return false;
        }
        catch(Exception exc) {
            logger.error("Error occurred while sending subscription request to server.");
            exc.printStackTrace();
            return false;
        }
        return true;

    }

    private static void sendHeartBeat(WSClient mcpWSClient) {
        JsonObject wsHeartbeat = new JsonObject();
        JsonObject wsHBv2 = new JsonObject();
        wsHBv2.addProperty("ref", 888);
        wsHBv2.addProperty("operation", "heartbeat");
        wsHeartbeat.add("v2", wsHBv2);
        mcpWSClient.send(wsHeartbeat.toString());
    }

    private static String getBaseUrl(String host, String scheme, String port) {
        String url;

        if(scheme.equals("https")) {
            url = "https://" + host + ((port==null)? "" : ":"+ port);
        } else if (scheme.equals("wss")) {
            url = "wss://" + host + ((port==null)? "" : ":"+ port);
        } else {
            url = "http://" + host + ((port==null)? "" : ":"+ port);
        }

        return url;
    }

    private static boolean isHostReachable(String hostIpAddress, int portNumber, int timeOutMilliSec) {
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(hostIpAddress, portNumber), timeOutMilliSec);
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
