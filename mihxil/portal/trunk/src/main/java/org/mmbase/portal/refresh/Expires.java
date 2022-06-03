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
 * Straight-forward refresh policy simply based on a expire time in seconds. Could have used os-cache tags 'time' attribute. But this makes
 * it possible to always specify a class.
 *
 * So this class interprets the refresh 'param' as a time in seconds after which age the block needs a refresh.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Expires.java 41540 2010-03-19 14:46:28Z michiel $
 */
public class Expires implements WebEntryRefreshPolicy {

    int expires = 3600; // Defaults to one hour

    public void init(String key, String param) {
        expires = Integer.parseInt(param);
    }
    public boolean needsRefresh(CacheEntry e) {
        long now = System.currentTimeMillis();
        return now > e.getLastUpdate() + expires * 1000;
    }
}