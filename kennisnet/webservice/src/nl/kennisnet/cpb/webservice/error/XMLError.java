package nl.kennisnet.cpb.webservice.error;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/** Copy of the version of this class in the contenthub
 * @author dekok01
 *
 */
public class XMLError {

   public String xml;


   public String getXml() {
      return this.xml;
   }
 

   public XMLError(Throwable e) {
      String buff = new String();
      try { 
         ByteArrayOutputStream bstr = new ByteArrayOutputStream();
         OutputStreamWriter out = new OutputStreamWriter(bstr,"UTF-8");
         PrintWriter writer = new PrintWriter(out);
         //e.printStackTrace(new PrintWriter(writer));
         e.printStackTrace(writer);
         writer.flush();
         buff= bstr.toString();
      } catch (Exception err) {
         err.printStackTrace();
      }
      this.xml = loadXml(e.getMessage(), buff.toString());
   }


   public XMLError(String msg) {
      this.xml = loadXml(msg, "");
   }


   private String loadXml(String message, String trace) {
      Element root = new Element("error");      
      Element msg = new Element("msg");
      Element stackTrace = new Element("debug");
      
      stackTrace.addContent(new CDATA(trace));
      msg.addContent(new CDATA(message));
      root.addContent(msg);
      root.addContent(stackTrace);
      Document doc = new Document();
      doc.setRootElement(root);
      XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
      return out.outputString(doc);
   }
}
