package com.mycompany.example.plugin.myhook;

import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.pull.PullRequestParticipant;

import javax.annotation.Nonnull;

public class MyRepositoryHook implements RepositoryMergeRequestCheck {

    /**
     * Vetos a pull-request if there aren't enough reviewers.
     */
    @Override
    public void check(@Nonnull RepositoryMergeRequestCheckContext context) {
        int requiredReviewers = context.getSettings().getInt("reviewers", 0);
        int acceptedCount = 0;
        for (PullRequestParticipant reviewer : context.getMergeRequest().getPullRequest().getReviewers()) {
            acceptedCount = acceptedCount + (reviewer.isApproved() ? 1 : 0);
        }
        if (acceptedCount < requiredReviewers) {
            context.getMergeRequest().veto("Not enough approved reviewers", acceptedCount +
                    " reviewers have approved your pull request. You need " + requiredReviewers +
                    " (total) before you may merge.");
        }
    }
}
