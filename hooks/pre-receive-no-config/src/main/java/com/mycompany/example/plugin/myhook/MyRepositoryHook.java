package com.mycompany.example.plugin.myhook;

import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.repository.*;

import javax.annotation.Nonnull;

public class MyRepositoryHook implements PreRepositoryHook<RepositoryPushHookRequest> {

    /**
     * Disables deletion of branches
     */
    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull RepositoryPushHookRequest request) {
        for (RefChange refChange : request.getRefChanges()) {
            if (refChange.getType() == RefChangeType.DELETE) {
                return RepositoryHookResult.rejected("Deletion blocked",
                        "The ref '" + refChange.getRef().getId() + "' cannot be deleted.");
            }
        }
        return RepositoryHookResult.accepted();
    }
}