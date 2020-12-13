package com.loginspector.process

import spock.lang.Specification

class ProcessLogFileTest extends Specification {

    def logFileReaderUsed = Mock(LogFileReader)
    def gatherStatisticsStrategyUsed = Mock(GatherStatisticsStrategy)

    def underTest = new ProcessLogFile.ProcessLogFileFlow(
            {a -> logFileReaderUsed},
            gatherStatisticsStrategyUsed
    )

    def testUseCaseFlow() {
        given:
        def firstLine = generateLogLine()
        def secondLine = generateLogLine()
        def resultMocked = new ByteArrayInputStream("result".getBytes())

        when:
        def actualResult = underTest.execute(new ByteArrayInputStream("test".getBytes()))

        then: "consumes a log line"
        1 * logFileReaderUsed.readLine() >> Optional.of(firstLine)
        1 * gatherStatisticsStrategyUsed.consume(firstLine)
        and: "consumes another log line"
        1 * logFileReaderUsed.readLine() >> Optional.of(secondLine)
        1 * gatherStatisticsStrategyUsed.consume(secondLine)
        and: "runs out of lines, statistics strategy returns result"
        1 * logFileReaderUsed.readLine() >> Optional.empty()
        1 * gatherStatisticsStrategyUsed.getResultAsXml() >> resultMocked
        and:
        actualResult == resultMocked
    }

    private static LogLine generateLogLine() {
        return LogLine.structuredLogLine(null, UUID.toString(), null, null, null)
    }
}
