/**
 * 
 */
package nl.kennisnet.cpb.webservice;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.kennisnet.cpb.webservice.delegate.AbstractRestDelegate;
import nl.kennisnet.cpb.webservice.delegate.AgendaDelegate;
import nl.kennisnet.cpb.webservice.delegate.AllContentOnPageDelegate;
import nl.kennisnet.cpb.webservice.delegate.AllFeedsOnPageDelegate;
import nl.kennisnet.cpb.webservice.delegate.AllPagesOnSiteDelegate;
import nl.kennisnet.cpb.webservice.delegate.AllSitesDelegate;
import nl.kennisnet.cpb.webservice.delegate.AttachmentUploadDelegate;
import nl.kennisnet.cpb.webservice.delegate.ContentEntryDelegate;
import nl.kennisnet.cpb.webservice.delegate.ContentOnPageDelegate;
import nl.kennisnet.cpb.webservice.delegate.EvenementEntryDelegate;
import nl.kennisnet.cpb.webservice.delegate.EvenementenEntryDelegate;
import nl.kennisnet.cpb.webservice.delegate.FeedOnPageDelegate;
import nl.kennisnet.cpb.webservice.delegate.ImageUploadDelegate;
import nl.kennisnet.cpb.webservice.delegate.PageDelegate;
import nl.kennisnet.cpb.webservice.delegate.PageOnFeedOnPageDelegate;
import nl.kennisnet.cpb.webservice.delegate.SearchDelegate;
import nl.kennisnet.cpb.webservice.delegate.SearchImageDelegate;
import nl.kennisnet.cpb.webservice.delegate.SiteEntryDelegate;
import nl.kennisnet.cpb.webservice.delegate.TekstblokEntryDelegate;
import nl.kennisnet.cpb.webservice.error.BadRequestException;
import nl.kennisnet.cpb.webservice.error.EntryNotFoundException;
import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.error.WebserviceNotImplementedException;
import nl.kennisnet.cpb.webservice.error.XMLError;
import nl.kennisnet.cpb.webservice.service.ContentElementTranslationService;


/**
 * The front controller for handling the requests for the REST-webservice.
 * @author dekok01
 * 
 */
public class RestWebserviceServlet extends HttpServlet {

   private static Logger log = Logging
   .getLoggerInstance(RestWebserviceServlet.class.getName());
   
   public static final String WEBSERVICE_URL = "/edit/rest/v1_0/";
   
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
	   	   
