/**
 * 
 */
package nl.kennisnet.cpb.webservice.util;

import java.io.ByteArrayInputStream;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.kennisnet.cpb.cloud.builders.ImageBuilder;
import nl.kennisnet.cpb.util.ImageInfo;

/**
 * Convenience class providing functionality similar to the ImageBuilder class.
 * 
 * Log: not needed
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class ImageHelper {

   /**
    * Logger instance.
    */
   private static Logger log = Logging.getLoggerInstance(ImageBuilder.class.getName());

   private ImageInfo imageInfo;


   /**
    * Creator.
    */
   public ImageHelper() {
      imageInfo = new ImageInfo();
      log.debug("Constructed ImageBuilder");
   }
   
   public Integer getImageWidth(byte[] data) {
      imageInfo.setInput(new ByteArrayInputStream(data));

      if (!imageInfo.check()) {
         log.service("ImageBuilder: Error parsing image");
      }

      log.debug("ImageBuilder: width being returned = " + imageInfo.getWidth());
      return imageInfo.getWidth();
   }
   
   public Integer getImageHeight(byte[] data) {
      imageInfo.setInput(new ByteArrayInputStream(data));

      if (!imageInfo.check()) {
         log.service("ImageBuilder: Error parsing image");
      }

      log.debug("ImageBuilder: height being returned = " + imageInfo.getHeight());
      return imageInfo.getHeight();
   }
}
