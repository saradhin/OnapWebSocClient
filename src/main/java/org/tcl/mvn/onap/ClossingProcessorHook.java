package org.tcl.mvn.onap;

import org.apache.log4j.Logger;

public class ClossingProcessorHook extends Thread {

    private static WSClient wsClient;
    static Logger logger = Logger.getLogger(ClossingProcessorHook.class);

    @Override
    public void run(){
        if(!this.wsClient.equals(null) && this.wsClient.isOpen()) {
            try {
                wsClient.closeBlocking();
                logger.info("Websocket connection is closed on exit hook.");
            }
            catch (InterruptedException intExc) {
                logger.error("Failed to close websocket connection");
                intExc.printStackTrace();
            }
        }
        else {
            logger.info("Websocket connection is either closed or not available.");
        }
        /*System.out.println("Status="+FilesProcessor.status);
        System.out.println("FileName="+FilesProcessor.fileName);
        if(!FilesProcessor.status.equals("FINISHED")){
            System.out.println("Seems some error, sending alert");
        }*/

    }

    public ClossingProcessorHook buildWithWSClient(WSClient wsClientToBeClosed) {
        this.wsClient = wsClientToBeClosed;
        return this;
    }
}
