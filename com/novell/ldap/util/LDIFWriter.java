/* **************************************************************************
 * $Novell: LDIFWriter.java,v 1.29 2002/10/22 22:15:59 $
 *
 * Copyright (C) 2002 Novell, Inc. All Rights Reserved.
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
package com.novell.ldap.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.util.Iterator;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPMessageQueue;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPSearchResult;
import com.novell.ldap.message.LDAPAddRequest;
import com.novell.ldap.message.LDAPDeleteRequest;
import com.novell.ldap.message.LDAPModifyDNRequest;
import com.novell.ldap.message.LDAPModifyRequest;
import com.novell.ldap.util.Base64;

/**
 * LDIFWriter is used to write LDIF content records or LDIF change records
 * to the OutputStream object.
 *
 * <p>The object of the class is used to generate LDIF content record or LDIF
 * change record lines and write the lines to OUtputStream</p>
 */


public class LDIFWriter implements LDAPWriter
{

    private Boolean         requestFile = null;          // request file=true
    private BufferedWriter  bufWriter;
    private String          version;

    /**
     * Constructs an LDIFWriter object.  It allows the setting of the 
     * OutputStreamReader object, and assumes the LDIF version is "1".
     * The type of file written is determined by the first message written
     * to the file.
     *
     * <p>If the first message is one of LDAPAddRequest,
     * LDAPDeleteRequest, LDAPModifyDNRequest, or LDAPModifyRequest the
     * file will be writen as an LDAP request (change) file.</p>
     *
     * <p>If the first message written to the file is an LDAPSearchResult object
     * an LDIF content file will be written.</p>
     *
     * <p>You are not allowed to mix request data and content data</p>
     *
     * @param out     The OutputStream where the LDIF data will be written.
     *
     * @throws IOException for errors writing to the stream.
     */
    public LDIFWriter(OutputStream out)
                throws IOException
    {
        this( out, "1", null );
        return;
    }

    /**
     * Constructs an LDIFWriter object.  It allows the setting of the 
     * OutputStreamReader object, the LDIF version, and the type of LDIF file.
     *
     * @param out     The OutputStream where the LDIF data will be written.
     *
     * @param version The version to set in the LDIF file, must be "1".
     *
     * @param request If true sets the out file type to request (change) data,
     * else the file type will be content.
     *
     * @throws IOException
     */
    public LDIFWriter(OutputStream out, String version, boolean request)
                throws IOException
    {
        this( out, version, new Boolean(request));
        return;
    }
    
    /**
     * Constructs an LDIFWriter object.  It allows the setting of the 
     * OutputStreamReader object, the LDIF version, and the type of LDIF file.
     *
     * <p>You are not allowed to mix request data and content data</p>
     *
     * @param out     The OutputStream where the LDIF data will be written.
     *
     * @param version The version to set in the LDIF file, must be "1".
     *
     * @param request If true sets the out file type to request (change) data,
     * else the file type will be content.
     *
     * @throws IOException
     */
    private LDIFWriter(OutputStream out, String version, Boolean request)
                throws IOException
    {
        super();

        // check LDIF file version
        if ( version != "1" ) {
            throw new RuntimeException(
                        "com.novell.ldap.ldif_dsml.LDIFWriter: LDIF version:"
                                   + "found: " + version + ", Should be: 1");
        }

        this.version = version;
        requestFile = request;
        OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
        bufWriter = new BufferedWriter( osw );
        writeComments("This LDIF file was generated by the LDIF APIs. " +
                        "of Novell's Java LDAP SDK");
        writeVersionLine();
        return;
    }

