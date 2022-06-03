/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.webservice;

import com.sun.xml.internal.ws.util.xml.ContentHandlerToXMLStreamWriter;
import java.io.*;
import javax.xml.stream.*;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.framework.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.sax.SAXResult;

import org.mmbase.util.logging.*;

/**
 * This implementation of WebService is based on an MMBase component block, which can be an XML too.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ComponentWebService.java 44704 2011-01-10 14:36:15Z michiel $
 */
public class  ComponentWebService extends AbstractWebService {

    private static final Logger log = Logging.getLoggerInstance(ComponentWebService.class);

    private static boolean warnedStaxSupport = false;
    private String component = null;
    private String block     = null;

    public void setComponent(String c) {
        component = c;
    }

    public void setBlock(String b) {
        block = b;
    }

    protected Block getBlock() {
        Component com = ComponentRepository.getInstance().getComponent(component);
        if (com == null) {
            throw new IllegalArgumentException("No such component '" + com + "'");
        }
        Block b = com.getBlock(block);
        if (b == null) {
            throw new IllegalArgumentException("No such block " + com + ":" + block);
        }
        return  b;
    }



    protected static abstract class Job {
        Throwable getException() {
            return null;
        }
        abstract void render(Writer w, Parameters p) throws IOException;
    }


    protected Job getRendererJob() {
        return new Job() {
            Throwable t = null;

            @Override
            public Throwable getException() {
                return t;
            }
            @Override
            public void render(final Writer w, final Parameters params) {
                try {
                    Block b = getBlock();

                    Renderer renderer = b.getRenderer(Renderer.Type.BODY);
                    if (log.isDebugEnabled()) {
                        log.debug("Start rendering " + renderer + " " + params);
                    }
                    renderer.render(params, w, new RenderHints(renderer, WindowState.NORMAL, "id", "class", RenderHints.Mode.NORMAL));
                    log.debug("Finished rendering " + renderer);
                    w.close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    t = e;
                }

            }
        };
    }

    protected static boolean supportStax() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        boolean result;
        try {
            result = transformerFactory.getFeature(StAXResult.FEATURE);
        } catch (IllegalArgumentException iea) {
            // Sigh. Saxon somewhy thinks that if it does not support it, it may also not recognize it by an IAE. Seems silly to me, but well.
            result = false;
        }

        if (result == false) {
            // e.g. saxon.. Sigh.
            if (! warnedStaxSupport) {
                log.warn("" + transformerFactory + " does not support " + StAXResult.FEATURE);
                warnedStaxSupport = true;
            }
        }
        return result;
    }

    protected static Result getResult(XMLStreamWriter  writer) {
        if (supportStax()) {
            return new StAXResult(writer);
        } else {
            // How incredibly easy it is to be cutting edge :-(. This happens more or less alway. Sun's xalan, and saxon don't have the feature.
            return  new SAXResult(new ContentHandlerToXMLStreamWriter(writer) {
                    @Override
                    public void startDocument() {
                        // hmm
                    }
                    /*
                    public void startPrefixMapping(java.lang.String prefix, java.lang.String uri) {
                        log.debug("" + prefix + ":" + uri, new Exception());
                    }
                    */


                });
        }
    }

    private static class State {
        Throwable exception;
        boolean ready = false;
    }

    protected static void render(final Result result, final Job job, final Parameters params) throws IOException, TransformerConfigurationException, InterruptedException  {

        // This stuff needs to be tested, it undoubtly does not work yet.

        final PipedReader  r = new PipedReader();
        final PipedWriter  w = new PipedWriter();

        final State state = new State();
        Runnable transform = new Runnable() {
                @Override
                public synchronized void run() {
                    try {
                        Source xml = new StreamSource(r);
                        log.trace("Transforming");
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        transformer.setOutputProperty(OutputKeys.INDENT, "no");
                        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                        transformer.transform(xml, result);
                        log.trace("Ready");
                    } catch (Exception e) {
                        state.exception = e;
                        log.warn(e);
                    }
                    state.ready = true;
                    log.trace("Notifying");
                    notifyAll();

                }
            };
        ThreadPools.filterExecutor.execute(transform);


        // do _NOT_ render the Component in the help-thread, because that wont' work in Jetty.
        // Jetty used ThreadLocal's to determin the current request/response

        // I first did 'job' in the executor, but that gives horrible problems in Jetty with JspRenderers.

        r.connect(w);
        log.trace("Rendering");
        try {
            job.render(w, params);


            log.trace("Rendering ready");
        } finally {
            w.close();
            synchronized(transform) { // make sure we have the lock
                while (! state.ready) {
                    transform.wait();
                }
            }
        }
    }



   @Override
   public void serve(final XMLStreamWriter writer, final Parameters params) throws WebServiceException {
       try {
           Result result = getResult(writer);
           render(result, getRendererJob(), params);
       } catch (IOException ioe) {
           throw new WebServiceException(ioe);
       } catch (TransformerConfigurationException te) {
           throw new WebServiceException(te);
       } catch (InterruptedException ie) {
           throw new WebServiceException(ie);
       }
    }

    @Override
    public Parameter[] getParameterDefinition() {
        return getBlock().createParameters().getDefinition();
    }

    @Override
    public String toString() {
        return "ComponentWebService:" + getBlock() + "(" + getAction() + ")";
    }

    public static void main(String [] argv) throws Exception {
        try {
            Job testJob = new Job() {
                    public void render(Writer w, Parameters p) throws IOException {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        String line = reader.readLine();
                        while (line != null) {
                            w.write(line);
                            line = reader.readLine();
                        }
                        log.info("Finished rendering ");
                    }
                };
            XMLOutputFactory output = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = output.createXMLStreamWriter(System.out);

            Result result = getResult(writer);
            render(result, testJob, null); //StAXResult(writer);

            log.info("Ready.. ");
        } finally {
            log.info("Shutting down.. ");
            org.mmbase.util.ThreadPools.shutdown();
        }
    }


}
