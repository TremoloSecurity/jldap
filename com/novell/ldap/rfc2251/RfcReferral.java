/* **************************************************************************
 * $Novell: /ldap/src/jldap/com/novell/ldap/rfc2251/RfcReferral.java,v 1.8 2001/03/01 00:30:20 cmorris Exp $
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
package com.novell.ldap.rfc2251;

import java.io.IOException;
import java.io.InputStream;
import com.novell.ldap.asn1.*;

/**
 *        Referral ::= SEQUENCE OF LDAPURL
 */
public class RfcReferral extends ASN1SequenceOf {

	//*************************************************************************
	// Constructor for Referral
	//*************************************************************************

	/**
	 * The only time a Referral object is constructed, is when we are
	 * decoding an RfcLDAPResult or COMPONENTS OF RfcLDAPResult.
	 */
	public RfcReferral(ASN1Decoder dec, InputStream in, int len)
		throws IOException
	{
		super(dec, in, len);

		//convert from ASN1OctetString to RfcLDAPURL here (then look at
		// LDAPResponse.getReferrals())
	}

	//*************************************************************************
	// Accessors
	//*************************************************************************

	// inherited from SequenceOf

}