    /**
     * Write an LDAP record into LDIF file. A request or change operation may
     * be objects of type  LDAPAddRequest, LDAPDeleteRequest,
     * LDAPModifyDNRequest, or LDAPModifyRequest.
     * To write LDIF Content you must use an LDAPSearchResult object.
     *
     * <p>You are not allowed to mix request data and content data</p>
     *
     * @param request LDAPMessage object
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see com.novell.ldap.LDAPSearchResults
     * @see com.novell.ldap.message.LDAPAddRequest
     * @see com.novell.ldap.message.LDAPDeleteRequest
     * @see com.novell.ldap.message.LDAPModifyDNRequest
     * @see com.novell.ldap.message.LDAPModifyRequest
     */
    public void writeMessage(LDAPMessage request)
                throws IOException
    {
        LDAPControl[]  controls = request.getControls();
        
        // Check for valid type
        switch( request.getType()) {
        case LDAPMessage.SEARCH_RESPONSE:
            if( requestFile == null) {
                requestFile = Boolean.FALSE;
            }
            if( isRequest()) {
                throw new RuntimeException("Attempting to write content " +
                        " in a request stream");
            }
            break;
        case LDAPMessage.ADD_REQUEST:
        case LDAPMessage.DEL_REQUEST:
        case LDAPMessage.MODIFY_RDN_REQUEST:
        case LDAPMessage.MODIFY_REQUEST:
            if( requestFile == null) {
                requestFile = Boolean.TRUE;
            }
            if( ! isRequest()) {
                throw new RuntimeException("Attempting to write request " +
                        " in a content stream");
            }
            break;
        default:
            throw new RuntimeException("Unsupported request type: " +
                    request.toString());
        }

        switch( request.getType()) {
        case LDAPMessage.SEARCH_RESPONSE:
            // LDAP Search Result Entry, write entry to outputStream
            LDAPSearchResult sreq = (LDAPSearchResult)request;
            writeAddRequest(sreq.getEntry(), controls);
            break;
        case LDAPMessage.ADD_REQUEST:
            // LDAPAdd request, write entry to outputStream
            LDAPAddRequest areq = (LDAPAddRequest)request;
            writeAddRequest(areq.getEntry(), controls);
            break;
        case LDAPMessage.DEL_REQUEST:
            // LDAPDelete request, write dn to outputStream
            LDAPDeleteRequest dreq = (LDAPDeleteRequest)request;
            writeDeleteRequest( dreq.getDN(), controls );
            break;
        case LDAPMessage.MODIFY_RDN_REQUEST:
            // LDAPModDN request, write request data to outputStream
            LDAPModifyDNRequest rreq = (LDAPModifyDNRequest)request;
            // write to outputStream
            writeModifyDNRequest( rreq.getDN(),
                                  rreq.getNewRDN(),
                                  rreq.getDeleteOldRDN(),
                                  rreq.getParentDN(),
                                  controls );
            break;
        case LDAPMessage.MODIFY_REQUEST:
            // LDAPModify request, write modifications to outputStream
            LDAPModifyRequest mreq = (LDAPModifyRequest)request;
            // write to outputStream
            writeModifyRequest( mreq.getDN(), mreq.getModifications(), controls );
            break;
        }            
        // write an empty line to separate records
        bufWriter.newLine();
        return;
    }

    /**
     * Write a comment line into the LDIF OutputStream.
     *
     * <p> an '#' char is added to the front of each line to indicate that
     * the line is a comment line. If a line contains more than 78
     * chars, it will be split into multiple lines that start
     * with '#' chars.</p>
     *
     * @param lines The comment lines to be written to the OutputStream
     *
     * @throws IOException if an I/O error occurs.
     */
    public void writeComments (String lines) throws IOException
    {
        if (lines != null && lines.length() != 0) {

            if (lines.length() <= 78) {
                // short line, write it out
                bufWriter.write("# " + lines, 0, lines.length()+2);
            }
            else {
                // berak long line
                StringBuffer longLine = new StringBuffer();
                longLine.append(lines);

                while(longLine.length() > 78) {
                    // write "# " and the starting 78 chars
                    bufWriter.write("# " + longLine, 0, 80);
                    // start a new line
                    bufWriter.newLine();
                    // remove the chars that already been written out

                    longLine.delete(0, 78);
                }

                // write the remaining part of the lien out
                bufWriter.write("# " + longLine, 0, longLine.length()+2);
            }
            // start a new line
            bufWriter.newLine();
        }
        return;
    }

    /**
     * Gets the version of the LDIF data associated with the input stream
     *
     * @return the version number
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Returns true if request data ist associated with the input stream,
     * or false if content data.
     *
     * @return true if input stream contains request data.
     */
    public boolean isRequest()
    {
        return requestFile.booleanValue();
    }
    
