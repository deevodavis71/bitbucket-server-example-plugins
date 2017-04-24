package com.mycompany.example.plugin.servlet;

import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.UserService;
import com.google.common.collect.ImmutableMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProfileServlet extends AbstractExampleServlet {
    private final UserService userService;

    public ProfileServlet(SoyTemplateRenderer soyTemplateRenderer, UserService userService) {
        super(soyTemplateRenderer);
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get userSlug from path
        String pathInfo = req.getPathInfo();

        String userSlug = pathInfo.substring(1); // Strip leading slash
        ApplicationUser user = userService.getUserBySlug(userSlug);

        if (user == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        render(resp, "plugin.example.profile", ImmutableMap.<String, Object>of("user", user));
    }
}
