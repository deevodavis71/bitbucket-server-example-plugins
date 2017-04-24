package com.mycompany.example.plugin.servlet;

import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.google.common.collect.ImmutableMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PullRequestServlet extends AbstractExampleServlet {
    private final RepositoryService repositoryService;
    private final PullRequestService pullRequestService;

    public PullRequestServlet(SoyTemplateRenderer soyTemplateRenderer, RepositoryService repositoryService, PullRequestService pullRequestService) {
        super(soyTemplateRenderer);
        this.repositoryService = repositoryService;
        this.pullRequestService = pullRequestService;
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

        PullRequest pullRequest;
        try {
            int pullRequestId = Integer.parseInt(components[3]);
            pullRequest = pullRequestService.getById(repository.getId(), pullRequestId);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (pullRequest == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String template = "plugin.example.pullRequest";

        render(resp, template, ImmutableMap.<String, Object>of("pullRequest", pullRequest));
    }

}
