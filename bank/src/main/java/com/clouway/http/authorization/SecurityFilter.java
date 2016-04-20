package com.clouway.http.authorization;

import com.clouway.core.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;

/**
 * @author Slavi Dichkov (slavidichkof@gmail.com)
 */
public class SecurityFilter implements Filter {
    private final HashSet<String> allowedPages;
    private CurrentUserProvider currentUserProvider = DependencyManager.getDependency(CurrentUserProvider.class);

    public SecurityFilter(HashSet<String> allowedPages) {
        this.allowedPages = allowedPages;
    }

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri=req.getRequestURI();
        if (uri.equals("/")){
            chain.doFilter(request, response);
            return;
        }

        String endpoint=uri.split("/")[1];
        if (allowedPages.contains(endpoint)){
            chain.doFilter(request, response);
            return;
        }

        CurrentUser currentUser = currentUserProvider.get(new CookieSessionFinder(req.getCookies()));

        if (currentUser.getUser().isPresent()) {
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect("/login");
        }
    }

    public void destroy() {

    }
}
