package com.mycompany.example.plugin.myhook;

import com.atlassian.bitbucket.hook.HookResponse;
import com.atlassian.bitbucket.hook.repository.PreReceiveRepositoryHook;
import com.atlassian.bitbucket.hook.repository.RepositoryHookContext;
import com.atlassian.bitbucket.repository.Ref;
import com.atlassian.bitbucket.repository.RefChange;
import com.atlassian.bitbucket.repository.RefService;

import javax.annotation.Nonnull;
import java.util.Collection;

public class ProtectRefHook implements PreReceiveRepositoryHook {

    private final RefService refService;

    public ProtectRefHook(RefService refService) {
        this.refService = refService;
    }


    /**
     * Disables changes to a ref
     */
    @Override
    public boolean onReceive(@Nonnull RepositoryHookContext context,
                             @Nonnull Collection<RefChange> refChanges,
                             @Nonnull HookResponse hookResponse) {
        String refId = context.getSettings().getString("ref-id");
        Ref found = refService.resolveRef(context.getRepository(), refId);
        if (found != null) {
            for (RefChange refChange : refChanges) {
                String changeRefId = refChange.getRef().getId();
                if (changeRefId.equals(found.getId())) {
                    hookResponse.err().println("The ref '" + changeRefId + "' cannot be altered.");
                    return false;
                }
            }
        }

        return true;
    }
}
