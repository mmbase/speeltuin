/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.portal.refresh;

import java.util.*;
import com.opensymphony.oscache.web.*;
import com.opensymphony.oscache.base.*;

/**
 * @author Michiel Meeuwissen
 * @version $Id: Always.java 41541 2010-03-19 14:49:13Z michiel $
 */
public class Always implements WebEntryRefreshPolicy {

    public void init(String key, String param) {

    }
    public boolean needsRefresh(CacheEntry e) {
        return true;
    }
}