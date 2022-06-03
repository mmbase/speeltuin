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
public class PageDelegate extends AbstractRestDelegate {

	public final static PageDelegate INSTANCE = new PageDelegate();

	private PageDelegate() {
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws WebserviceException, ServletException, IOException {
		res.getWriter().write(
				"Getting page document on site with site-id "
						+ req.getAttribute(AbstractRestDelegate.SITE_ID_ATTR) + " and pages-id " + req.getAttribute(AbstractRestDelegate.PAGE_ID_ATTR)
								.toString() + "....");
		// TODO call to copa service
	}

	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException, WebserviceException {
		res.getWriter().write(
				"Deleting page document on site with site-id "
						+ req.getAttribute(AbstractRestDelegate.SITE_ID_ATTR) + " and pages-id " + req.getAttribute(AbstractRestDelegate.PAGE_ID_ATTR)
								.toString() + "....");
		// TODO call to copa service
	}

	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException, WebserviceException {
		res.getWriter().write(
				"Putting page document on site with site-id "
						+ req.getAttribute(AbstractRestDelegate.SITE_ID_ATTR) + " and pages-id " + req.getAttribute(AbstractRestDelegate.PAGE_ID_ATTR)
								.toString() + "....");
		// TODO call to copa service
	}

}
