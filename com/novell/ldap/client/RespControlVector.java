/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/client/RespControlVector.java,v 1.1 2000/11/22 22:17:42 vtag Exp $
 *
 * Copyright (C) 1999, 2000 Novell, Inc. All Rights Reserved.
 * 
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. 
 ***************************************************************************/

package com.novell.ldap.client;

import java.util.*;

/**
 * The <code>MessageVector</code> class implements extends the
 * existing Vector class so that it can be used to maintain a
 * list of currently registered control responses.  
 */
public class RespControlVector extends java.util.Vector
{
    public RespControlVector( int cap, int incr)
    {
        super( cap, incr);
        return;
    }
 
    /** Inner class defined to create a temporary object to encapsulate
     * all registration information about a response control.  This class
     * cannot be used outside this class */
    private class RegisteredControl
    {
        public String myOID;
        public Class myClass;
        
        public RegisteredControl(String oid, Class controlClass)
        {
            myOID = oid;
            myClass = controlClass;
        }
        
    }

    /* Adds a control to the current list of registered response controls.
     *
     */
    public synchronized void registerResponseControl(String oid, Class controlClass)
    {
        addElement(new RegisteredControl(oid, controlClass));
    }
    
    /* Searches the list of registered controls for a mathcing control.  We
     * search using the OID string.  If a match is found we return the 
     * Class name that was provided to us on registration.
     */
    public synchronized Class findResponseControl(String searchOID)
                throws NoSuchFieldException
    {
        RegisteredControl ctl = null;
        
        /* loop through the contents of the vector */
        for( int i = 0; i < elementCount; i++) {
            
            /* Get next registered control */
            if( (ctl = (RegisteredControl)elementData[i]) == null) {
                throw new NoSuchFieldException();
            }
            
            /* Does the stored OID match with whate we are looking for */
            if(ctl.myOID.compareTo(searchOID) == 0) {
                
                /* Return the class name if we have match */
                return ctl.myClass;
            }
        }   
        /* The requested control does not have a registered response class */
        return null;
    }
      
 }