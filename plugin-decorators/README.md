# Bitbucket Server plugin decorating tutorials

This is the Atlassian Bitbucket Server example plugin for decorating various pages around Bitbucket Server.
See the [Bitbucket Server developer documentation](https://developer.atlassian.com/bitbucket/server/docs/latest/reference/plugin-decorators.html)
for a list of the available decorators in Bitbucket Server 4.x.

----

## Project decorator

Add a tab with content to the project page.

![project](src/main/resources/examples/project-decorator.png)

----

## Repository decorator

Add a tab with content to the repository page.

![repo](src/main/resources/examples/repository-decorator.png)

Repository settings page

![repo-settings](src/main/resources/examples/repository-settings-decorator.png)

----

## User profile decorator

[User profile tutorial](https://developer.atlassian.com/bitbucket/server/docs/latest/tutorials-and-examples/decorating-the-user-profile.html) – add content to the user profile page.

![profile](src/main/resources/examples/user-profile-decorator.png)

----

## User account decorator

[User account tutorial](https://developer.atlassian.com/bitbucket/server/docs/latest/tutorials-and-examples/decorating-the-user-account.html) – add content to the user account page.

![account](src/main/resources/examples/user-profile-decorator.png)

----

Here are the SDK commands you'll use immediately:

* `atlas-run`   -- installs this plugin into the product and starts it on localhost
* `atlas-debug` -- same as atlas-run, but allows a debugger to attach at port 5005
* `atlas-cli`   -- after atlas-run or atlas-debug, opens a Maven command line window:
                 - `pi` reinstalls the plugin into the running product instance
* `atlas-help`  -- prints description for all commands in the SDK

Full documentation is always available at:

https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK
