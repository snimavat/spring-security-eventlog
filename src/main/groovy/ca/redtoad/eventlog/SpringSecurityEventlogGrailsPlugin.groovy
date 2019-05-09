/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package ca.redtoad.eventlog

import org.springframework.security.authentication.DefaultAuthenticationEventPublisher
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent

import grails.plugin.springsecurity.SecurityEventListener
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import grails.plugins.Plugin

class SpringSecurityEventlogGrailsPlugin extends Plugin {

    def version = "3.0.1"
    def grailsVersion = "3.3 > *"
    def loadBefore = ['springSecurityRest']
    def loadAfter = ['springSecurityCore']
    def title = "Spring Security Event Log"
    def description = 'A plugin to log Spring Security events'
    def documentation = "https://github.com/ataylor284/spring-security-eventlog"
    def license = "APACHE"

    def scm = [url: "http://github.com/ataylor284/spring-security-eventlog"]
    def issueManagement = [system: 'GITHUB', url: "http://github.com/ataylor284/spring-security-eventlog/issues"]

    Closure doWithSpring() {
        { ->
            Class eventLoggerClass = grailsApplication.config.grails.plugin.springsecurity.eventlog.eventLogger ?: SpringSecurityEventLogger
            springSecurityEventLogger(eventLoggerClass)

            // normally these two beans are instantiated only if
            // 'useSecurityEventListener' is true, but they're needed so
            // we override the config
            securityEventListener(SecurityEventListener)
            authenticationEventPublisher(DefaultAuthenticationEventPublisher) {
                additionalExceptionMappings =
                    ([(NoStackUsernameNotFoundException.name): AuthenticationFailureBadCredentialsEvent.name] as Properties)

            }
        }
    }

    void doWithApplicationContext() {
        applicationContext.logoutHandlers << applicationContext.springSecurityEventLogger
    }
}
