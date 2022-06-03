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
public class AllPagesOnSiteDelegate extends AbstractRestDelegate {

	public final static AllPagesOnSiteDelegate INSTANCE = new AllPagesOnSiteDelegate();

	private AllPagesOnSiteDelegate() {
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws WebserviceException, ServletException, IOException {
		res.getWriter().write(
				"Getting all pages on site with id "
						+ req.getAttribute(AbstractRestDelegate.SITE_ID_ATTR)
								.toString() + "....");
		// TODO call to copa service
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException, WebserviceException {
		res.getWriter().write(
				"Posting to all pages on site with id "
						+ req.getAttribute(AbstractRestDelegate.SITE_ID_ATTR)
								.toString() + "....");
	}

}
