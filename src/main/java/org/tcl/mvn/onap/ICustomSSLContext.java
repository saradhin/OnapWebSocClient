package org.tcl.mvn.onap;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public interface ICustomSSLContext {
    SSLContext getCustomSSLContext() throws NoSuchAlgorithmException, KeyManagementException;
}
