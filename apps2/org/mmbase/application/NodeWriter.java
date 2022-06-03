/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.application;

import java.io.*;
import java.util.*;
import org.mmbase.module.core.*;

import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.Encode;

/**
 * Utility class for writing xml files for data- and relation sources, suppied by an application export class.
 * Does not support or export dtd information.
 * @author Daniel Ockeleon
 * @author Jaco de Groot
 * @author Pierre van Rooden
 * @author Rob Vermeulen (VPRO)
 */
public class NodeWriter{
    
    // logger
    private static Logger log = Logging.getLoggerInstance(NodeWriter.class.getName());
    
    private MMBase mmb;
    private Vector resultsmsgs;
    private String directory;
    private String builderName;
    private boolean isRelationNode;
    private File file;
    private FileWriter fw;
    private int nrOfNodes;
    
    /**
     * Constructor, opens the initial xml file and writes a header.
     * The file opened for writing is [directory]/[buildername].xml.
     *
     * @param mmb NNBase object for retrieving type information
     * @param resultsmsgs vector of strings fro reporting results.
     * @param directory  the directory to write the files to (including the
     *                   trailing slash).
     * @param buildername name of the builder to export
     * @param isRelationNode if <code>true</code>, the source to write is a relationsource.
     *        Otherwise, a datasource is written.
     */
    NodeWriter(MMBase mmb, Vector resultsmsgs, String directory,
    String builderName, boolean isRelationNode) {
        // store parameters
        this.mmb = mmb;
        this.resultsmsgs = resultsmsgs;
        this.directory = directory;
        this.builderName = builderName;
        this.isRelationNode = isRelationNode;
        // define and open the file to write
        file = new File(directory + builderName + ".xml");
        try {
            log.debug("Opening " + file + " for writing.");
            fw = new FileWriter(file);
        } catch (Exception e) {
            resultsmsgs.addElement("Failed opening file " + file);
        }
        // Write the header
        write("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n");
        //write("<!DOCTYPE builder PUBLIC \"//MMBase - data//\" \"http://www.mmbase.org/dtd/data.dtd\">\n");
        write("<" + builderName + " "
        + "exportsource=\"mmbase://127.0.0.1/install/b1\" "
        + "timestamp=\"20000602143030\">\n");
        // initialize the nr of nodes written
        nrOfNodes = 0;
    }
    
    /**
     *  Writes a node (object) to the datasource file.
     *  Relationsources are stored in a slightly different format from data sources.
     *  @param node The object to store.
     */
    public void write(MMObjectNode node) {
        // retrieve basic information of the node
        int number=node.getIntValue("number");
        String owner=node.getStringValue("owner");
        // start writing the node
        if (isRelationNode) {
            // For a relationnode, the fields snumber, dnumber and rnumber are stored as
            // named references (as snumber, rnumber, and rtype).
            // determine the relation 'type' (use the value of sname in RelDef, or the
            // current buildername by default)
            String rtype = builderName;
            int rnumber=node.getIntValue("rnumber");
            MMObjectNode reldefnode=mmb.getRelDef().getNode(rnumber);
            if (reldefnode!=null) {
                rtype = reldefnode.getStringValue("sname");
            }
            write("\t<node number=\""+number+"\" owner=\""+owner+"\" snumber=\""+ node.getIntValue("snumber") +"\" dnumber=\""+ node.getIntValue("dnumber") +"\" rtype=\""+ rtype +"\"");
            // add directionality if used
            if (InsRel.usesdir) {
                int dir=node.getIntValue("dir");
                if (dir==1) {
                    write(" dir=\"unidirectional\"");
                } else {
                    write(" dir=\"bidirectional\"");
                }
            }
            write(">\n");
        } else {
            // For a data node, store the alias if at all possible.
            String tm=mmb.OAlias.getAlias(number);
            if (tm==null) {
                write("\t<node number=\""+number+"\" owner=\""+owner+"\">\n");
            } else {
                write("\t<node number=\""+number+"\" owner=\""+owner+"\" alias=\""+tm+"\">\n");
            }
        }
        MMObjectBuilder bul=node.parent;
        Enumeration nd=bul.getFields().elements();
        while (nd.hasMoreElements()) {
            FieldDefs def=(FieldDefs)nd.nextElement();
            String key=def.getDBName();
            if (isRelationNode) {
                // note that the routine below assumes
                // fields in a relation node cannot contain binary blobs
                //
                if (!key.equals("number") && !key.equals("owner")
                && !key.equals("otype") && !key.equals("CacheCount")
                && !key.equals("snumber") && !key.equals("dnumber")
                && !key.equals("rnumber") && !key.equals("dir") && !key.startsWith("_")) {
                    write("\t\t<"+key+">"+node.getValue(key)+"</"+key+">\n");
                }
            } else {
                //due to current tcp implementation sometimes nodeField are created
                //those fiels always start with an underscore. If a node starts with
                //we skip it
                if (!key.startsWith("_")) {
                    write(writeXMLField(key, node, directory, mmb));
                }
            }
        }
        // end the node
        write("\t</node>\n\n");
        nrOfNodes++;
    }
    
