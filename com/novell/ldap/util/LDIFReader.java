/* Generated by Together */

package com.novell.ldap.ldif_dsml;

import java.io.IOException;
import java.io.InputStream;
import com.novell.ldap.LDAPEntry;
import java.io.OutputStream;

public class LDIFReader extends LDIF implements LDAPImport {
    public LDIFReader(InputStream in) {
        super();
        super.setVersion( LDIF.LDIF_VERSION_1 );
        return;
    }

    public LDAPOperation readChange() throws IOException  {
        if( isContent()) {
            throw new RuntimeException("Cannot read changes from LDIF content file");
        }
        return currentChange;
    }

    public LDAPEntry readContent() {
        if( ! isContent()) {
            throw new RuntimeException("Cannot read content from LDIF change file");
        }
        return currentEntry;
    }

    /**
     * @supplierCardinality 0..*
     * @clientCardinality 1 
     */
    public final static LDAPOperation currentChange = null;

    /**
     * @clientCardinality 1
     * @supplierCardinality 0..*
     */
    private LDAPEntry currentEntry = null;
}
