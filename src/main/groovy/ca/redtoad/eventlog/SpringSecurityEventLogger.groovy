/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package ca.redtoad.eventlog

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j

import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AbstractAuthenticationEvent
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.switchuser.AuthenticationSwitchUserEvent

@Slf4j
@CompileDynamic
class SpringSecurityEventLogger implements ApplicationListener<AbstractAuthenticationEvent>, LogoutHandler {

    @SuppressWarnings(['CatchRuntimeException'])
    void logAuthenticationEvent(String eventName, Authentication authentication, String remoteAddress, String switchedUsername) {
        try {
            def username = authentication?.principal?.hasProperty('username')?.getProperty(authentication?.principal) ?: authentication?.principal
            def sessionId = authentication?.details?.sessionId
            SpringSecurityEvent.withTransaction {
                new SpringSecurityEvent(
                    username: username,
                    eventName: eventName,
                    sessionId: sessionId,
                    remoteAddress: remoteAddress,
                    switchedUsername: switchedUsername).save(failOnError:true)
            }
        }
        catch (RuntimeException e) {
            log.error("error saving spring security event", e)
            throw e
        }
    }

    void onApplicationEvent(AbstractAuthenticationEvent event) {
        logAuthenticationEvent(event.getClass().simpleName, event.authentication, event.authentication?.details?.remoteAddress, switchedUsername(event))
    }

    void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logAuthenticationEvent('Logout', authentication, request.remoteHost, null)
    }

    protected String switchedUsername(AbstractAuthenticationEvent event) {
        event instanceof AuthenticationSwitchUserEvent ? event.targetUser.username : null
    }
}
