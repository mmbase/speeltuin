/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;


/**

 * @author Michiel Meeuwissen
 */
public class JettyTestServlet extends HttpServlet {


    /**
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.getOutputStream();
        req.setAttribute("bloe", "bla");
        res.getOutputStream().write("<!-- test -->".getBytes());
        res.setContentType("text/xml");

        HttpServletResponseWrapper wrapper = new GenericResponseWrapper(res) {
            @Override
            public void setStatus(int s) {
            }
            @Override
            public void sendError(int s) throws IOException {
            }
            @Override
            public void sendError(int s, String m) throws IOException {
            }
            };

        RequestDispatcher dispatcher = req.getRequestDispatcher("/dispatched.jspx");
        dispatcher.include(req, wrapper);

        res.getOutputStream().write(wrapper.toString().getBytes());
    }

}

