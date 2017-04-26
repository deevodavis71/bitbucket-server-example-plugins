package com.atlassian.bitbucket.server.examples;

import com.atlassian.bitbucket.hook.repository.PreRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookRequest;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestSearchRequest;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.atlassian.bitbucket.pull.PullRequestState;
import com.atlassian.bitbucket.repository.RefChangeType;
import com.atlassian.bitbucket.util.Page;
import com.atlassian.bitbucket.util.PageUtils;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;

public class BranchInReviewHook implements PreRepositoryHook<RepositoryHookRequest> {

    private final I18nService i18nService;
    private final PullRequestService pullRequestService;

    public BranchInReviewHook(I18nService i18nService, PullRequestService pullRequestService) {
        this.i18nService = i18nService;
        this.pullRequestService = pullRequestService;
    }

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull RepositoryHookRequest request) {

        // Find all refs that are about to be deleted
        Set<String> deletedRefIds = request.getRefChanges().stream()
                .filter(refChange -> refChange.getType() == RefChangeType.DELETE)
                .map(refChange -> refChange.getRef().getId())
                .collect(Collectors.toSet());

        if (deletedRefIds.isEmpty()) {
            // nothing is going to be deleted, no problem
            return RepositoryHookResult.accepted();
        }

        // verify whether any of the refs are already in review
        PullRequestSearchRequest searchRequest = new PullRequestSearchRequest.Builder()
                .state(PullRequestState.OPEN)
                .fromRefIds(deletedRefIds)
                .fromRepositoryId(request.getRepository().getId())
                .build();

        Page<PullRequest> found = pullRequestService.search(searchRequest, PageUtils.newRequest(0, 1));
        if (found.getSize() > 0) {
            // found at least 1 open pull request from one of the refs that are about to be deleted
            PullRequest pullRequest = found.getValues().iterator().next();
            return RepositoryHookResult.rejected(
                    i18nService.getMessage("hook.guide.branchinreview.summary"),
                    i18nService.getMessage("hook.guide.branchinreview.details",
                            pullRequest.getFromRef().getDisplayId(), pullRequest.getId()));
        }

        return RepositoryHookResult.accepted();
    }
}
