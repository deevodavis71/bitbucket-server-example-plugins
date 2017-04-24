package com.mycompany.example.plugin.myhook;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.setting.*;

import javax.annotation.Nonnull;

public class ReviewerValidator implements RepositorySettingsValidator {

    @Override
    public void validate(@Nonnull Settings settings, @Nonnull SettingsValidationErrors errors,
                         @Nonnull Repository repository) {
        if (settings.getInt("reviewers", 0) == 0) {
            errors.addFieldError("reviewers", "Number of reviewers must be greater than zero");
        }
    }
}
