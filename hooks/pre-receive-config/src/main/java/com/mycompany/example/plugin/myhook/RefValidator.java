package com.mycompany.example.plugin.myhook;

import com.atlassian.bitbucket.repository.Ref;
import com.atlassian.bitbucket.repository.RefService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.setting.RepositorySettingsValidator;
import com.atlassian.bitbucket.setting.Settings;
import com.atlassian.bitbucket.setting.SettingsValidationErrors;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class RefValidator implements RepositorySettingsValidator {

    private final RefService refService;

    public RefValidator(RefService refService) {
        this.refService = refService;
    }

    @Override
    public void validate(@Nonnull Settings settings, @Nonnull SettingsValidationErrors settingsValidationErrors,
                         @Nonnull Repository repository) {
        String refId = settings.getString("ref-id", "");
        if (StringUtils.isEmpty(refId)) {
            settingsValidationErrors.addFieldError("ref-id", "The ref id must be specified");
        } else {
            Ref ref = refService.resolveRef(repository, refId);
            if (ref == null) {
                settingsValidationErrors.addFieldError("ref-id", "Failed to find the ref");
            }
        }
    }
}