package com.mycompany.example.plugin.myhook;

import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.repository.*;
import com.atlassian.bitbucket.setting.*;

import javax.annotation.Nonnull;
import java.net.URL;
import java.util.Collection;

/**
 * Note that hooks can implement RepositorySettingsValidator directly.
 */
public class MyRepositoryHook implements AsyncPostReceiveRepositoryHook, RepositorySettingsValidator {

    /**
     * Connects to a configured URL to notify of all changes.
     */
    @Override
    public void postReceive(@Nonnull RepositoryHookContext context, @Nonnull Collection<RefChange> refChanges) {
        String url = context.getSettings().getString("url");
        if (url != null) {
            try {
                new URL(url).openConnection().getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void validate(@Nonnull Settings settings, @Nonnull SettingsValidationErrors errors,
                         @Nonnull Repository repository) {
        if (settings.getString("url", "").isEmpty()) {
            errors.addFieldError("url", "Url field is blank, please supply one");
        }
    }
}
