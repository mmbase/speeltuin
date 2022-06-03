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
public class AllSitesDelegate extends AbstractRestDelegate {

	public final static AllSitesDelegate INSTANCE = new AllSitesDelegate();

	private AllSitesDelegate() {
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws WebserviceException, ServletException, IOException {
		res.getWriter().write("Getting all sites....");
		// TODO call to copa service
	}

}
