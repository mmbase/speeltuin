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
public class AllFeedsOnPageDelegate extends AbstractRestDelegate {

	public final static AllFeedsOnPageDelegate INSTANCE = new AllFeedsOnPageDelegate();

	private AllFeedsOnPageDelegate() {
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws WebserviceException, ServletException, IOException {
		res.getWriter().write(
				"Getting all feeds on a page with id "
						+ req.getAttribute(AbstractRestDelegate.PAGE_ID_ATTR)						
						+ " and content-id "
						+ req.getAttribute(AbstractRestDelegate.CONTENT_ID_ATTR)
								.toString() + "....");
		// TODO call to copa service
	}
}
