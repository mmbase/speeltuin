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
public class AllContentOnPageDelegate extends AbstractRestDelegate {

	public final static AllContentOnPageDelegate INSTANCE = new AllContentOnPageDelegate();

	private AllContentOnPageDelegate() {
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws WebserviceException, ServletException, IOException {
		res.getWriter().write(
				"Getting all content on a page with id "
						+ req.getAttribute(AbstractRestDelegate.PAGE_ID_ATTR)						
						+ " and on site with site-id "
						+ req.getAttribute(AbstractRestDelegate.SITE_ID_ATTR)
								.toString() + "....");
		// TODO call to copa service
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException, WebserviceException {
		res.getWriter().write(
				"Posting to all content on a page with id "						
						+ req.getAttribute(AbstractRestDelegate.PAGE_ID_ATTR)
						+ " and on site with site-id "
						+ req.getAttribute(AbstractRestDelegate.SITE_ID_ATTR)
								.toString() + "....");
		// TODO call to copa service
	}

}