    /**
     * Check if the input byte array object is safe to make a String.
     *
     * <p>Check if the input byte array contains any un-printable value</p>
     *
     * @param bytes The byte array object to be checked.
     *
     * @return boolean object to incidate that the byte array
     * object is safe or not
     */
    public boolean isPrintable( byte[] bytes )
    {
        if (bytes == null) {
            throw new RuntimeException(
                    "com.novell.ldap.ldif_dsml.LDIFWriter: null pointer");
        }
        else if (bytes.length > 0) {
            for (int i=0; i<bytes.length; i++) {
                if ( (bytes[i]&0x00ff) < 0x20 || (bytes[i]&0x00ff) > 0x7e ) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Write the version line of LDIF file into the OutputStream.
     *
     * <p>Two extra lines will be written to separate version line
     * with the rest of lines in LDIF file</p>
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeVersionLine () throws IOException
    {
        // LDIF file is currently using 'version 1'
        String versionLine = new String("version: " + getVersion());
        bufWriter.write( versionLine, 0, versionLine.length());
        // write an empty line to separate the version line
        // with the rest of the contents in LDIF file
        bufWriter.newLine();
        bufWriter.newLine();
        return;
    }

    /**
     * Write a line into the OutputStream.
     *
     * <p>If the line contains more than 80 chars, it will be splited into
     * multiple lines that start with a space ( ASCII ' ') except the
     * first one.</p>
     *
     * @param line The line to be written to the OutputStream
     *
     * @throws IOException if an I/O error occurs.
     */
    private void writeLine (String line) throws IOException
    {
        if ( line != null && line.length() != 0) {

            if (line.length() <= 80) {
                // write short line
                bufWriter.write(line, 0, line.length());
            }
            else {
                // berak long line
                StringBuffer longLine = new StringBuffer();
                longLine.append(line);
                // write the first 80 chars to outputStream
                bufWriter.write(longLine.toString(), 0, 80);
                // remove the chars that already been written out
                longLine.delete(0, 80);
                bufWriter.newLine();

                while(longLine.length() > 79) {
                    // write continuation line
                    bufWriter.write(" " + longLine, 0, 80);
                    // remove the chars that already been written out
                    longLine.delete(0, 79);
                    // start a new line
                    bufWriter.newLine();
                }

                // write the remaining part of the lien out
                bufWriter.write(" " + longLine, 0, longLine.length()+1);
            }

            // write an empty line
            bufWriter.newLine();
        }
    }

    /**
     * Used to generate LDIF content record or LDIF change/add record lines.
     *
     * <p>Turn LDAPEntry object and LDAPControl[] object into LDIF record
     * lines</p>
     *
     * @param entry  LDAPREntry object
     * @param ctrls  LDAPControl object
     */
    private void writeAddRequest( LDAPEntry entry, LDAPControl[] ctrls )
            throws IOException
    {

        // write dn line(s)
        writeDN(entry.getDN());
        if (isRequest()) {
            // add control line(s)
            if ( ctrls != null ) {
                writeControls( ctrls );
            }
            // add change type line
            writeLine("changetype: add");
        }

        // write attribute line(s)
        LDAPAttributeSet attrSet = entry.getAttributeSet();
        Iterator allAttrs = attrSet.iterator();

        while(allAttrs.hasNext()) {
            LDAPAttribute attr = (LDAPAttribute)allAttrs.next();
            String attrName = attr.getName();
            byte[][] values = attr.getByteValueArray();

            if( values != null) {
               for (int i=0; i<values.length; i++) {
                   writeAttribute(attrName, values[i]);
               }
            }
        }
        return;
    }

    /**
     * Used to generate LDIF change/modify record lines.
     *
     * <p>Turn entry DN, LDAPModification[] object, and LDAPControl[] object
     * into LDIF LDIF record fields and then turn record fields into LDIF
     * change/modify record lines</p>
     *
     * @param dn    String object representing entry DN
     * @param mods  LDAPModification array object
     * @param ctrls LDAPControl array object
     *
     * @see LDAPModification
     * @see LDAPControl
     */
    private void writeModifyRequest( String dn,
                                     LDAPModification[] mods,
                                     LDAPControl[] ctrls )
                throws IOException
    {

        int i, modOp, len = mods.length;
        String attrName, attrValue;
        LDAPAttribute attr;

        // Write the dn field
        writeDN(dn);
        // write controls if there is any
        if ( ctrls != null ) {
            writeControls( ctrls );
        }

        // save change type
        writeLine("changetype: modify");

        // save attribute names and values
        for ( i = 0; i < len; i++ ) {

            modOp = mods[i].getOp();
            attr =  mods[i].getAttribute();
            attrName = attr.getName();
            attrValue = attr.getStringValue();

            switch ( modOp )  {
                case LDAPModification.ADD:
                    writeLine("add: "+ attrName);
                    break;
                case LDAPModification.DELETE:
                    writeLine("delete: "+ attrName);
                    break;
                case LDAPModification.REPLACE:
                    writeLine("replace: "+ attrName);
                    break;
                default:
            }

            // add attribute names and values to record fields
            writeAttribute(attrName, attrValue);

            // add separators between different modify operations
            writeLine("-");
        }
        return;
    }

    /**
     * Used to generate LDIF change/moddn record lines.
     *
     * <p>Turn entry DN and moddn information into LDIF change/modify
     * record lines</p>
     *
     * @param dn      String object representing entry DN
     * @param newRDN  The NewRDN for the ModDN request
     * @param deleteOldRDN the deleteOldRDN flag
     * @param newSuperior   the new Superior DN for a move, or null if rename
     * @param ctrls   LDAPControl array object
     */
    private void writeModifyDNRequest( String dn,
                                       String newRDN,
                                       boolean deleteOldRDN,
                                       String newSuperior,
                                       LDAPControl[] ctrls )
                  throws IOException
    {
        // Write the dn field
        writeDN(dn);
        
        // write controls if there is any
        if ( ctrls != null ) {
            writeControls( ctrls );
        }

        // save change type
        writeLine("changetype: moddn");

        // save new RDN
        if ( Base64.isLDIFSafe(newRDN)) {
            writeLine("newrdn:" + newRDN);
        }
        else {
            // base64 encod newRDN
            // put newRDN into record fields
            writeLine("newrdn:: " + Base64.encode(newRDN));
        }

        // save deleteOldRDN
        writeLine("deleteoldrdn:" + deleteOldRDN);

        // save newSuperior
        if ( newSuperior != null) {
            if ( Base64.isLDIFSafe(newSuperior) ) {
                writeLine("newsuperior:" + newSuperior);
            }
            else {
                // base64 encod newRDN
                // put newSuperior into record fields
                writeLine("newsuperior:: " +  Base64.encode(newSuperior));
            }
        }
        return;
    }

    /**
     * Used to generate LDIF change/delete record lines.
     *
     * <p>Turn entry DN, controls
     * and change type into LDIF change/delete record fields and then turn
     * record fields into LDIF moddn record lines</p>
     *
     * @param dn    String object representing entry DN
     * @param ctrls LDAPControl array object
     *
     * @see LDAPControl
     */
    private void writeDeleteRequest( String dn,
                                   LDAPControl[] ctrls )
                throws IOException
    {
        // write dn line(s)
        writeDN(dn);
        // write control line(s)
        if ( ctrls != null ) {
            writeControls( ctrls );
        }
        // write change type
        writeLine("changetype: delete");
        return;
    }

    /**
     * Write the DN to the outputStream.  If the DN characters are unsafe,
     * the DN is encoded.
     *
     * @param dn the DN to write
     */
    private void writeDN(String dn)
                throws IOException
    {
        if ( Base64.isLDIFSafe(dn) ) { // safe
            writeLine("dn: " + dn);
        }
        else { // not safe
            writeLine("dn:: " + Base64.encode(dn));
        }
        return;
    }

    /**
     * Write control line(s).
     *
     * @param ctrls LDAPControl array object
     */
    private void writeControls(LDAPControl[] ctrls)
                throws IOException
    {
        for ( int i = 0; i < ctrls.length; i++ ) {
            // get control value
            byte[] cVal = ctrls[i].getValue();

            if ( cVal != null && cVal.length > 0 ) {
                // always encode control value(s) ?
                writeLine( "control: " + ctrls[i].getID() + " "
                                       + ctrls[i].isCritical() + ":: "
                                       + Base64.encode(cVal));
            }
            else {
                writeLine("control: " + ctrls[i].getID() + " "
                                      + ctrls[i].isCritical());
            }
        }
        return;
    }

    /**
     * Write attribute name and value into outputStream.
     *
     * <p>Check if attrVal starts with NUL, LF, CR, ' ', ':', or '<'
     * or contains any NUL, LF, or CR and then write it out</p>
     */
    private void writeAttribute(String attrName, String attrVal)
                throws IOException
    {
        if (attrVal != null) {
            if ( Base64.isLDIFSafe(attrVal) ) {
                writeLine( attrName + ": " + attrVal );
            }
            else {
                // IF attrVal contains NON-SAFE-INIT-CHAR or NON-SAFE-CHAR,
                // it has to be base64 encoded
                writeLine(attrName + ":: " + Base64.encode(attrVal));
            }
        }
        return;
    }

    /**
     * Write attribute name and value into outputStream.
     *
     * <p>Check if attribute value contains NON-SAFE-INIT-CHAR or
     * NON-SAFE-CHAR. if it does, it needs to be base64 encoded and then
     * write it out</p>
     */
    private void writeAttribute(String attrName, byte[] attrVal)
                throws IOException
    {
        if (attrVal != null) {
            if ( Base64.isLDIFSafe(attrVal) && isPrintable(attrVal) ) {
                // safe to make a String value
                writeLine( attrName + ": " + new String(attrVal, "UTF-8") );
            }
            else {
                // not safe
                writeLine(attrName + ":: " + Base64.encode(attrVal));
            }
        }
        return;
    }
}
