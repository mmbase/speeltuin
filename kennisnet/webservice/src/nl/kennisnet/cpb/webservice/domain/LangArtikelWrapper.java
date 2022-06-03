/**
 * 
 */
package nl.kennisnet.cpb.webservice.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mmbase.bridge.Node;

/**
 * A wrapper for a lang artikel. This includes links, internal links, attachments and its redactiegroups. 
 * The images are included within this wrapper. *  
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class LangArtikelWrapper extends NodeWrapper {
   
   
   private ImageWrapper imageBeforeIntro;
   
   /**
    * Optional image before the first paragraph
    */
   private ImageWrapper imageBeforeFirstParagraph;
   
   /**
    * The paragraphs
    */
   private List<ParagraphWrapper> paragraphs;

   public LangArtikelWrapper(Node n,
         List<OrderedWrapper> links, 
         List<OrderedWrapper> iLinks,
         List<OrderedWrapper> attachments,
         List<PageWrapper> pages,
         List<RedactiegroepWrapper> redactiegroepen,
         ImageWrapper imageBeforeIntro, 
         ImageWrapper imageBeforeFirstParagraph,
         List<ParagraphWrapper> paragraphs) {
      
      super (n, links, iLinks, attachments, pages, redactiegroepen, null);
      this.imageBeforeIntro = imageBeforeIntro;
      this.imageBeforeFirstParagraph = imageBeforeFirstParagraph;
      Collections.sort(paragraphs);
      this.paragraphs = paragraphs;
   }

  
   public ImageWrapper getImageBeforeIntro() {
      return imageBeforeIntro;
   }

   public ImageWrapper getImageBeforeFirstParagraph() {
      return imageBeforeFirstParagraph;
   }

   public List<ParagraphWrapper> getParagraphs() {
      return paragraphs;
   }
}
