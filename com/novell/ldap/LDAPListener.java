/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/org/ietf/ldap/LDAPListener.java,v 1.5 2000/08/03 22:06:15 smerrill Exp $
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
 
package org.ietf.ldap;

import java.io.*;
import java.util.Vector;

import org.ietf.asn1.ldap.*;
import com.novell.ldap.client.*;
//import com.novell.ldap.client.protocol.AbandonRequest;

/**
 * Not in the draft.
 *
 * Represents the message queue associated with a particular LDAP
 * operation or operations.
 */
public abstract class LDAPListener implements TimerListener {

	protected Connection conn;
	protected LDAPMessageQueue queue;
	protected Vector exceptions;

   /**
    * Returns the message IDs for all outstanding requests. The last ID in
    * the array is the messageID of the latest submitted request.
    */
   public int[] getMessageIDs()
	{
		return queue.getMessageIDs();
   }

	/**
	 * Returns a boolean value indicating whether or not the specified
	 * message id exists in the message id list.
	 */
	protected boolean messageIDExists(int msgId)
	{
		return queue.messageIDExists(msgId);
	}

   /**
    * Reports true if a response has been received from the server.
    */
   public boolean isResponseReceived()
	{
		return queue.isResponseReceived();
   }

	/**
	 *
	 */
	public void removeResponses(int msgId)
	{
		queue.removeResponses(msgId);
	}

   /**
    * Merges two response listeners. Moves/appends the content from another
    * listener to this one.
    */
   public void merge(LDAPResponseListener listener2)
	{
   }

	/**
	 * Will write the message and start the timer (if any) for the client
	 * side timeout.
	 */
	public void writeMessage(LDAPMessage msg, int msLimit)
		throws IOException
	{
		int messageID = msg.getMessageID();
		Timer timer = null;

		if(msLimit > 0) {
			timer = new Timer(messageID, msLimit, this);
			timer.start();
		}

		queue.addMessageID(messageID, timer);
		conn.writeMessage(msg);
	}

	/**
	 * @internal
	 * Called by Timer when the timer for a given messageID has expired.
	 */
	public void timedOut(int msgId)
	{
		exceptions.addElement(
			new LDAPException("Client timeout", LDAPException.LDAP_TIMEOUT));
		queue.removeTimer(msgId); // timer thread does not need to be stopped.
		try {
			conn.writeMessage(new LDAPMessage(new AbandonRequest(msgId)));
//				new AbandonRequest(conn.getMessageID(), msgId,
//										 (LDAPControl[])null, true
//										).getLber());
		}
		catch(IOException ioe) {
			// communication error
		}
		removeResponses(msgId);
		removeMessageIDAndNotify(msgId);
	}

	/**
	 * @internal
	 * called by LDAPSearchResults
	 * Will abandon all message IDs for this LDAPListener.
	 */
	public void abandonAll()
	{
		int[] ids = queue.getMessageIDs();
		try {
			for(int i=0; i<ids.length; i++) {
				conn.writeMessage(new LDAPMessage(new AbandonRequest(ids[i])));
//				conn.writeMessage(
//					new AbandonRequest(conn.getMessageID(), ids[i],
//											 (LDAPControl[])null, true
//											).getLber());
			}
		}
		catch(IOException ioe) {
			// communication error
		}
	}
	
	/**
	 *
	 */
	public void addLDAPMessage(org.ietf.asn1.ldap.LDAPMessage message)
	{
		queue.addLDAPMessage(message);
	}

	public org.ietf.asn1.ldap.LDAPMessage getLDAPMessage()
	{
		return queue.getLDAPMessage();
	}

	protected void removeMessageID(int msgId) { // does this need to be synchronized !!!!
		queue.removeMessageID(msgId);
	}

	/**
	 * In the event of an abandon, we want to remove the message id and notify
	 * in case a thread is waiting for a response.
	 */ 
	public void removeMessageIDAndNotify(int id)
	{
		queue.removeMessageIDAndNotify(id);
	}

	public void finalize()
	{
		conn.removeLDAPListener(this);
	}

}

