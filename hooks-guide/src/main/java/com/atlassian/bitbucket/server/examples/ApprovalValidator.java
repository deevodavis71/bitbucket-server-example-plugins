package com.atlassian.bitbucket.server.examples;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.setting.RepositorySettingsValidator;
import com.atlassian.bitbucket.setting.Settings;
import com.atlassian.bitbucket.setting.SettingsValidationErrors;

import javax.annotation.Nonnull;

public class ApprovalValidator implements RepositorySettingsValidator {

    @Override
    public void validate(@Nonnull Settings settings, @Nonnull SettingsValidationErrors errors,
                         @Nonnull Repository repository) {
        try {
            if (settings.getInt("approvals", 0) <= 0) {
                errors.addFieldError("approvals", "Number of approvals must be greater than zero");
            }
        } catch (NumberFormatException e) {
            errors.addFieldError("approvals", "Number of approvals must be a number");
        }
    }
}
