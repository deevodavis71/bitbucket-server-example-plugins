package com.mycompany.example.plugin.auth.container;

import com.atlassian.bitbucket.auth.*;
import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.user.*;
import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * {@code HttpAuthenticationHandler} that authenticates users based on the {@code REMOTE_USER} HTTP header that's set
 * by the container (Tomcat) or reverse proxy (e.g. Apache).
 */
public class RemoteUserAuthenticationHandler implements HttpAuthenticationHandler, HttpAuthenticationSuccessHandler {

    private static final String KEY_CONTAINER_AUTH_NAME = "auth.container.remote-user";
    private static final Logger log = LoggerFactory.getLogger(RemoteUserAuthenticationHandler.class);

    private final I18nService i18nService;
    private final UserService userService;

    public RemoteUserAuthenticationHandler(I18nService i18nService, UserService userService) {
        this.i18nService = i18nService;
        this.userService = userService;
    }

    @Nullable
    public ApplicationUser authenticate(@Nonnull HttpAuthenticationContext httpAuthenticationContext) {
        HttpServletRequest request = httpAuthenticationContext.getRequest();
        String remoteUser = request.getRemoteUser();
        if (StringUtils.isBlank(remoteUser)) {
            // no authenticated user provided by the container. Opt out of authentication - perhaps another auth plugin
            // can still authenticate. Could be useful to still allow form-based authentication for a separate
            // administrator user
            return null;
        }

        ApplicationUser user = userService.getUserByName(remoteUser);
        if (user != null) {
            // user has been authenticated. Record the used remoteUser on the request so it can be persisted in the
            // http session by success handler method below. Don't persist it in the session here, because the session
            // may be cleared in the case of a transparent re-authentication
            request.setAttribute(KEY_CONTAINER_AUTH_NAME, remoteUser);
        } else {
            log.info("User {} not found. Container-provided authentication failed..", request.getRemoteUser());
        }

        return user;
    }

    public void validateAuthentication(@Nonnull HttpAuthenticationContext httpAuthenticationContext) {
        HttpSession session = httpAuthenticationContext.getRequest().getSession(false);
        if (session == null) {
            // nothing to validate - the user wasn't authenticated by this authentication handler
            return;
        }

        String sessionUser = (String) session.getAttribute(KEY_CONTAINER_AUTH_NAME);
        String remoteUser = httpAuthenticationContext.getRequest().getRemoteUser();
        if (sessionUser != null && !Objects.equal(sessionUser, remoteUser)) {
            throw new ExpiredAuthenticationException(i18nService.getKeyedText("container.auth.usernamenomatch",
                    "Session username '{0}' does not match username provided by the container '{1}'",
                    sessionUser, remoteUser));
        }
    }

    public boolean onAuthenticationSuccess(@Nonnull HttpAuthenticationSuccessContext context) throws ServletException, IOException {
        // if this authentication handler was responsible for the authentication, an attribute has been stored on the
        // request. If that's the case, persist it in the session so it can be used in future authentication validations
        String authenticationUser = (String) context.getRequest().getAttribute(KEY_CONTAINER_AUTH_NAME);
        if (authenticationUser != null) {
            context.getRequest().getSession().setAttribute(KEY_CONTAINER_AUTH_NAME, authenticationUser);
        }

        return false;
    }
}
