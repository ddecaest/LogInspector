package com.loginspector.process


import spock.lang.Specification

import java.time.LocalDateTime

class LogFileReaderTest extends Specification {

    def handlesOneValidLine() {
        given:
        def content = "2010-10-06 09:11:51,360 [WorkerThread-0] DEBUG [DmsObjectDeterminator]: Object to encode for ObjectId { com.dn.dms.models.DocumentStatus - 19936 } (encoding depth = 1): null"
        def logFile = createReaderFromContent(content)

        when:
        def optionalFirstLine = logFile.readLine()

        then:
        optionalFirstLine.isPresent()
        def firstLine = optionalFirstLine.get()
        firstLine.timestamp == LocalDateTime.of(2010, 10, 6, 9, 11, 51, padNanos(360))
        firstLine.thread == "WorkerThread-0"
        firstLine.loglevel == LogLevel.DEBUG
        firstLine.className == "DmsObjectDeterminator"
        firstLine.errorMessage == "Object to encode for ObjectId { com.dn.dms.models.DocumentStatus - 19936 } (encoding depth = 1): null"

        when:
        def secondLine = logFile.readLine()

        then:
        secondLine.isEmpty()
    }

    def handlesInvalidDate() {
        given:
        def content = "2010-13-06 09:11:51,360 [WorkerThread-0] DEBUG [DmsObjectDeterminator]: Object to encode for ObjectId { com.dn.dms.models.DocumentStatus - 19936 } (encoding depth = 1): null"
        def logFile = createReaderFromContent(content)

        when:
        def optionalFirstLine = logFile.readLine()

        then:
        optionalFirstLine.isPresent()
        def firstLine = optionalFirstLine.get()
        firstLine.timestamp == LocalDateTime.of(2010, 10, 6, 9, 11, 51, padNanos(360))
        firstLine.thread == "WorkerThread-0"
        firstLine.loglevel == LogLevel.DEBUG
        firstLine.className == "DmsObjectDeterminator"
        firstLine.errorMessage == "Object to encode for ObjectId { com.dn.dms.models.DocumentStatus - 19936 } (encoding depth = 1): null"

        when:
        def secondLine = logFile.readLine()

        then:
        secondLine.isEmpty()
    }

    private static LogFileReader createReaderFromContent(String content) {
        def targetStream = new ByteArrayInputStream(content.getBytes())
        return new LogFileReader(targetStream)
    }

    private static int padNanos(int nanos) {
        def paddedString = String.valueOf(nanos).padRight(9, "0")
        return Integer.valueOf(paddedString)
    }
}
