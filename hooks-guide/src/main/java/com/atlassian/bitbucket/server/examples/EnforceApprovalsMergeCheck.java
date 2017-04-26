package com.atlassian.bitbucket.server.examples;

import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.PullRequestMergeHookRequest;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.RepositoryMergeCheck;
import com.atlassian.bitbucket.pull.PullRequestParticipant;

import javax.annotation.Nonnull;

public class EnforceApprovalsMergeCheck implements RepositoryMergeCheck {

    /**
     * Vetoes a pull-request if there aren't enough approvals.
     */
    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull PullRequestMergeHookRequest request) {
        int requiredApprovals = context.getSettings().getInt("approvals", 0);
        int acceptedCount = 0;
        for (PullRequestParticipant reviewer : request.getPullRequest().getReviewers()) {
            acceptedCount = acceptedCount + (reviewer.isApproved() ? 1 : 0);
        }
        if (acceptedCount < requiredApprovals) {
            return RepositoryHookResult.rejected("Not enough approved reviewers", acceptedCount +
                    " reviewers have approved your pull request. You need " + requiredApprovals +
                    " (total) before you may merge.");
        }
        return RepositoryHookResult.accepted();
    }
}
