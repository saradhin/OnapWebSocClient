package org.tcl.mvn.onap;

import com.google.gson.*;
import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.tcl.mvn.onap.model.JerseyRestClient;

import javax.net.SocketFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Iterator;
import java.util.Map;

public class WSClient extends WebSocketClient {
    static Logger logger = Logger.getLogger(WSClient.class);

    private String ONAP_DMAAP_TOPIC;
    private String ONAP_DMAAP_URL;

    public WSClient( URI serverUri ) {
        super( serverUri );
    }

    public WSClient( URI serverUri, Map<String, String> httpHeaders ) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        logger.info( "Connected to server: " + getURI() + "\n" );

    }

    @Override
    public void onMessage( String message ) {
        logger.info( "Received from server: " );
        processMessage(message);

    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        logger.info( "Disconnected from: " + getURI() + "; Code: " + code + " " + reason + "\n" );
        logger.info("Connection closed by: " + ( remote ? "remote peer" : "us" )  + "\n");

    }

    @Override
    public void onError( Exception ex ) {
        logger.error(ex.getStackTrace());

    }

    public String getONAP_DMAAP_TOPIC() {
        return ONAP_DMAAP_TOPIC;
    }

    public void setONAP_DMAAP_TOPIC(String ONAP_DMAAP_TOPIC) {
        this.ONAP_DMAAP_TOPIC = ONAP_DMAAP_TOPIC;
    }

    public String getONAP_DMAAP_URL() {
        return ONAP_DMAAP_URL;
    }

    public void setONAP_DMAAP_URL(String ONAP_DMAAP_URL) {
        this.ONAP_DMAAP_URL = ONAP_DMAAP_URL;
    }

    public WSClient buildWithCustomSSL() {
        SocketFactory sockFactory = new CustomSSLContext()
                .getCustomSSLContext()
                .getSocketFactory();
        this.setSocketFactory(sockFactory);
        return this;
    }

    public WSClient buildWithCustomSSL(String onapDmaapUrl, String onapDmaapTopic) {
        SocketFactory sockFactory = new CustomSSLContext()
                .getCustomSSLContext()
                .getSocketFactory();
        this.setSocketFactory(sockFactory);
        this.setONAP_DMAAP_URL(onapDmaapUrl);
        this.setONAP_DMAAP_TOPIC(onapDmaapTopic);
        return this;
    }

    private void processMessage(String message) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject objResponse = new JsonParser().parse(message).getAsJsonObject();
        if(objResponse.has("v2")) {
            JsonObject objV2 = objResponse.getAsJsonObject("v2");
            if(objV2.getAsJsonPrimitive("operation").getAsString().equals("relay")) {
                JsonObject objAlarmEvent =  objV2.getAsJsonObject("message").getAsJsonObject("body").getAsJsonObject("event");
                JsonObject vesAlarmObject =  convertToVESFormat(objAlarmEvent);
                postDmaapMsg(vesAlarmObject);
                if(objAlarmEvent.has("alarm")) {
                    JsonObject objAlarmData = objAlarmEvent.getAsJsonObject("alarm").getAsJsonObject("data");
                    if(objAlarmData.has("attributes")) {
                        JsonObject objAlarmAttributes = objAlarmData.getAsJsonObject("attributes");

                        System.out.println(gson.toJson(new JsonParser().parse(objAlarmAttributes.toString())));
                    }
                }

            }
            else {
                System.out.println(gson.toJson(new JsonParser().parse(objV2.toString())));
            }
        }
    }

    private JsonObject convertToVESFormat(JsonObject jsonMCPEvent) {
        try {
            JsonObject objAlarmAttributes = jsonMCPEvent
                    .getAsJsonObject("alarm")
                    .getAsJsonObject("data")
                    .getAsJsonObject("attributes");

            Iterator itObjAlarmAttr = objAlarmAttributes.keySet().iterator();
            JsonArray vesAddInfoArray = new JsonArray();

            while (itObjAlarmAttr.hasNext()) {
                String key = (String) itObjAlarmAttr.next();
                String value = objAlarmAttributes.get(key).toString();

                JsonObject objCurr = new JsonObject();
                objCurr.addProperty("name", key);
                objCurr.addProperty("value", value);

                vesAddInfoArray.add(objCurr);

            }

            JsonObject vesFaultFields = new JsonObject();
            vesFaultFields.addProperty("eventSeverity", objAlarmAttributes.get("condition-severity").getAsString()); //"CRITICAL","MAJOR","MINOR","WARNING","NORMAL"
            vesFaultFields.addProperty("alarmCondition", objAlarmAttributes.get("native-condition-type").getAsString());
            vesFaultFields.addProperty("faultFieldsVersion", "2.1");
            vesFaultFields.addProperty("specificProblem", objAlarmAttributes.get("native-condition-type").getAsString());
            vesFaultFields.addProperty("eventSourceType", "Ciena " + objAlarmAttributes.get("node-type").getAsString());
            vesFaultFields.addProperty("vfStatus", "Active");  //"Active","Idle","Preparing to terminate","Ready to terminate", "Requesting termination"
            vesFaultFields.add("alarmAdditionalInformation", vesAddInfoArray);

            String startDTStr = objAlarmAttributes.get("first-raise-time").getAsString();
            String lastDTStr = objAlarmAttributes.get("last-raise-time").getAsString();

            long startEpochDTStr = convertDTStrToEpoch(startDTStr);
            long lastEpochDTStr = convertDTStrToEpoch(lastDTStr);

            JsonObject vesCommonEventHeader = new JsonObject();
            vesCommonEventHeader.addProperty("domain", "fault"); //"fault","heartbeat","measurementsForVfScaling","mobileFlow","other","sipSignaling","stateChange","syslog","thresholdCrossingAlert","voiceQuality"
            vesCommonEventHeader.addProperty("version", "1.1");
            vesCommonEventHeader.addProperty("eventId", objAlarmAttributes.get("id").getAsString());
            vesCommonEventHeader.addProperty("eventType", jsonMCPEvent.get("_type").getAsString());
            vesCommonEventHeader.addProperty("eventName", jsonMCPEvent.get("_type").getAsString());
            vesCommonEventHeader.addProperty("sourceName", objAlarmAttributes.get("device-name").getAsString() + ":" + objAlarmAttributes.get("resource").getAsString());
            vesCommonEventHeader.addProperty("priority", "High"); //"High","Medium","Normal","Low"
            vesCommonEventHeader.addProperty("reportingEntityName", objAlarmAttributes.get("device-name").getAsString());
            vesCommonEventHeader.addProperty("startEpochMicrosec", startEpochDTStr);
            vesCommonEventHeader.addProperty("lastEpochMicrosec", lastEpochDTStr);
            vesCommonEventHeader.addProperty("sequence", objAlarmAttributes.get("number-of-occurrences").getAsNumber());

            JsonObject vesEvent = new JsonObject();
            vesEvent.add("commonEventHeader", vesCommonEventHeader);
            vesEvent.add("faultFields", vesFaultFields);

            JsonObject vesFinal = new JsonObject();
            vesFinal.add("event", vesEvent);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            logger.info("Converted to VES format: ");
            System.out.println(gson.toJson(new JsonParser().parse(vesFinal.toString())));
            return vesFinal;
        }
        catch(Exception exc) {
            logger.error("Error in converting BP alarm object into Onap VES format.");
            logger.error(exc.getStackTrace());
            return null;

        }

    }

    private long convertDTStrToEpoch(String dateTimeString) {
        try {

            DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd")
                    .appendLiteral('T')
                    .appendPattern("HH:mm:ss.SSS")
                    .appendLiteral('Z')
                    .toFormatter();
            LocalDateTime dtStart = LocalDateTime.parse(dateTimeString, dtf);
            Instant instantStart = dtStart.toInstant(ZoneOffset.UTC);
            return instantStart.toEpochMilli();
        }
        catch (Exception exc) {
            logger.error("Error in converting Date-time string to Epoch format! " + exc.getStackTrace());
            return Long.MIN_VALUE;

        }

    }

    private void postDmaapMsg(JsonObject vesEvent) {
        try {
            JerseyRestClient restClient = new JerseyRestClient(ONAP_DMAAP_URL);
            WebTarget onapWebTarget = restClient.getWEB_TARGET();
            if (onapWebTarget == null) {
                logger.error("Found null web target for Onap Dmaap! Can't post alarm in Dmaap.");
                return;
            }
            Gson gson = new Gson();
            logger.info("Sending POST call to DMAAP topic " + ONAP_DMAAP_URL + "/events/" + ONAP_DMAAP_TOPIC);
            Response response = onapWebTarget
                    .path("/events/" + ONAP_DMAAP_TOPIC)
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(gson.toJson(vesEvent), MediaType.APPLICATION_JSON));

            logger.info("Received " + response.getStatus() + " " + response.getStatusInfo() + " from DMAAP.");
            if (response.getStatus() >= 200 && response.getStatus() < 300) {
                logger.info("Alarm event is posted to DMAAP.");

            } else {
                logger.error("Token request is unsuccessful. Verify user credentials with server administrator!");

            }
        }
        catch (Exception exc) {
            logger.error("Error in posting VES alarm event into DMAAP. " + exc.getStackTrace());

        }

    }

}
