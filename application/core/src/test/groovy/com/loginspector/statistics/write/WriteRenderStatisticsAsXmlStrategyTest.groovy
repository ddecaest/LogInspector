package com.loginspector.statistics.write

import com.loginspector.logging.Logger
import com.loginspector.statistics.RenderStatistics
import spock.lang.Specification

import java.time.LocalDateTime

class WriteRenderStatisticsAsXmlStrategyTest extends Specification {

    Logger loggerUsed = Mock(Logger)

    def underTest = new WriteRenderStatisticsAsXmlStrategy({ clazz -> loggerUsed })

    def handlesOneRenderInfo() {

        given:
        def outputStreamUsed = new ByteArrayOutputStream()
        def renderInfo = createBaseRenderInfo("render-uid-test", "documentid123", "page15")
        renderInfo.addStartRenderTimestamp(LocalDateTime.of(2000, 5, 3, 10, 15, 20))
        renderInfo.addGetRenderTimestamp(LocalDateTime.of(2000, 5, 3, 10, 15, 20))
        def renderInfos = new ArrayList<RenderStatistics.RenderInfo>()
        renderInfos.add(renderInfo)

        def expected = """<report>
    <rendering>
        <document>documentid123</document>
        <page>page15</page>
        <uid>render-uid-test</uid>
        <start>2000-05-03 10:15:20,000</start>
        <get>2000-05-03 10:15:20,000</get>
    </rendering>
    <summary>
        <count>1</count>
        <duplicates>0</duplicates>
        <unnecessary>0</unnecessary>
    </summary>
</report>
"""

        when:
        underTest.writeStatistics(outputStreamUsed, new RenderStatistics(renderInfos))
        def result = new String(outputStreamUsed.toByteArray())

        then:
        normalizeLineEndings(result) == normalizeLineEndings(expected)
    }

    private static String normalizeLineEndings(String testResult) {
        testResult.replace("\r", "") // Carriage returns are a windows-only thing...
    }

    private static RenderStatistics.RenderInfo createBaseRenderInfo(String renderUid, String documentId, String page) {
        return new RenderStatistics.RenderInfo(new ArrayList<LocalDateTime>(), renderUid, documentId, page)
    }
}