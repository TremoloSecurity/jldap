/* **************************************************************************
 * $OpenLDAP$
 *
 * Copyright (C) 1999 - 2002 Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND
 * TREATIES. USE, MODIFICATION, AND REDISTRIBUTION OF THIS WORK IS SUBJECT
 * TO VERSION 2.0.1 OF THE OPENLDAP PUBLIC LICENSE, A COPY OF WHICH IS
 * AVAILABLE AT HTTP://WWW.OPENLDAP.ORG/LICENSE.HTML OR IN THE FILE "LICENSE"
 * IN THE TOP-LEVEL DIRECTORY OF THE DISTRIBUTION. ANY USE OR EXPLOITATION
 * OF THIS WORK OTHER THAN AS AUTHORIZED IN VERSION 2.0.1 OF THE OPENLDAP
 * PUBLIC LICENSE, OR OTHER PRIOR WRITTEN CONSENT FROM NOVELL, COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 ******************************************************************************/

package com.novell.ldap;

import java.util.Enumeration;

import com.novell.ldap.asn1.*;
import com.novell.ldap.rfc2251.*;

/**
 *  Encapsulates a single search result that is in response to an asynchronous
 *  search operation.
 *
 *  <p>Sample Code: <a href="http://developer.novell.com/ndk/doc/samplecode/jldap_sample/asynchronous/Searchas.java.html">Searchas.java</p>
 *
 * @see LDAPConnection#search
 */
public class LDAPSearchResult extends LDAPMessage {

   /**
    * Constructs an LDAPSearchResult object.
    *
    * @param message The RfcLDAPMessage with a search result.
    */
   /*package*/
   LDAPSearchResult(RfcLDAPMessage message)
   {
      super(message);
   }

   /**
    * Returns the entry of a server's search response.
    *
    * @return The LDAPEntry associated with this LDAPSearchResult
    */
   public LDAPEntry getEntry() {
      LDAPAttributeSet attrs = new LDAPAttributeSet();

      ASN1Sequence attrList =
         ((RfcSearchResultEntry)message.getProtocolOp()).getAttributes();

      ASN1Object[] seqArray = attrList.toArray();
      for(int i = 0; i < seqArray.length; i++) {
         ASN1Sequence seq = (ASN1Sequence)seqArray[i];
         LDAPAttribute attr =
            new LDAPAttribute(((ASN1OctetString)seq.get(0)).stringValue());

         ASN1Set set = (ASN1Set)seq.get(1);
         Object[] setArray = set.toArray();
         for(int j = 0; j < setArray.length; j++) {
            attr.addValue(((ASN1OctetString)setArray[j]).byteValue());
         }
         attrs.add(attr);
      }

      return new LDAPEntry(
         ((RfcSearchResultEntry)message.getProtocolOp()).getObjectName().stringValue(),
         attrs);
   }
}
