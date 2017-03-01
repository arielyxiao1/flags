/* 
 * @author Yat Rehani
 * Last modified: 9/23/2016
 * This is a short example of MVC.
 * The welcome-file in the deployment descriptor (web.xml) points
 * to this servlet.  So it is also the starting place for the web
 * application.
 *
 * The servlet is acting as the controller.
 * There are two views - prompt.jsp and result.jsp.
 * It decides between the two by determining if there is a search parameter or
 * not. If there is no parameter, then it uses the prompt.jsp view, as a 
 * starting place. If there is a search parameter, then it searches for a 
 * picture and uses the result.jsp view.
 * The model is provided by FlagModel.
 */
package flag;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * The following WebServlet annotation gives instructions to the web container.
 * It states that when the user browses to the URL path /getAFlag
 * then the servlet with the name InterestingPictureServlet should be used.
 */
@WebServlet(name = "FlagServlet",
        urlPatterns = {"/FlagServlet"})
public class FlagServlet extends HttpServlet {

    FlagModel fm = null;  // The "business model" for this app

    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        fm = new FlagModel();
    }

    // This servlet will reply to HTTP GET requests via this doGet method
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // get the search parameter if it exists
        String search = request.getParameter("country");

        String nextView;
        /*
         * Check if the search parameter is present.
         * If not, then give the user instructions and prompt for a search string.
         * If there is a search parameter, then do the search and return the result.
         */
        if (search != null) {
            // use model to do the search and choose the result view
            fm.doFlagSearch(search);
            /*
             * Attributes on the request object can be used to pass data to
             * the view.  These attributes are name/value pairs, where the name
             * is a String object.
             */
            request.setAttribute("flagImageURL",fm.getFlagImageUrl());
            request.setAttribute("flagDescription", fm.getFlagDescription());
            request.setAttribute("flagCountryName", fm.getFlagCountryName());
            nextView = "result.jsp";
        } else {
            // no search parameter so choose the prompt view
            nextView = "prompt.jsp";
        }
        // Transfer control over the the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }
}