    /**
     *  Writes a footer to the xml file, and closes the file.
     */
    public void done() {
        // write the footer
        write("</"+ builderName + ">\n");
        resultsmsgs.addElement("Saving " + nrOfNodes + " " + builderName
        + " to : " + file);
        try {
            log.debug("Closing file " + file);
            fw.close();
        } catch (Exception e) {
            resultsmsgs.addElement("Failed closing file " + file);
        }
    }
    
    /**
     *  Writes a string datasource file.
     *  @param s The string to store.
     */
    private void write(String s) {
        try {
            fw.write(s);
        } catch (Exception e) {
            resultsmsgs.addElement("Failed writing to file " + file);
        }
    }
    
    /**
     *  Creates a description string of a field in a node for use in a datasource file.
     *  Binary data (such as images) are stored as seperate binary files, the string then contains
     *  a reference in lieu of the actual value.
     *  A number of 'special purpose' fields (number, owner, otype, CacheCount) are skipped and not written.
     *  Other fields are added 'in line'.
     *  @param key the fieldname to store
     *  @param node The node wose field to store
     *  @param targetpath the path where any binary files may be stored
     *  @param mmb MMBase object for retrieving type info
     *  @return A <code>String</code> descriving in xml-format the field's content (or a reference to that content)
     */
    private static String writeXMLField(String key,MMObjectNode node, String targetpath,MMBase mmb) {
        if (!key.equals("number") && !key.equals("owner") && !key.equals("otype") && !key.equals("CacheCount")) {
            // this is a bad way of doing it imho
            int type=node.getDBType(key);
            String stype=mmb.getTypeDef().getValue(node.getIntValue("otype"));
            if (type==FieldDefs.TYPE_BYTE) {
                String body="\t\t<"+key+" file=\""+stype+"/"+node.getIntValue("number")+"."+key+"\" />\n";
                File file = new File(targetpath+stype);
                try {
                    file.mkdirs();
                } catch(Exception e) {
                    log.error("Can't create dir : "+targetpath+stype);
                }
                byte[] value=node.getByteValue(key);
                saveFile(targetpath+stype+"/"+node.getIntValue("number")+"."+key,value);
                return body;
            } else {
                String body="\t\t<"+key+">"+Encode.encode("ESCAPE_XML", "" + node.getValue(key)) +"</"+key+">\n";
                return body;
            }
        }
        return "";
    }
    
    /**
     *  Stores binary data in a file
     *  @param filename path of the file to store the data
     *  @param value binary data to store (byte array)
     *  @return <code>true</code> if the write was succesful, <code>false</code> if an exception occurred
     */
    static boolean saveFile(String filename,byte[] value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.write(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error(e.toString());
            log.error(Logging.stackTrace(e));
            return false;
        }
        return true;
    }
    
}
