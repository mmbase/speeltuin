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
import nl.kennisnet.cpb.webservice.util.WebserviceHelper;

/**
 * The delegate for uploading an attachment to Copacabana.
 * 
 *  Input (Multipart form upload via POST): 
 *    - "title" : the title of the image (mandatory, 1-100 chars)
 *    - "alt": the alt text of the image (mandatory, 1-100 chars)
 *    - "tags": search tags associated with this image
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
public class AttachmentUploadDelegate extends AbstractRestDelegate {
   
   private static final String PARAM_TITLE = "title";      
   
   private static Logger LOG = Logging
   .getLoggerInstance(AttachmentUploadDelegate.class.getName());

   public final static AttachmentUploadDelegate INSTANCE = new AttachmentUploadDelegate();
   
   private AttachmentUploadDelegate() {      
   }



   @Override
   public void doPost(HttpServletRequest req, HttpServletResponse res)
         throws WebserviceException,ServletException, IOException {

      ServletFileUpload upload = new ServletFileUpload(
            new DiskFileItemFactory());
      
      // Maximum admitted file size = 2 MB?
      upload.setSizeMax(2000000);
      int number = -1;
      try {         
         List<FileItem> fileItems = upload.parseRequest(req);
         if (fileItems == null || fileItems.size() == 0) {            
            throw new BadRequestException("File upload failed: no form data found ");
         } else {
            String title = null;
            String filename = null;
            byte[] file = null;
            
            for (FileItem fi: fileItems) {               
               if (!fi.isFormField()) {                      
                  file = fi.get();
                  filename = fi.getName();
               } else if (PARAM_TITLE.equals(fi.getFieldName())) {
                  title = fi.getString();
               }
            }
            
            if (file == null || file.length == 0) {
               throw new BadRequestException("No file present in upload");
            }
            if (StringUtils.isBlank(title) || title.length() > 100) {
               throw new BadRequestException("Title should be 1-100 characters long. Input:" + title);
            }
            number = dao.saveAttachment(file,title,filename);

         }
      } catch (FileUploadException e) {
         throw new BadRequestException(e);         
      }
      res.setHeader("Location", WebserviceHelper.createAttachmentBaseURL() + number);
      res.setStatus(HttpURLConnection.HTTP_CREATED);
   }

}
