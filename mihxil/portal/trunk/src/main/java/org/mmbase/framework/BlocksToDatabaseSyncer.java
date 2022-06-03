/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.core.event.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**

 * @author Michiel Meeuwissen
 * @version $Id: ComponentRepository.java 38472 2009-09-07 13:47:12Z michiel $
 * @since MMBase-1.9.4
 */

public class BlocksToDatabaseSyncer extends BasicComponent implements SystemEventListener {
    private static final Logger LOG = Logging.getLoggerInstance(BlocksToDatabaseSyncer.class);


    public BlocksToDatabaseSyncer(String name) {
        super(name);
        EventManager.getInstance().addEventListener(this);
    }

    private boolean ready = false;
    private boolean up = false;
    public void notify(SystemEvent event) {
        boolean relevant = false;
        if (event instanceof ComponentRepository.Ready) {
            ready = true;
            relevant = true;
        } else if (event instanceof SystemEvent.Up) {
            up = true;
            relevant = true;
        }
        if (relevant && ready && up) {
            Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
            NodeManager blocks = cloud.getNodeManager("componentblocks");
            LOG.service("Syncing blocks builder with" + ComponentRepository.getInstance());
            for (Component component : ComponentRepository.getInstance().getComponents()) {
                LOG.service("Syncing component " + component);
                for (Block block : component.getBlocks()) {
                    LOG.debug("Syncing block " + block);
                    NodeQuery query = blocks.createQuery();
                    Queries.addConstraint(query, Queries.createConstraint(query, "component", Queries.getOperator("="), component.getName()));
                    Queries.addConstraint(query, Queries.createConstraint(query, "name", Queries.getOperator("="), block.getName()));
                    LOG.debug("Executing " + query.toSql());
                    NodeList instances = blocks.getList(query);
                    String description = block.getDescription().get(null);
                    if (instances.size() == 0) {
                        Node instance = blocks.createNode();
                        instance.setStringValue("component", component.getName());
                        instance.setStringValue("name", block.getName());
                        instance.setStringValue("description", description);
                        instance.commit();
                        LOG.info("Created new block node " + instance);
                    } else {
                        LOG.debug("Found objects" + instances);
                        Node instance = instances.get(0);
                        if (! instance.getStringValue("description").equals(description)) {
                            instance.setStringValue("description", description);
                            instance.commit();
                        }
                    }
                }
            }
            LOG.service("Ready");
        }
    }
}

