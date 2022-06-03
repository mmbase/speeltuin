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
public class PageOnFeedOnPageDelegate extends AbstractRestDelegate {

	public final static PageOnFeedOnPageDelegate INSTANCE = new PageOnFeedOnPageDelegate();

	private PageOnFeedOnPageDelegate() {
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws WebserviceException, ServletException, IOException {
		res
				.getWriter()
				.write(
						"Getting a page from a feed with feed-id "
								+ req
										.getAttribute(AbstractRestDelegate.FEED_ID_ATTR)
								+ "on a page with id "
								+ req
										.getAttribute(AbstractRestDelegate.PAGE_ID_ATTR)
								+ " and content-id "
								+ req
										.getAttribute(AbstractRestDelegate.CONTENT_ID_ATTR)
								+ " with page number "
								+ req.getAttribute(
										AbstractRestDelegate.PAGE_NR_ATTR)
										.toString() + "....");
		// TODO call to copa service
	}
}
