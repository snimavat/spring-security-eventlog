package ca.redtoad.eventlog

import grails.plugin.springsecurity.SecurityEventListener
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import grails.plugins.Plugin
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent

class SpringSecurityEventlogGrailsPlugin extends Plugin {

    def version = "3.0.1"
    def grailsVersion = "3.3 > *"
    def loadBefore = ['springSecurityRest']
    def loadAfter = ['springSecurityCore']
    def title = "Spring Security Event Log"
    def description = 'A plugin to log Spring Security events'
    def documentation = "https://github.com/ataylor284/spring-security-eventlog"
    def license = "APACHE"
    def developers = [
        [name: 'Andrew Taylor', email: 'ataylor@redtoad.ca', name: "Sudhir Nimavat", email: "sudhir@nimavat.me"]
    ]

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
