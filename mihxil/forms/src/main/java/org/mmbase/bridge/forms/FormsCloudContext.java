/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.forms;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import org.mmbase.util.xml.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.datatypes.DataType;
import org.mmbase.bridge.implementation.*;
import org.mmbase.storage.search.*;
import org.mmbase.security.*;
import org.mmbase.util.*;
import org.xml.sax.InputSource;
import org.mmbase.util.logging.*;

/**
 * The CloudContext which manages a bunch of 'forms'.
 *
 * The URI to receive this CloudContext looks like this:
 * <code>forms:[uri of another cloud context]</code>
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id: FormsCloudContext.java 44890 2011-01-14 18:15:01Z michiel $
 */

public class FormsCloudContext extends AbstractCloudContext {

    private static final Logger LOG = Logging.getLoggerInstance(FormsCloudContext.class);


    private static final Map<String, Map<String, NodeManagerDescription>> contexts = new HashMap<String, Map<String, NodeManagerDescription>>();


    private static final Pattern NORMAL_DIRS = Pattern.compile("[a-zA-Z0-9]+");
    static {
        ResourceLoader forms = ResourceLoader.getConfigurationRoot().getChildResourceLoader("forms");
        LOG.debug("Reading " + forms);
        for (String formName : forms.getChildContexts(NORMAL_DIRS, false)) {
            LOG.debug("Reading " + formName);
            ResourceLoader form = forms.getChildResourceLoader(formName);
            final Map<String, NodeManagerDescription> context = new HashMap<String, NodeManagerDescription>();
            context.put("typedef",
                        new NodeManagerDescription("typedef", new HashMap<String, Field>(), 0));


            for(String nodeManager : form.getResourcePaths(ResourceLoader.XML_PATTERN, true)) {
                try {
                    InputSource is = form.getInputSource(nodeManager);
                    LOG.debug("Reading " + nodeManager + " " + is);
                    ParentBuilderReader reader = new ParentBuilderReader(is) {
                            @Override
                            protected NodeManagerDescription getNodeManagerDescription(String parentBuilder) {
                                return context.get(parentBuilder);
                            }
                        };
                    context.put(reader.getName(), new NodeManagerDescription(reader, -1));
                } catch (IOException ioe) {
                    LOG.error(ioe);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }

            }

            if (context.size() > 0) {
                contexts.put(formName, context);
            }
        }
    }

    final CloudContext wrapped;
    private FormsCloudContext(CloudContext wrapped) {
        this.wrapped = wrapped;
        clouds.addAll(contexts.keySet());
    }

    Map<String, Map<String, NodeManagerDescription>> getNodeManagerDescriptions() {
        return Collections.unmodifiableMap(contexts);
    }



    public Cloud getCloud(String name, org.mmbase.security.UserContext user) throws NotFoundException {
        if (!getNodeManagerDescriptions().containsKey(name)) {
            throw new NotFoundException("No such cloud '" + name + "' (Available are " + getNodeManagerDescriptions().keySet() + ")");
        }
        return new FormsCloud(name, this, user);
    }


    @Override
    public String getUri() {
        return "forms:" + wrapped.getUri();
    }

    @Override
    public AuthenticationData getAuthentication() {
        return wrapped.getAuthentication();
    }


    public static class Resolver extends ContextProvider.Resolver {
        {
            description.setKey("forms");
        }
        @Override
        public CloudContext resolve(String uri) {
            if (uri.startsWith("forms:")){
                String subUri = uri.substring("forms:".length());
                return new FormsCloudContext(ContextProvider.getCloudContext(subUri));
            } else {
                return null;
            }
        }
        @Override
        public boolean equals(Object o) {
            return o != null && o instanceof Resolver;
        }
        @Override
        public String toString() {
            return "forms:local";
        }
    }


 }
