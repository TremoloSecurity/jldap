/* **************************************************************************
 * $Novell: /ldap/src/jldap/ldap/src/com/novell/ldap/LDAPDN.java,v 1.4 2000/08/28 22:18:56 vtag Exp $
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
 
package com.novell.ldap;
 
/*
 * 4.9 public class LDAPDN
 */
 
/**
 *  Specifies methods for manipulating a distinguished name (DN)
 *  and a relative distinguished name (RDN)).
 */
public class LDAPDN {

   /*
    * 4.9.1 equals
    */

   /**
    * Normalizes the names (if appropriate) and then determines whether the 
    * two distinguished names are the same.
    *
    *  @param dn1            String form of first DN to compare.
    *<br><br>
    *  @param dn2            String form of second DN to compare.
    *
    * @return Returns true if the two strings correspond to the same DN, 
    */
   public static boolean equals (String dn1, String dn2) {
      return false;
   }

   /*
    * 4.9.2 escapeRDN
    */

   /**
    * Returns the RDN after escaping the characters requiring escaping.
    *
    * <p>For example, for the rdn "�cn=Acme, Inc�", the escapeRDN method 
    * returns "�cn=Acme\, Inc�".</p>
    *
    *  @param rdn            The RDN to escape.
    *
    *@return The RDN with escaping characters.
    */
   public static String escapeRDN (String rdn) {
      return null;
   }

   /*
    * 4.9.3 unescapeRDN
    */

   /**
    * Returns the RDN after unescaping the characters requiring escaping.
    *
    * <p>For example, for the rdn "�cn=Acme\, Inc�", the unescapeRDN method 
    * returns "�cn=Acme, Inc�".</p>
    *
    *  @param rdn            The RDN to unescape.
    *
    * @return The RDN with the escaping characters removed.
    */
   public static String unescapeRDN (String rdn) {
      return null;
   }

   /*
    * 4.9.4 explodeDN
    */

   /**
    * Returns the individual components of a distinguished name (DN).
    *
    * @param dn        The distinguished name, for example, "cn=Babs
    *                  Jensen,ou=Accounting,o=Acme,c=US"
    *<br><br>
    * @param noTypes   If true, returns only the values of the
    *                  components, and not the names, for example, "Babs
    *                  Jensen", "Accounting", "Acme", "US" instead of
    *                  "cn=Babs Jensen", "ou=Accounting", "o=Acme", and
    *                  "c=US".
    *
    * @return An array of strings representing the individual components 
    * of a DN, or null if the DN is not a valid DN.
    */
   public static String[] explodeDN(String dn, boolean noTypes) {
      return null;
   }

   /*
    * 4.9.5 explodeRDN
    */

   /**
    * Returns the individual components of a relative distinguished name
    * (RDN).
    *
    *  @param rdn     The relative distinguished name, or in other words,
    *                 the left-most component of a distinguished name.
    *<br><br>
    *  @param noTypes   If true, returns only the values of the
    *                  components, and not the names of the component, for
    *                  example "Babs Jensen" instead of "cn=Babs Jensen".
    *
    * @return An array of strings representing the individual components 
    * of an RDN, or null if the RDN is not a valid RDN.

    */
   public static String[] explodeRDN(String rdn, boolean noTypes) {
      return null;
   }

}
