package com.loginspector.statistics.write;

import com.loginspector.logging.Logger;
import com.loginspector.statistics.RenderStatistics;
import com.loginspector.statistics.RenderStatistics.RenderInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class WriteRenderStatisticsAsXmlStrategy implements WriteStatisticsStrategy<RenderStatistics> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    private final Logger logger;

    public WriteRenderStatisticsAsXmlStrategy(Function<Class, Logger> createLogger) {
        this.logger = createLogger.apply(this.getClass());
    }

    @Override
    public void writeStatistics(OutputStream outputStream, RenderStatistics statistics) {
        try {
            Document document = writeAsXml(statistics);
            writeDocumentToOutputStream(outputStream, document);
        } catch (ParserConfigurationException | TransformerException e) {
            logger.error("An error occurred while writing away the statistics to XML.", e);
        }
    }

    private void writeDocumentToOutputStream(OutputStream outputStream, Document document) throws TransformerException {
        Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(outputStream));
    }

    private Document writeAsXml(RenderStatistics statistics) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document dom = db.newDocument();
        dom.appendChild(createDocumentContent(statistics, dom));
        return dom;
    }

    private Element createDocumentContent(RenderStatistics statistics, Document dom) {
        Element rootElement = dom.createElement("report");
        for (RenderInfo renderInfo : statistics.renderInfos) {
            rootElement.appendChild(createRenderingInfoElement(dom, renderInfo));
        }
        rootElement.appendChild(createSummaryElement(statistics, dom));
        return rootElement;
    }

    private Element createRenderingInfoElement(Document dom, RenderInfo renderInfo) {
        Element renderingElement = dom.createElement("rendering");
        renderingElement.appendChild(createNodeWithTextValue(dom, "document", renderInfo.documentId));
        renderingElement.appendChild(createNodeWithTextValue(dom, "page", renderInfo.page));
        renderingElement.appendChild(createNodeWithTextValue(dom, "uid", renderInfo.renderUid));

        for (LocalDateTime startRenderTimestamp : renderInfo.startRenderTimestamps) {
            renderingElement.appendChild(createNodeWithTextValue(dom, "start", present(startRenderTimestamp)));
        }
        for (LocalDateTime getRenderTimestamp : renderInfo.getRenderTimestamps) {
            renderingElement.appendChild(createNodeWithTextValue(dom, "get", present(getRenderTimestamp)));
        }
        return renderingElement;
    }

    private Element createSummaryElement(RenderStatistics statistics, Document dom) {
        Element summaryElement = dom.createElement("summary");
        summaryElement.appendChild(createNodeWithIntValue(dom, "count", statistics.renderInfos.size()));
        summaryElement.appendChild(createNodeWithIntValue(dom, "duplicates", statistics.getRendersWithMultipleStarts()));
        summaryElement.appendChild(createNodeWithIntValue(dom, "unnecessary", statistics.getRendersWithStartButNoGets()));
        return summaryElement;
    }

    private String present(LocalDateTime startRenderTimestamp) {
        if(startRenderTimestamp == null) {
            return "UNKNOWN";
        } else {
            return startRenderTimestamp.format(DATE_TIME_FORMATTER);
        }
    }

    private Element createNodeWithTextValue(Document dom, String nodeName, String value) {
        Element document = dom.createElement(nodeName);
        document.appendChild(dom.createTextNode(value));
        return document;
    }

    private Element createNodeWithIntValue(Document dom, String nodeName, int value) {
        return createNodeWithTextValue(dom, nodeName, String.valueOf(value));
    }
}
