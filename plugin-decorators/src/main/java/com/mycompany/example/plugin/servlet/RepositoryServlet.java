package com.mycompany.example.plugin.servlet;

import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.google.common.collect.ImmutableMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RepositoryServlet extends AbstractExampleServlet {
    private final RepositoryService repositoryService;

    public RepositoryServlet(SoyTemplateRenderer soyTemplateRenderer, RepositoryService repositoryService) {
        super(soyTemplateRenderer);
        this.repositoryService = repositoryService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get repoSlug from path
        String pathInfo = req.getPathInfo();

        String[] components = pathInfo.split("/");

        if (components.length < 3) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Repository repository = repositoryService.getBySlug(components[1], components[2]);

        if (repository == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean isSettings = false;
        if (components.length == 4 && "settings".equalsIgnoreCase(components[3])) {
            isSettings = true;
        }

        String template = isSettings ? "plugin.example.repositorySettings" : "plugin.example.repository";

        render(resp, template, ImmutableMap.<String, Object>of("repository", repository));
    }

}
