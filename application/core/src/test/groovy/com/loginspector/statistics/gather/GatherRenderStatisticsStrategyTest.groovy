package com.loginspector.statistics.gather

import com.loginspector.logfile.LogLevel
import com.loginspector.logfile.LogLine
import com.loginspector.logging.Logger
import spock.lang.Specification

import java.time.LocalDateTime

class GatherRenderStatisticsStrategyTest extends Specification {

    Logger loggerUsed = Mock(Logger)

    def underTest = new GatherRenderStatisticsStrategy({ clazz -> loggerUsed })

    def DOCUMENT_ID = "documenttest"
    def RENDER_UID = "renderuidtest"
    def THREAD = "testthread"
    def PAGE = "pagetest"

    def multipleStartsOneGet() {

        given:
        def thread1 = "Thread1"
        def thread2 = "Thread2"
        def firstStartRenderSeconds = 3
        def secondStartRenderSeconds = 6
        def getRenderSeconds = 5

        def firstStartRender = createStartRenderingLogLine(DOCUMENT_ID, PAGE, thread1, firstStartRenderSeconds, 1)
        def unstructured = LogLine.createUnstructuredLogLine("something else entirely", 2)
        def firstReturnUid = createReturnRenderUIDLogLine(RENDER_UID, thread1, 4, 3)
        def getRender = createGetRenderLogLine(RENDER_UID, getRenderSeconds, 4)
        def secondStartRender = createStartRenderingLogLine(DOCUMENT_ID, PAGE, thread2, secondStartRenderSeconds, 5)
        def secondReturnUid = createReturnRenderUIDLogLine(RENDER_UID, thread2, 7, 6)

        when:
        underTest.consume(firstStartRender)
        underTest.consume(unstructured)
        underTest.consume(firstReturnUid)
        underTest.consume(getRender)
        underTest.consume(secondStartRender)
        underTest.consume(secondReturnUid)
        and:
        def result = underTest.getResult()

        then:
        result.renderInfos.size() == 1
        def renderInfo = result.renderInfos[0]
        renderInfo.documentId == DOCUMENT_ID
        renderInfo.renderUid == RENDER_UID
        renderInfo.page == PAGE
        renderInfo.getRenderTimestamps == [createTime(getRenderSeconds)]
        renderInfo.startRenderTimestamps == [createTime(firstStartRenderSeconds), createTime(secondStartRenderSeconds)]
    }

    def oneStartMultipleGets() {

        given:
        def renderUid = "renderuidtest"
        def startRenderSeconds = 2
        def firstGetRenderSeconds = 5
        def secondGetRenderSeconds = 13

        def firstStartRender = createStartRenderingLogLine(DOCUMENT_ID, PAGE, THREAD, startRenderSeconds, 1)
        def firstReturnUid = createReturnRenderUIDLogLine(renderUid, THREAD, 4, 2)
        def getRender = createGetRenderLogLine(renderUid, firstGetRenderSeconds, 3)
        def unstructured = LogLine.createUnstructuredLogLine("something else entirely", 4)
        def getRender2 = createGetRenderLogLine(renderUid, secondGetRenderSeconds, 5)

        when:
        underTest.consume(firstStartRender)
        underTest.consume(firstReturnUid)
        underTest.consume(getRender)
        underTest.consume(unstructured)
        underTest.consume(getRender2)
        and:
        def result = underTest.getResult()

        then:
        result.renderInfos.size() == 1
        def renderInfo = result.renderInfos[0]
        renderInfo.documentId == DOCUMENT_ID
        renderInfo.page == PAGE
        renderInfo.renderUid == renderUid
        renderInfo.getRenderTimestamps == [createTime(firstGetRenderSeconds), createTime(secondGetRenderSeconds)]
        renderInfo.startRenderTimestamps == [createTime(startRenderSeconds)]
    }

