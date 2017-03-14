package ca.redtoad.eventlog

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.junit.Test
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.switchuser.AuthenticationSwitchUserEvent
import spock.lang.Specification

@Integration
@Rollback
class SpringSecurityEventLoggerTests extends Specification {

    SpringSecurityEventLogger logger = new SpringSecurityEventLogger()


    void testLogAuthenticationEventWithNullAuthentication() {
        when:
        logger.logAuthenticationEvent("event", null, "127.0.0.1", null)

        then:
        SpringSecurityEvent.count() == 1
        def event = SpringSecurityEvent.list().first()
        event.username == null
        event.sessionId == null
        event.eventName == "event"
        event.switchedUsername == null
        event.remoteAddress == "127.0.0.1"
    }


    void testLogAuthenticationEventWithStringPrincipal() {
        def authentication = new TestingAuthenticationToken("username", [])
        logger.logAuthenticationEvent("event", authentication, "127.0.0.1", null)

        assert SpringSecurityEvent.count() == 1
        def event = SpringSecurityEvent.list().first()
        assert event.username == "username"
        assert event.sessionId == null
        assert event.eventName == "event"
        assert event.switchedUsername == null
        assert event.remoteAddress == "127.0.0.1"
    }


    void testLogAuthenticationEventWithUserDetailsPrincipal() {
        when:
        def principal = { -> "username" } as UserDetails
        def authentication = new TestingAuthenticationToken(principal, [])
        logger.logAuthenticationEvent("event", authentication, "127.0.0.1", null)

        then:
        SpringSecurityEvent.count() == 1
        def event = SpringSecurityEvent.list().first()
        event.username == "username"
        event.sessionId == null
        event.eventName == "event"
        event.switchedUsername == null
        event.remoteAddress == "127.0.0.1"
    }

    void testLogAuthenticationSwitchUserEvent() {
        when:
        def principal = { -> "username" } as UserDetails
        def authentication = new TestingAuthenticationToken(principal, [])
        authentication.details = [remoteAddress: '127.0.0.1', sessionId: 'mockSessionId']
        def targetUser = { -> "switchedUsername" } as UserDetails

        logger.onApplicationEvent(new AuthenticationSwitchUserEvent(authentication, targetUser))

        then:
        SpringSecurityEvent.count() == 1
        def event = SpringSecurityEvent.list().first()
        event.username == "username"
        event.sessionId == "mockSessionId"
        event.eventName == "AuthenticationSwitchUserEvent"
        event.switchedUsername == "switchedUsername"
        event.remoteAddress == "127.0.0.1"
    }
}
