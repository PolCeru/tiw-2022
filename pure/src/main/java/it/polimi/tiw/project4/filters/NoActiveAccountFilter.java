package it.polimi.tiw.project4.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This filter checks if there is an active account before attempting a transfer and redirects to the home page if there isn't.
 */
@WebFilter(urlPatterns = {
        "/transfer",
})
public class NoActiveAccountFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession s = req.getSession(false);

        if (s != null && s.getAttribute("activeAccount") == null) {
            res.sendRedirect(request.getServletContext().getContextPath() + "/home");
            return;
        }

        chain.doFilter(request, response);
    }
}

