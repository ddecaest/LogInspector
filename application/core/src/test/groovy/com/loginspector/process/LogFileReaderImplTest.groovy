package com.loginspector.process

import com.loginspector.logging.Logger
import spock.lang.Specification

import java.time.LocalDateTime

class LogFileReaderImplTest extends Specification {

    Logger loggerUsed = Mock(Logger)

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
        firstLine.message == "Object to encode for ObjectId { com.dn.dms.models.DocumentStatus - 19936 } (encoding depth = 1): null"

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

        then: "a warning is logged"
        1 * loggerUsed.warn("Could not parse the date on line %s, %s is not a valid date!", "1", "2010-13-06 09:11:51,360")
        and: "log line is parsed without timestamp"
        optionalFirstLine.isPresent()
        def firstLine = optionalFirstLine.get()
        firstLine.timestamp == null
        firstLine.thread == "WorkerThread-0"
        firstLine.loglevel == LogLevel.DEBUG
        firstLine.className == "DmsObjectDeterminator"
        firstLine.message == "Object to encode for ObjectId { com.dn.dms.models.DocumentStatus - 19936 } (encoding depth = 1): null"

        when:
        def secondLine = logFile.readLine()

        then:
        secondLine.isEmpty()
    }

    def handlesInvalidLogLevel() {
        given:
        def content = "2010-10-06 09:11:51,360 [WorkerThread-0] BLOOP [DmsObjectDeterminator]: Object to encode for ObjectId { com.dn.dms.models.DocumentStatus - 19936 } (encoding depth = 1): null"
        def logFile = createReaderFromContent(content)

        when:
        def optionalFirstLine = logFile.readLine()

        then: "a warning is logged"
        1 * loggerUsed.warn("Could not parse the log level on line %s, %s is not a valid log level!", "1", "BLOOP")
        and: "log line is parsed with UNKNOWN log level"
        optionalFirstLine.isPresent()
        def firstLine = optionalFirstLine.get()
        firstLine.timestamp == LocalDateTime.of(2010, 10, 6, 9, 11, 51, padNanos(360))
        firstLine.thread == "WorkerThread-0"
        firstLine.loglevel == LogLevel.UNKNOWN
        firstLine.className == "DmsObjectDeterminator"
        firstLine.message == "Object to encode for ObjectId { com.dn.dms.models.DocumentStatus - 19936 } (encoding depth = 1): null"

        when:
        def secondLine = logFile.readLine()

        then:
        secondLine.isEmpty()
    }


    private LogFileReaderImpl createReaderFromContent(String content) {
        def targetStream = new ByteArrayInputStream(content.getBytes())
        return new LogFileReaderImpl(targetStream, { clazz -> loggerUsed })
    }

    private static int padNanos(int nanos) {
        def paddedString = String.valueOf(nanos).padRight(9, "0")
        return Integer.valueOf(paddedString)
    }
}
