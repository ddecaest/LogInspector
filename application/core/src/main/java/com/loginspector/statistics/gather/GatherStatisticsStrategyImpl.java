package com.loginspector.statistics.gather;

import com.loginspector.logfile.LogLine;
import com.loginspector.statistics.RenderingStatistics;
import com.loginspector.statistics.RenderingStatistics.PendingStartRenderingRequest;
import com.loginspector.statistics.RenderingStatistics.RenderingInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GatherStatisticsStrategyImpl implements GatherStatisticsStrategy<RenderingStatistics> {

    private static final Pattern START_RENDERING_MESSAGE_PATTERN = Pattern.compile("Executing request startRendering with arguments \\[(.*?),(.*?)]]");
    private static final Pattern RENDERING_UID_RETURNED_PATTERN = Pattern.compile("Service startRendering returned\s(.*)");
    private static final Pattern GET_RENDERING_PATTERN = Pattern.compile("Executing request getRendering with arguments [(.*?)]");

    private final List<PendingStartRenderingRequest> pendingRequests = new ArrayList<>();
    private final List<RenderingInfo> renderingInfos = new ArrayList<>();

    @Override
    public void consume(LogLine logLine) {
        if (!logLine.isStructuredLogLine()) {
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
        PendingStartRenderingRequest request = new PendingStartRenderingRequest(logLine.thread, documentId, page, logLine.timestamp);
        pendingRequests.add(request);
    }

    private void handleRenderingUidReturned(LogLine logLine, String renderUid) {
        Optional<PendingStartRenderingRequest> optionalMatchingPendingRequest = popMatchingPendingRequest(logLine.thread);
        if (optionalMatchingPendingRequest.isEmpty()) {
            // TODO: log
            // The pending request was before the start of the log file, ignore it, as we do not have a start timestamp...
            return;
        }
        PendingStartRenderingRequest matchingPendingRequest = optionalMatchingPendingRequest.get();

        var alreadyCompletedRenderingRequest = renderingInfos.stream()
                .filter(it -> Objects.equals(it.renderUid, renderUid))
                .findAny();
        if (alreadyCompletedRenderingRequest.isPresent()) {
            RenderingInfo renderingInfo = alreadyCompletedRenderingRequest.get();
            renderingInfo.addStartRenderTimestamp(matchingPendingRequest.timestamp);
        } else {
            RenderingInfo renderingInfo = matchingPendingRequest.updateWithRenderUid(renderUid);
            renderingInfos.add(renderingInfo);
        }
    }

    private Optional<PendingStartRenderingRequest> popMatchingPendingRequest(String thread) {
        Predicate<PendingStartRenderingRequest> findMatchingPendingRequest = it -> Objects.equals(it.thread, thread);

        var matchingPendingRequest = pendingRequests.stream()
                .filter(findMatchingPendingRequest)
                .findAny();
        pendingRequests.removeIf(findMatchingPendingRequest);

        return matchingPendingRequest;
    }

    private void handleGetRendering(LocalDateTime getRenderingTimestamp, String renderUid) {
        Optional<RenderingInfo> matchingRenderInfo = renderingInfos.stream()
                .filter(it -> Objects.equals(it.renderUid, renderUid))
                .findAny();

        matchingRenderInfo.ifPresent(it -> it.addGetRenderingTimestamp(getRenderingTimestamp));

        if(matchingRenderInfo.isEmpty()) {
            // TODO: it is possible that no match is found...
        }
    }

    @Override
    public RenderingStatistics getResult() {
        return new RenderingStatistics(renderingInfos);
    }
}
