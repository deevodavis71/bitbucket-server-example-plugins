package com.atlassian.bitbucket.server.examples;

import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Hook that logs when a user performs a force-push
 */
public class ForcePushLoggingHook implements PostRepositoryHook<RepositoryPushHookRequest> {

    private static final Logger log = LoggerFactory.getLogger(ForcePushLoggingHook.class);

    @Override
    public void postUpdate(@Nonnull PostRepositoryHookContext context,
                           @Nonnull RepositoryPushHookRequest request) {

        // Only an UPDATE can be a force push, so ignore ADD and DELETE changes
        Map<String, RefChange> updates = request.getRefChanges().stream()
                .filter(refChange -> refChange.getType() == RefChangeType.UPDATE)
                .collect(Collectors.toMap(
                        refChange -> refChange.getRef().getId(),
                        refChange -> refChange));

        if (!updates.isEmpty()) {
            // register a callback to receive any commit that was removed from any ref. This hook is not interested
            // in newly introduced commits, so it only registers for removed commits.
            context.registerCommitCallback(
                    new ForcePushDetectingCallback(request.getRepository(), updates),
                    RepositoryHookCommitFilter.REMOVED_FROM_ANY_REF);
        }
    }

    private static class ForcePushDetectingCallback implements RepositoryHookCommitCallback {

        private final Repository repository;
        private final Map<String, RefChange> refChangeById;
        private final Set<RefChange> forcePushes;

        private ForcePushDetectingCallback(Repository repository, Map<String, RefChange> refChangeById) {
            this.repository = repository;
            this.refChangeById = refChangeById;

            forcePushes = new HashSet<>();
        }

        @Override
        public boolean onCommitRemoved(@Nonnull CommitRemovedDetails commitDetails) {
            // The callback may be called for a commit that was removed from the repository when a branch is deleted.
            // Check whether the provided ref is updated before logging
            MinimalRef ref = commitDetails.getRef();
            // Remove the change because each change should be logged once, even if multiple commits were removed
            RefChange change = refChangeById.remove(ref.getId());
            if (change != null) {
                forcePushes.add(change);
            }
            // The hook only needs to receive more commits if there are RefChanges that have not yet been logged
            return !refChangeById.isEmpty();
        }

        // onEnd is called after the last commit has been provided
        @Override
        public void onEnd() {
            forcePushes.forEach(change ->
                    log.warn("[{}] {} was force pushed from {} to {}", repository,
                            change.getRef().getDisplayId(), change.getFromHash(), change.getToHash()));
        }
    }
}
