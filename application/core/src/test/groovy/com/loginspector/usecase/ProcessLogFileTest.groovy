package com.loginspector.usecase

import com.loginspector.logfile.LogFileReader
import com.loginspector.logfile.LogLine
import com.loginspector.statistics.gather.GatherStatisticsStrategy
import com.loginspector.statistics.write.WriteStatisticsStrategy
import spock.lang.Specification

class ProcessLogFileTest extends Specification {

    def logFileReaderUsed = Mock(LogFileReader)
    def gatherStatisticsStrategyUsed = Mock(GatherStatisticsStrategy)
    def writeStatisticsStrategyUsed = Mock(WriteStatisticsStrategy)

    def underTest = new ProcessLogFile.ProcessLogFileFlow(
            { a -> logFileReaderUsed },
            gatherStatisticsStrategyUsed, writeStatisticsStrategyUsed
    )

    def testUseCaseFlow() {
        given:
        def firstLine = generateLogLine()
        def secondLine = generateLogLine()
        def gatherResult = "een resultaat"
        def outputStream = new ByteArrayOutputStream()

        when:
        underTest.execute(new ByteArrayInputStream("test".getBytes()), outputStream)

        then: "consumes a log line"
        1 * logFileReaderUsed.readLine() >> Optional.of(firstLine)
        1 * gatherStatisticsStrategyUsed.consume(firstLine)
        and: "consumes another log line"
        1 * logFileReaderUsed.readLine() >> Optional.of(secondLine)
        1 * gatherStatisticsStrategyUsed.consume(secondLine)
        and: "runs out of lines, statistics strategy returns result"
        1 * logFileReaderUsed.readLine() >> Optional.empty()
        1 * gatherStatisticsStrategyUsed.getResult() >> gatherResult
        1 * writeStatisticsStrategyUsed.writeStatistics(outputStream, gatherResult)
    }

    private static LogLine generateLogLine() {
        return LogLine.unstructuredLogLine(UUID.randomUUID().toString(), -1)
    }
}
