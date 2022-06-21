package it.polimi.tiw.project4.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This filter checks if the user is not logged in and redirects to the index page.
 */
@WebFilter(urlPatterns = {
        "/home",
        "/account",
        "/do_transfer",
        "/transfer",
        "/get_account",
        "/add_to_book",
        "/account_book"
})
public class NotLoggedInFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession s = req.getSession(false);

        if (s == null || s.getAttribute("currentUser") == null) {
            res.sendRedirect(request.getServletContext().getContextPath());
            return;
        }

        chain.doFilter(request, response);
    }
}
