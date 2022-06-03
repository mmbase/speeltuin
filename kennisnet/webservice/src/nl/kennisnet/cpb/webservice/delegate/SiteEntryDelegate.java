/**
 * 
 */
package nl.kennisnet.cpb.webservice.delegate;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.kennisnet.cpb.webservice.error.WebserviceException;

/**
 * @author dekok01
 *
 */
public class SiteEntryDelegate extends AbstractRestDelegate {

	public final static SiteEntryDelegate INSTANCE = new SiteEntryDelegate(); 
	
	private SiteEntryDelegate () {}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws WebserviceException, ServletException, IOException {
		res.getWriter().write("Getting site entry with id " + req.getAttribute(AbstractRestDelegate.SITE_ID_ATTR).toString() + "....");
		//TODO call to copa service
	}
	
	

}
