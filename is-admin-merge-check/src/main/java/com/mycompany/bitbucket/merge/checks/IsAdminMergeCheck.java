package com.mycompany.bitbucket.merge.checks;

import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component("isAdminMergeCheck")
public class IsAdminMergeCheck implements RepositoryMergeCheck {

    private final I18nService i18nService;
    private final PermissionService permissionService;

    @Autowired
    public IsAdminMergeCheck(@ComponentImport I18nService i18nService,
                             @ComponentImport PermissionService permissionService) {
        this.i18nService = i18nService;
        this.permissionService = permissionService;
    }

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull PullRequestMergeHookRequest request) {
        Repository repository = request.getPullRequest().getToRef().getRepository();
        if (!permissionService.hasRepositoryPermission(repository, Permission.REPO_ADMIN)) {
            String summaryMsg = i18nService.getMessage("mycompany.plugin.merge.check.notrepoadmin.summary",
                    "Only repository administrators may merge pull requests");
            String detailedMsg = i18nService.getText("mycompany.plugin.merge.check.notrepoadmin.detailed",
                    "The user merging the pull request must be an administrator of the target repository");
            return RepositoryHookResult.rejected(summaryMsg, detailedMsg);
        }
        return RepositoryHookResult.accepted();
    }
}
