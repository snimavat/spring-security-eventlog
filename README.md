[![CircleCI](https://img.shields.io/circleci/project/github/yakworks/spring-security-eventlog/master.svg?longCache=true&style=for-the-badge&logo=circleci)](https://circleci.com/gh/yakworks/view-tools)
[![9ci](https://img.shields.io/badge/BUILT%20BY-9ci%20Inc-blue.svg?longCache=true&style=for-the-badge)](http://9ci.com)
<img src="https://forthebadge.com/images/badges/built-with-love.svg" height="28">
[![forthebadge](https://forthebadge.com/images/badges/made-with-groovy.svg)](https://forthebadge.com)
<img src="https://forthebadge.com/images/badges/gluten-free.svg" height="28">
[![forthebadge](https://forthebadge.com/images/badges/approved-by-george-costanza.svg)](https://forthebadge.com)


Spring Security Eventlog Plugin
===============================

Installation
-----------

```groovy

compile "org.grails.plugins.spring-security-eventlog:3.0.3"

```


This plugin creates a simple log of spring security events.  Each time
a user logs in or logs out, a log entry will created, storing the
remote address, session id, user name, event name, switched user name,
and the time at which the event occurred.

Events are logged to a table named SPRING_SECURITY_EVENT, mapped to a
domain object ca.redtoad.eventlog.SpringSecurityEvent.

Each event has the following fields:

* username - the username entered
* sessionId - the user's session
* eventName - the name of the event
* remoteAddress - the user's IP address
* switchedUsername - username that is being switched to
* dateCreated - the event's timestamp

Some of the event names that are captured:

* AuthenticationFailureBadCredentialsEvent - a bad username or password
* AuthenticationSuccessEvent - a successful login
* InteractiveAuthenticationSuccessEvent - a successful login where the user entered his/her username and password
* AuthenticationSwitchUserEvent - user having ROLE_SWITCH_USER has assumed the identity of a (likely different) user
* Logout - a user logged out interactively


Customizing
-----------

You can specify your own logger if you would like to override how
events get logged.  Create a subclass of SpringSecurityEventLogger and
add your custom behavior to logAuthenticationEvent.  For example:

    package mypackage
    
    import ca.redtoad.eventlog.SpringSecurityEventLogger
    import org.springframework.security.core.Authentication
    
    class CustomEventLogger extends SpringSecurityEventLogger {
        void logAuthenticationEvent(String eventName, Authentication authentication, String remoteAddress, String switchedUsername) {
            println "$eventName! $authentication from $remoteAddress"
        }
    }

In your `Config.groovy`, tell grails to your own event logger class:

    grails.plugin.springsecurity.eventlog.eventLogger = mypackage.CustomEventLogger


Changelog
---------

* Changes in spring-security-eventlog 0.4
   * cleaned up plugin (thanks burtbeckwith!)
   * updated build dependencies to latest versions

* Changes in spring-security-eventlog 0.3
   * added logging of 'switched user name' when a AuthenticationSwitchUserEvent occurs
   * small cleanup of plugin definition

* Changes in spring-security-eventlog 0.2

    * fix exception on anonymous logout
    * changed logAuthenticationEvent signature to take remoteAddress
      directly rather than pulling it out of authentication.details
