package org.mmbase.mmget;

import java.io.IOException;
import java.net.*;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Reads a web resource an returns its tags that may contain links to other resources. 
 *
 * @author Andr&eacute; van Toly
 * @version $Id: UrlReader.java 35493 2009-05-28 22:44:29Z michiel $
 */
public abstract class UrlReader {
    private static final Logger log = Logging.getLoggerInstance(UrlReader.class);
    
    /** 
     * Gets all links to resources
     *
     * @return  list with tags that can contain links
     */
    protected abstract ArrayList<String> getLinks() throws IOException;
    
    /** 
     * Contenttype from URLConnection
     *
     * @return  contenttype constant
     */
    protected abstract int getContentType();
    
    /** 
     * The URL from URLConnection. Can be different: 
     *   host/dir returns host/dir/ while
     *   host/file returns host/file.
     *
     * @return  the url URLConnection connects to
     */
    protected abstract URL getUrl();

    protected abstract void close() throws IOException;

}
