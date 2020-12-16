package com.loginspector.statistics.gather;

import com.loginspector.logfile.LogLine;
import com.loginspector.logging.Logger;
import com.loginspector.statistics.RenderStatistics;
import com.loginspector.statistics.RenderStatistics.PendingStartRenderRequest;
import com.loginspector.statistics.RenderStatistics.RenderInfo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GatherRenderStatisticsStrategy implements GatherStatisticsStrategy<RenderStatistics> {

    private static final Pattern START_RENDERING_MESSAGE_PATTERN = Pattern.compile("Executing request startRendering with arguments \\[(.*?),(.*?)]]");
    private static final Pattern RENDERING_UID_RETURNED_PATTERN = Pattern.compile("Service startRendering returned\s(.*)");
    private static final Pattern GET_RENDERING_PATTERN = Pattern.compile("Executing request getRendering with arguments [(.*?)]");

    private final List<PendingStartRenderRequest> pendingRequests = new ArrayList<>();
    private final Map<String, RenderInfo> renderUidOnInfo = new HashMap<>();

    private final Logger logger;

    public GatherRenderStatisticsStrategy(Function<Class, Logger> createLogger) {
        this.logger = createLogger.apply(this.getClass());
    }

    @Override
    public void consume(LogLine logLine) {
        if (!logLine.isStructuredLogLine()) {
            // The log file can contain exceptions, which do not conform to the specified structure.
            // These can safely be ignored.
            logger.info("Log line %d is an unstructured log line, skipping...", logLine);
            return;
        }

        Matcher startRenderingMatcher = START_RENDERING_MESSAGE_PATTERN.matcher(logLine.message);
        if (startRenderingMatcher.find()) {
            final String documentId = startRenderingMatcher.group(1);
            final String page = startRenderingMatcher.group(2);
            handleStartRenderingRequest(logLine, documentId, page);
            return;
        }

        Matcher renderingUidReturnedMatcher = RENDERING_UID_RETURNED_PATTERN.matcher(logLine.message);
        if (renderingUidReturnedMatcher.find()) {
            final String renderUid = renderingUidReturnedMatcher.group(1).trim();
            handleRenderingUidReturned(logLine, renderUid);
            return;
        }

        Matcher getRenderingMatcher = GET_RENDERING_PATTERN.matcher(logLine.message);
        if (renderingUidReturnedMatcher.find()) {
            final String renderUid = getRenderingMatcher.group(1);
            handleGetRendering(logLine.timestamp, renderUid);
        }
    }

    private void handleStartRenderingRequest(LogLine logLine, String documentId, String page) {
        PendingStartRenderRequest request = new PendingStartRenderRequest(logLine.thread, documentId, page, logLine.timestamp);
        pendingRequests.add(request);
    }

    private void handleRenderingUidReturned(LogLine logLine, String renderUid) {
        Optional<PendingStartRenderRequest> optionalMatchingPendingRequest = popMatchingPendingRequest(logLine.thread);
        if (optionalMatchingPendingRequest.isEmpty()) {
            // The start rendering happened before this log file. We still want to register getRenderings, so add a render info anyway
            renderUidOnInfo.put(renderUid, RenderInfo.createWithoutStartRenderingInfo(renderUid));
            return;
        }
        PendingStartRenderRequest matchingPendingRequest = optionalMatchingPendingRequest.get();

        var alreadyKnownRenderUidInfo = renderUidOnInfo.get(renderUid);
        if (alreadyKnownRenderUidInfo != null) {
            alreadyKnownRenderUidInfo.addStartRenderTimestamp(matchingPendingRequest.timestamp);
        } else {
            renderUidOnInfo.put(renderUid, matchingPendingRequest.asCompletedRender(renderUid));
        }
    }

    private Optional<PendingStartRenderRequest> popMatchingPendingRequest(String thread) {
        Predicate<PendingStartRenderRequest> findMatchingPendingRequest = it -> Objects.equals(it.thread, thread);
        var matchingPendingRequest = pendingRequests.stream()
                .filter(findMatchingPendingRequest)
                .findAny();
        pendingRequests.removeIf(findMatchingPendingRequest);
        return matchingPendingRequest;
    }

    private void handleGetRendering(LocalDateTime getRenderTimestamp, String renderUid) {
        RenderInfo renderInfo = renderUidOnInfo.get(renderUid);
        if(renderInfo != null) {
            renderInfo.addGetRenderTimestamp(getRenderTimestamp);
        } else {
            renderUidOnInfo.put(renderUid, RenderInfo.createWithoutStartRenderingInfo(renderUid));
        }
    }

    @Override
    public RenderStatistics getResult() {
        return new RenderStatistics(renderUidOnInfo.values());
    }
}
