/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/LDAPSecureSocketFactory.java,v 1.7 2001/03/01 00:29:57 cmorris Exp $
 *
 * Copyright (C) 1999, 2000, 2001 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.7 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.7 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap;

import java.io.*;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;



/**
 * Represents the socket factory that creates secure socket connections to
 * LDAP servers using JSSE technology.
 *
 */
public class LDAPJSSESecureSocketFactory implements LDAPSocketFactory {
    private static SocketFactory ssl;
    /**
     * Constructs an LDAPSecureSocketFactory object.
     *
     */
    public LDAPJSSESecureSocketFactory()
    {
        ssl = SSLSocketFactory.getDefault();
    }

    /**
     * Returns the socket connected to the LDAP server with the specified
     * host name and port number.
     *
     * <p>The secure connection is established to the server when this
     * call returns.</p>
     *
     * @param host The host name or a dotted string representing the IP address
     *             of the LDAP server to which you want to establish
     *             a connection.
     *<br><br>
     * @param port The port number on the specified LDAP server that you want to
     *             use for this connection. The default LDAP port for SSL
     *             connections is 636.
     *
     * @return A socket to the LDAP server using the specifiec host name and
     *         port number.
     *
     * @exception IOException A socket to the specified host and port
     *                          could not be created.
     * @exception UnknownHostException The specified host could not be found.
     */
    public java.net.Socket makeSocket(String host, int port)
        throws IOException, UnknownHostException
    {
        try {
            return ssl.createSocket(host, port);
        }   // For now just turn all exceptions into IOException
        catch( Exception e) {
           throw new IOException( e.toString());
        }
    }

    public void testInstallation(String inetaddress, int port) throws java.io.IOException {
        SSLSocket sslSocket = (SSLSocket)ssl.createSocket(inetaddress, port);
        String [] cipherSuites = sslSocket.getEnabledCipherSuites();
        for(int i= 0; i < cipherSuites.length; i++){
            System.out.println("Cipher Suite " + i + " = " + cipherSuites[i]);
        }
    }
}