    def getWithoutStartRequest() {

        given:
        def getRenderSeconds = 2
        def returnUid = createReturnRenderUIDLogLine(RENDER_UID, THREAD, 1, 1)
        def getRender = createGetRenderLogLine(RENDER_UID, getRenderSeconds, 2)

        when:
        underTest.consume(returnUid)
        underTest.consume(getRender)
        and:
        def result = underTest.getResult()

        then:
        result.renderInfos.size() == 1
        def renderInfo = result.renderInfos[0]
        renderInfo.documentId == "UNKNOWN"
        renderInfo.renderUid == RENDER_UID
        renderInfo.page == "UNKNOWN"
        renderInfo.getRenderTimestamps == [createTime(getRenderSeconds)]
        renderInfo.startRenderTimestamps == []
    }

    def getWithoutReturnUid() {

        given:
        def getRenderSeconds = 1
        def getRender = createGetRenderLogLine(RENDER_UID, getRenderSeconds, 1)

        when:
        underTest.consume(getRender)
        and:
        def result = underTest.getResult()

        then:
        result.renderInfos.size() == 1
        def renderInfo = result.renderInfos[0]
        renderInfo.documentId == "UNKNOWN"
        renderInfo.renderUid == RENDER_UID
        renderInfo.page == "UNKNOWN"
        renderInfo.getRenderTimestamps == [createTime(getRenderSeconds)]
        renderInfo.startRenderTimestamps == []
    }

    def startWithoutReturnUid() {

        given:
        def startRenderSeconds = 7
        def startRender = createStartRenderingLogLine(DOCUMENT_ID, PAGE, THREAD, startRenderSeconds, 1)

        when:
        underTest.consume(startRender)
        and:
        def result = underTest.getResult()

        then:
        result.renderInfos.size() == 1
        def renderInfo = result.renderInfos[0]
        renderInfo.documentId == DOCUMENT_ID
        renderInfo.renderUid == "UNKNOWN"
        renderInfo.page == PAGE
        renderInfo.getRenderTimestamps == []
        renderInfo.startRenderTimestamps == [createTime(startRenderSeconds)]
    }

    def startWithoutGet() {

        given:
        def startRenderSeconds = 7
        def startRender = createStartRenderingLogLine(DOCUMENT_ID, PAGE, THREAD, startRenderSeconds, 1)
        def returnUid = createReturnRenderUIDLogLine(RENDER_UID, THREAD, 9, 2)

        when:
        underTest.consume(startRender)
        underTest.consume(returnUid)
        and:
        def result = underTest.getResult()

        then:
        result.renderInfos.size() == 1
        def renderInfo = result.renderInfos[0]
        renderInfo.documentId == DOCUMENT_ID
        renderInfo.renderUid == RENDER_UID
        renderInfo.page == PAGE
        renderInfo.getRenderTimestamps == []
        renderInfo.startRenderTimestamps == [createTime(startRenderSeconds)]
    }
    
    private static LogLine createStartRenderingLogLine(
            String documentId, String page, String thread, Integer second, Integer lineNumber
    ) {
        def logMessage = createStartRenderingRequestMessage(documentId, page)
        return LogLine.createStructuredLogLine(createTime(second),
                logMessage, LogLevel.DEBUG, "Some class", thread, lineNumber
        )
    }

    private static LocalDateTime createTime(int second) {
        LocalDateTime.of(2005, 3, 4, 10, 12, second)
    }

    private static String createStartRenderingRequestMessage(documentId, pageId) {
        return """Executing request startRendering with arguments [${documentId},${pageId}]"""
    }

    private static LogLine createReturnRenderUIDLogLine(
            String renderUid, String thread, Integer second, Integer lineNumber
    ) {
        def logMessage = createReturnRenderUIDMessage(renderUid)
        return LogLine.createStructuredLogLine(createTime(second),
                logMessage, LogLevel.DEBUG, "Some class", thread, lineNumber
        )
    }

    private static String createReturnRenderUIDMessage(renderUid) {
        return """Service startRendering returned ${renderUid}"""
    }

    private static LogLine createGetRenderLogLine(
            String renderUid, Integer second, Integer lineNumber
    ) {
        def logMessage = createGetRenderMessage(renderUid)
        return LogLine.createStructuredLogLine(createTime(second),
                logMessage, LogLevel.DEBUG, "NA", "NA", lineNumber
        )
    }

    private static String createGetRenderMessage(renderUid) {
        return """Executing request getRendering with arguments [${renderUid}]"""
    }
}
