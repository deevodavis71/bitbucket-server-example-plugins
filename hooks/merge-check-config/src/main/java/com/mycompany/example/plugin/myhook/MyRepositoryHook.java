package com.mycompany.example.plugin.myhook;

import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.pull.PullRequestParticipant;

import javax.annotation.Nonnull;

public class MyRepositoryHook implements PreRepositoryHook<PullRequestMergeHookRequest> {

    /**
     * Vetoes a pull-request if there aren't enough reviewers.
     */
    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull PullRequestMergeHookRequest request) {
        int requiredReviewers = context.getSettings().getInt("reviewers", 0);
        int acceptedCount = 0;
        for (PullRequestParticipant reviewer : request.getPullRequest().getReviewers()) {
            acceptedCount = acceptedCount + (reviewer.isApproved() ? 1 : 0);
        }
        if (acceptedCount < requiredReviewers) {
            return RepositoryHookResult.rejected("Not enough approved reviewers", acceptedCount +
                    " reviewers have approved your pull request. You need " + requiredReviewers +
                    " (total) before you may merge.");
        }
        return RepositoryHookResult.accepted();
    }
}
