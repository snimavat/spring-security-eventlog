/*
* Copyright 2019 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package ca.redtoad.eventlog

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class SpringSecurityEvent {

    String username
    String sessionId
    String eventName
    String remoteAddress
    String switchedUsername
    Date dateCreated

    static constraints = {
        username(nullable: true)
        sessionId(nullable: true)
        eventName()
        remoteAddress(nullable: true)
        switchedUsername(nullable: true)
        dateCreated()
    }

    static mapping = {
        version false
    }
}
