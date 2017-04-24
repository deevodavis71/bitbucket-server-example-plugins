# Example Bitbucket Server plugin for custom authentication (SSO / Kerberos)

This plugin provides a HttpAuthenticationHandler implementation that attempts to authentication the container-provided
remoteUser (`HttpServletRequest.getRemoteUser`). If the remote user is set and a user with that name can be found,
authentication succeeds. If no remote user is set or the user is not found, the authenticator opts out of authentication
and allows other authentication plugins to authenticate the user.

# License

This example is licensed under the Apache License, Version 2

