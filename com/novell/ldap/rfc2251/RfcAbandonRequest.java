
package org.ietf.asn1.ldap;

import org.ietf.asn1.*;

/**
 *       AbandonRequest ::= [APPLICATION 16] MessageID
 */
public class AbandonRequest extends MessageID implements Request {

	//*************************************************************************
	// Constructor for AbandonRequest
	//*************************************************************************

	/**
	 *
	 */
	public AbandonRequest(int msgId)
	{
		super(msgId);
	}

	//*************************************************************************
	// Mutators
	//*************************************************************************

	//*************************************************************************
	// Accessors
	//*************************************************************************

	/**
	 * Override getIdentifier to return an application-wide id.
	 *
	 * ID = CLASS: APPLICATION, FORM: CONSTRUCTED, TAG: 16. (0x50)
	 */
	public ASN1Identifier getIdentifier()
	{
		return new ASN1Identifier(ASN1Identifier.APPLICATION, false,
			                       ProtocolOp.ABANDON_REQUEST);
	}

}
