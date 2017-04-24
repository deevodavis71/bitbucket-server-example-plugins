package com.mycompany.example.plugin.myhook;

import com.atlassian.bitbucket.hook.*;
import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.repository.*;

import javax.annotation.Nonnull;
import java.util.Collection;

public class MyRepositoryHook implements PreReceiveRepositoryHook {

    /**
     * Disables deletion of branches
     */
    @Override
    public boolean onReceive(@Nonnull RepositoryHookContext context,
                             @Nonnull Collection<RefChange> refChanges,
                             @Nonnull HookResponse hookResponse) {
        for (RefChange refChange : refChanges) {
            if (refChange.getType() == RefChangeType.DELETE) {
                hookResponse.err().println("The ref '" + refChange.getRefId() + "' cannot be deleted.");
                return false;
            }
        }
        return true;
    }
}