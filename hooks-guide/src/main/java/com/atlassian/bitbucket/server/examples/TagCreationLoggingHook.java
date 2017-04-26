package com.atlassian.bitbucket.server.examples;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.event.tag.TagCreationHookRequest;
import com.atlassian.bitbucket.hook.repository.PostRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PostRepositoryHookContext;
import com.atlassian.bitbucket.repository.Tag;
import com.atlassian.bitbucket.user.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * Hook that logs who created a new tag through the REST API
 */
public class TagCreationLoggingHook implements PostRepositoryHook<TagCreationHookRequest> {

    private static final Logger log = LoggerFactory.getLogger(TagCreationLoggingHook.class);

    private final AuthenticationContext authenticationContext;

    public TagCreationLoggingHook(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    @Override
    public void postUpdate(@Nonnull PostRepositoryHookContext context,
                           @Nonnull TagCreationHookRequest request) {
        ApplicationUser user = authenticationContext.getCurrentUser();
        String username = user != null ? user.getName() : "<unknown>";
        Tag tag = request.getTag();
        log.info("[{}] {} created a new tag: {}, which references {}",
                request.getRepository(), username, tag.getDisplayId(), tag.getLatestCommit());
    }
}
