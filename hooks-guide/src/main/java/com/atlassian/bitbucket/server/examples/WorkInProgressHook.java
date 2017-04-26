package com.atlassian.bitbucket.server.examples;

import com.atlassian.bitbucket.commit.Commit;
import com.atlassian.bitbucket.hook.repository.*;

import javax.annotation.Nonnull;

/**
 * Hook that blocks any newly introduced commits that have "Work in progress" in the commit message
 */
public class WorkInProgressHook implements PreRepositoryHook<RepositoryHookRequest> {
    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull RepositoryHookRequest request) {

        // hook only wants commits added to the repository
        context.registerCommitCallback(
                new WorkInProgressCallback(),
                RepositoryHookCommitFilter.ADDED_TO_REPOSITORY);

        // return accepted() here, the callback gets a chance to reject the change when getResult() is called
        return RepositoryHookResult.accepted();
    }

    private static class WorkInProgressCallback implements PreRepositoryHookCommitCallback {

        private RepositoryHookResult result = RepositoryHookResult.accepted();

        @Nonnull
        @Override
        public RepositoryHookResult getResult() {
            return result;
        }

        @Override
        public boolean onCommitAdded(@Nonnull CommitAddedDetails commitDetails) {
            Commit commit = commitDetails.getCommit();
            String message = commit.getMessage().toLowerCase();
            if (message.startsWith("work in progress")) {
                // use the i18nService to internationalise the messages for public plugins
                // (that are published to the marketplace)
               result = RepositoryHookResult.rejected(
                        "Don't push 'in progress' commits!",
                        "Offending commit " + commit.getId() + " on " + commitDetails.getRef().getDisplayId());
                // this will block the change, so no need to inspect further commits
                return false;
            }

            return true;
        }
    }
}
