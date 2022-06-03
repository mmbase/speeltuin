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
 * @version $Id: Never.java 41641 2010-03-26 16:25:29Z michiel $
 */
public class Never implements WebEntryRefreshPolicy {

    public void init(String key, String param) {

    }
    public boolean needsRefresh(CacheEntry e) {
        return false;
    }
}