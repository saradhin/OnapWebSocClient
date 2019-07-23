package org.tcl.mvn.onap;

import org.apache.log4j.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CustomSSLContext implements ICustomSSLContext {
    private TrustManager[] myTrustManager;
    private final SecureRandom secureRandom;
    private final KeyManager[] keyManagers;
    private SSLContext ctx;

    static Logger logger = Logger.getLogger(CustomSSLContext.class);
    public CustomSSLContext() {
        TrustManager[] tm = { new CustomTrustManager() };
        myTrustManager = tm;
        secureRandom = new SecureRandom();
        keyManagers = null;
    }

    @Override
    public SSLContext getCustomSSLContext() {
        setCustomSSLContext();
        return this.ctx;
    }

    private void setCustomSSLContext() {
        try {
            this.ctx = SSLContext.getInstance("TLSv1");
            System.setProperty("https.protocols", "TLSv1");
            ctx.init(keyManagers, myTrustManager, secureRandom);
        }
        catch (NoSuchAlgorithmException nsaExc) {
            logger.error("Error in setting new SSLContext: " + nsaExc.getStackTrace());
        }
        catch(KeyManagementException kmExc) {
            logger.error("Error in setting new SSLContext: " + kmExc.getStackTrace());
        }

    }

}
