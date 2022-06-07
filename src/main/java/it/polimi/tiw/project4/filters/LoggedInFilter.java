package it.polimi.tiw.project4.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This filter checks if the user is logged in and redirects to the home page.
 */
public class LoggedInFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession s = req.getSession(false);

        if (s != null) {
            Object user = s.getAttribute("currentUser");
            if (user != null) {
                res.sendRedirect(request.getServletContext().getContextPath() + "/home");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
