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
public class FeedOnPageDelegate extends AbstractRestDelegate {

	public final static FeedOnPageDelegate INSTANCE = new FeedOnPageDelegate();

	private FeedOnPageDelegate() {
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws WebserviceException, ServletException, IOException {
		res.getWriter().write(
				"Getting a feed with feed-id "
						+ req.getAttribute(AbstractRestDelegate.FEED_ID_ATTR)
						+ "on a page with id "
						+ req.getAttribute(AbstractRestDelegate.PAGE_ID_ATTR)
						+ " and content-id "
						+ req
								.getAttribute(
										AbstractRestDelegate.CONTENT_ID_ATTR)
								.toString() + "....");
		// TODO call to copa service
	}
}