	   try {
	   res.setContentType("text/xml; charset=" + ContentElementTranslationService.ENCODING);
	   
		String[] segments = req.getPathInfo().split("/");
		AbstractRestDelegate delegate = null;
		
		if (segments.length == 2) {
			if ("sites".equals(segments[1])) {
				delegate = AllSitesDelegate.INSTANCE;
			} else if ("search".equals(segments[1])) {
				delegate = SearchDelegate.INSTANCE;
			} else if ("searchImages".equals(segments[1])) {
            delegate = SearchImageDelegate.INSTANCE;
         } else if ("tekstblok".equals(segments[1])) {
            delegate = TekstblokEntryDelegate.INSTANCE;
         } else if ("evenement".equals(segments[1])) {
            delegate = EvenementEntryDelegate.INSTANCE;
         } else if ("agenda".equals(segments[1])) {
            delegate = AgendaDelegate.INSTANCE;
         }  else if ("attachment".equals(segments[1])) {
            delegate = AttachmentUploadDelegate.INSTANCE;
         }  else if ("image".equals(segments[1])) {
            delegate = ImageUploadDelegate.INSTANCE;
         }
		} else if (segments.length == 3) {
			if ("content".equals(segments[1])) {
				delegate = ContentEntryDelegate.INSTANCE;
				handleId(req,segments[2],AbstractRestDelegate.CONTENT_ID_ATTR);
			} else if ("sites".equals(segments[1])) {
				delegate = SiteEntryDelegate.INSTANCE;
				handleId(req,segments[2],AbstractRestDelegate.SITE_ID_ATTR);
			} /*else if ("images".equals(segments[1])) {
				delegate = SearchImageDelegate.INSTANCE;
				handleId(req,segments[2],AbstractRestDelegate.IMAGE_ID_ATTR);
			} */else if ("evenement".equals(segments[1])) {
            delegate = EvenementEntryDelegate.INSTANCE;
            handleId(req, segments[2],AbstractRestDelegate.EVENEMENT_ID_ATTR);
			} else if ("tekstblok".equals(segments[1])) {
            delegate = TekstblokEntryDelegate.INSTANCE;
            handleId(req, segments[2],AbstractRestDelegate.CONTENT_ID_ATTR);
         } else if ("agenda".equals(segments[1])) {
            delegate = AgendaDelegate.INSTANCE;
            handleId(req, segments[2],AbstractRestDelegate.AGENDA_ID_ATTR);
         }
		} else if (segments.length == 4) {
			if ("sites".equals(segments[1]) && "pages".equals(segments[3])) {
				delegate = AllPagesOnSiteDelegate.INSTANCE;
				req.setAttribute(AbstractRestDelegate.CONTENT_ID_ATTR, convertToInteger(segments[2]));				
			} else if ("agenda".equals(segments[1]) && "evenement".equals(segments[3])) {
			   delegate = EvenementenEntryDelegate.INSTANCE;
            req.setAttribute(AbstractRestDelegate.AGENDA_ID_ATTR, convertToInteger(segments[2]));
			}
		} else if (segments.length == 5) {
			if ("site".equals(segments[1]) && "pages".equals(segments[3])) {
				delegate = PageDelegate.INSTANCE;
				req.setAttribute(AbstractRestDelegate.SITE_ID_ATTR, convertToInteger(segments[2]));
				handleId(req,segments[4],AbstractRestDelegate.PAGE_ID_ATTR);
			} else if ("agenda".equals(segments[1]) && "evenement".equals(segments[3])) {
			   delegate = EvenementEntryDelegate.INSTANCE;
            req.setAttribute(AbstractRestDelegate.AGENDA_ID_ATTR, convertToInteger(segments[2]));
            handleId(req,segments[4],AbstractRestDelegate.EVENEMENT_ID_ATTR);
			}
		} else if (segments.length == 6) {
			if ("sites".equals(segments[1]) && "pages".equals(segments[3]) && "content".equals(segments[5])) {
				delegate = AllContentOnPageDelegate.INSTANCE;
				req.setAttribute(AbstractRestDelegate.SITE_ID_ATTR, convertToInteger(segments[2]));
				req.setAttribute(AbstractRestDelegate.PAGE_ID_ATTR, convertToInteger(segments[4]));
			} else if ("content".equals(segments[1]) && "pages".equals(segments[3]) && "feeds".equals(segments[5])) {
				delegate = AllFeedsOnPageDelegate.INSTANCE;
				req.setAttribute(AbstractRestDelegate.CONTENT_ID_ATTR, convertToInteger(segments[2]));
				req.setAttribute(AbstractRestDelegate.PAGE_ID_ATTR, convertToInteger(segments[4]));
			}
		} else if (segments.length == 7) {
			if ("sites".equals(segments[1]) && "pages".equals(segments[3]) && "content".equals(segments[5])) {
				delegate = ContentOnPageDelegate.INSTANCE;
				req.setAttribute(AbstractRestDelegate.SITE_ID_ATTR, convertToInteger(segments[2]));
				req.setAttribute(AbstractRestDelegate.PAGE_ID_ATTR, convertToInteger(segments[4]));
				handleId(req,segments[6],AbstractRestDelegate.CONTENT_ID_ATTR);
			} else if ("content".equals(segments[1]) && "pages".equals(segments[3]) && "feeds".equals(segments[5])) {
				delegate = FeedOnPageDelegate.INSTANCE;
				req.setAttribute(AbstractRestDelegate.CONTENT_ID_ATTR, convertToInteger(segments[2]));
				req.setAttribute(AbstractRestDelegate.PAGE_ID_ATTR, convertToInteger(segments[4]));
				handleId(req,segments[6],AbstractRestDelegate.FEED_ID_ATTR);
			}
		} else if (segments.length == 9) {
			if ("content".equals(segments[1]) && "pages".equals(segments[3]) && "feeds".equals(segments[5]) && "pages".equals(segments[7])) {
				delegate = PageOnFeedOnPageDelegate.INSTANCE;
				req.setAttribute(AbstractRestDelegate.CONTENT_ID_ATTR, convertToInteger(segments[2]));
				req.setAttribute(AbstractRestDelegate.PAGE_ID_ATTR, convertToInteger(segments[4]));
				req.setAttribute(AbstractRestDelegate.FEED_ID_ATTR, convertToInteger(segments[6]));
				handleId(req,segments[8],AbstractRestDelegate.PAGE_NR_ATTR);
			}
		}
		delegateRequest(delegate, req, res);
	   }
	   catch (WebserviceNotImplementedException wniex) {
         wniex.printStackTrace();
	      res.setStatus(HttpURLConnection.HTTP_NOT_IMPLEMENTED);
         handleWebserviceException(wniex,res);  
	   }
	   catch (BadRequestException bex) {	      	   
         bex.printStackTrace();
	      res.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
         handleWebserviceException(bex,res);	      
      }	   
	   catch (EntryNotFoundException enfex) {
         enfex.printStackTrace();
	      res.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
         handleWebserviceException(enfex, res);
	   }
	   catch (WebserviceException wex) {
         wex.printStackTrace();
	      res.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
         handleWebserviceException(wex,res);
	   }
	   catch (Throwable ex) {
         ex.printStackTrace();
	      res.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);	      
	      log.error("Unexpected error in webservice:", ex);
         res.getWriter().print(new XMLError(ex).getXml());
	   }
	   finally {
	      res.getWriter().close();
	   }
	}

	
	/** Print our checked WebserviceException to the response in a certain error-xml format
	 * @param wex the checked exception that occurred
	 * @param res the response to be written to
	 */
	private void handleWebserviceException(WebserviceException wex,
         HttpServletResponse res) {
	   try {
         if (wex.getCause() == null) {
            log.error("Error in webservice:" + wex.getMessage());
            res.getWriter().print(new XMLError(wex.getMessage()).getXml());
         } else {
            log.error("Error in webservice:" + wex.getCause());
            res.getWriter().print(new XMLError(wex.getCause()).getXml());
         }
      } catch (Exception ex) {
         log.error("ERROR: Strange exception in response");
      }      
   }
	
	/** Converts an identifier in the REST URL to an integer. 
	 * @param s the identifier to be converted
	 * @return the integer representation
	 * @throws WebserviceException If the identifier is not numeric
	 */
	private Integer convertToInteger(String s) throws WebserviceException {
	   try {
	      return Integer.parseInt(s);
	   } catch (NumberFormatException nfe) {
	      throw new BadRequestException("Rest URL expects only numeric identifiers, not:" + s);
	   }
	}


   /**
	 * Utility for handling id's in REST-url. Id's at the end of the URL may
	 * require splitting id and desired representation. After the splitting
	 * process, put both values separately in request attributes For example,
	 * 5.html would be split into 5 and html and request attributes with name as
	 * specified in parameter 'attr' and
	 * AbstractRestDelegate.CONTENT_REPRESENTATION
	 * 
	 * @param req
	 * @param idStr
	 * @param attr
	 */
	private void handleId(HttpServletRequest req, String idStr, String attrName) throws WebserviceException {
		String[] ss = idStr.split("\\.");				
		req.setAttribute(attrName, convertToInteger(ss[0]));
		if (ss.length > 1) {
			req
					.setAttribute(AbstractRestDelegate.CONTENT_REPRESENTATION,
							ss[1]);
		}
	}

	private void delegateRequest(AbstractRestDelegate delegate,
			HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException, WebserviceException {
		String method = req.getMethod();

		if ("GET".equals(method)) {
			delegate.doGet(req, res);
		} else if ("POST".equals(method)) {
			delegate.doPost(req, res);
		} else if ("PUT".equals(method)) {
			delegate.doPut(req, res);
		} else if ("DELETE".equals(method)) {
			delegate.doDelete(req, res);
		}
	}
}
