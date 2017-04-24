package com.mycompany.example.plugin.myhook;

import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.repository.Ref;
import com.atlassian.bitbucket.repository.RefChange;
import com.atlassian.bitbucket.repository.RefService;
import com.atlassian.bitbucket.repository.ResolveRefRequest;

import javax.annotation.Nonnull;

public class ProtectRefHook implements PreRepositoryHook<RepositoryPushHookRequest> {

    private final RefService refService;

    public ProtectRefHook(RefService refService) {
        this.refService = refService;
    }

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull RepositoryPushHookRequest request) {
        String refId = context.getSettings().getString("ref-id");
        Ref found = refService.resolveRef(new ResolveRefRequest.Builder(request.getRepository())
                .refId(refId)
                .build());
        if (found != null) {
            for (RefChange refChange : request.getRefChanges()) {
                String changeRefId = refChange.getRef().getId();
                if (changeRefId.equals(found.getId())) {
                    return RepositoryHookResult.rejected(
                            "Protected ref", "The ref '" + changeRefId + "' cannot be altered.");
                }
            }
        }
        return RepositoryHookResult.accepted();
    }
}
