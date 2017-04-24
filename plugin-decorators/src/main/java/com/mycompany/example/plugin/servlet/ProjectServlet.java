package com.mycompany.example.plugin.servlet;

import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.google.common.collect.ImmutableMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProjectServlet extends AbstractExampleServlet {
    private final ProjectService projectService;

    public ProjectServlet(SoyTemplateRenderer soyTemplateRenderer, ProjectService projectService) {
        super(soyTemplateRenderer);
        this.projectService = projectService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get projectKey from path
        String pathInfo = req.getPathInfo();

        String[] components = pathInfo.split("/");

        if (components.length < 2) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Project project = projectService.getByKey(components[1]);

        if (project == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean isSettings = false;
        if (components.length == 3 && "settings".equalsIgnoreCase(components[2])) {
            isSettings = true;
        }

        String template = isSettings ? "plugin.example.projectSettings" : "plugin.example.project";

        render(resp, template, ImmutableMap.<String, Object>of("project", project));
    }
}
