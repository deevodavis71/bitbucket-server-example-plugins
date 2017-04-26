package com.atlassian.bitbucket.server.examples;

import com.atlassian.bitbucket.hook.repository.PostRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PostRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryPushHookRequest;
import com.atlassian.bitbucket.hook.repository.SynchronousPreferred;
import com.atlassian.bitbucket.repository.RefChangeType;

import javax.annotation.Nonnull;

/**
 * Hook that writes a warning to the git client when it detects a push to the master branch
 */
@SynchronousPreferred(asyncSupported = false)
public class PushToMasterWarnHook implements PostRepositoryHook<RepositoryPushHookRequest> {

    @Override
    public void postUpdate(@Nonnull PostRepositoryHookContext context,
                           @Nonnull RepositoryPushHookRequest request) {
        request.getScmHookDetails().ifPresent(scmDetails -> {
            request.getRefChanges().forEach(refChange -> {
                if ("refs/heads/master".equals(refChange.getRef().getId()) &&
                        refChange.getType() == RefChangeType.UPDATE) {
                    scmDetails.out().println("You should create a pull request and " +
                            "get some input from your team mates!");
                }
            });
        });
    }
}
