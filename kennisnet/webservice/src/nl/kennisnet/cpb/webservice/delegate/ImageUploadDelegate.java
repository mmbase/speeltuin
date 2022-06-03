/**
 * 
 */
package nl.kennisnet.cpb.webservice.delegate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.kennisnet.cpb.webservice.error.BadRequestException;
import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.service.ContentElementTranslationService;
import nl.kennisnet.cpb.webservice.util.WebserviceHelper;

/**
 * The delegate for uploading an image to Copacabana.
 * 
 *  Input (Multipart form upload via POST): 
 *    - "title" : the title of the image (mandatory, 1-100 chars)
 *    - "alt": the alt text of the image (mandatory, 1-100 chars)
 *    - "tags": search tags associated with this image
 *    - "redactiegroup": the redactiegroup code this image should be connected to (mandatory, 1-10 characters)
 *    - the image file itself
 *  Output: no output, only HTTP state code
 *    - HTTP state 200 if processing was ok
 *    - HTTP state 400 bad request or 500 internal server error in case of errors
 *  
 * 
 * Log: not needed
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class ImageUploadDelegate extends AbstractRestDelegate {
   
   private static final String PARAM_TITLE = "title";
   private static final String PARAM_ALT = "alt";
   private static final String PARAM_TAGS = "tags";
   private static final Object PARAM_REDACTIEGROUP = "redactiegroup";
   
   private static Logger LOG = Logging
   .getLoggerInstance(ImageUploadDelegate.class.getName());

   public final static ImageUploadDelegate INSTANCE = new ImageUploadDelegate();
   
   
   private ImageUploadDelegate() {      
   }



   @Override
   public void doPost(HttpServletRequest req, HttpServletResponse res)
         throws WebserviceException,ServletException, IOException {

      Integer imageNumber = null;
      ServletFileUpload upload = new ServletFileUpload(
            new DiskFileItemFactory());
      
      // Maximum admitted file size = 2 MB?
      upload.setSizeMax(2000000);
      try {         
         List<FileItem> fileItems = upload.parseRequest(req);
         if (fileItems == null || fileItems.size() == 0) {            
            throw new BadRequestException("File upload failed: no form data found ");
         } else {
            String title = null;
            String alt = null;
            String tags = null;
            String redactiegroup = null;
            byte[] file = null;
            
            for (FileItem fi: fileItems) {               
               if (!fi.isFormField()) {                      
                  file = fi.get();
                  
               } else if (PARAM_TITLE.equals(fi.getFieldName())) {
                  title = fi.getString();
               } else if (PARAM_ALT.equals(fi.getFieldName())) {
                  alt = fi.getString();
               } else if (PARAM_TAGS.equals(fi.getFieldName())) {
                  tags = fi.getString();
               } else if (PARAM_REDACTIEGROUP.equals(fi.getFieldName())) {
                  redactiegroup = fi.getString();
               }
               
            }
            
            if (file == null || file.length == 0) {
               throw new BadRequestException("No file present in upload");
            }
            if (StringUtils.isBlank(title) || title.length() > 100) {
               throw new BadRequestException("Title should be 1-100 characters long. Input:" + title);
            }
            if (StringUtils.isBlank(alt) || alt.length() > 100) { //Estimate, not sure how long this may be
               throw new BadRequestException("Alt text should be 1-100 characters long. Input:" + alt);
            }
            if (tags != null &&  tags.length() > 200) { //Estimate, not sure how long this may be
               throw new BadRequestException("Tags should be 0-200 characters long. Input:" + tags);
            }
            
            if (StringUtils.isBlank(redactiegroup) || redactiegroup.length() > 10) { //Estimate, not sure how long this may be
               throw new BadRequestException("Redactiegroup is mandatory (and max. 10 characters).. Input:" + tags);
            }
            
            imageNumber = dao.saveImage(file,title,alt,tags, redactiegroup);
            
            if (imageNumber == null) {
               throw new BadRequestException("Image was not stored correctly");
            }
         }
      } catch (FileUploadException e) {
         throw new BadRequestException(e);         
      }      
      res.setHeader("Location", WebserviceHelper.createImageBaseURL()
            + imageNumber);
      res.setStatus(HttpURLConnection.HTTP_CREATED);
   }

}
