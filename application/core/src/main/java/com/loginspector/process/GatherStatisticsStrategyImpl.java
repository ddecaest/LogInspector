package com.loginspector.process;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GatherStatisticsStrategyImpl implements GatherStatisticsStrategy {

    private static final Pattern START_RENDERING_MESSAGE_PATTERN = Pattern.compile("Executing request startRendering with arguments \\[(.*?),(.*?)]]");
    private static final Pattern RENDERING_UID_RETURNED_PATTERN = Pattern.compile("Service startRendering returned\s(.*)");
    private static final Pattern GET_RENDERING_PATTERN = Pattern.compile("Executing request getRendering with arguments [(.*?)]");

    private final List<PendingStartRenderingRequest> pendingRequests = new ArrayList<>();
    private final List<RenderingInfo> renderingInfos = new ArrayList<>();

    @Override
    public void consume(LogLine logLine) {
        if(!logLine.isStructuredLogLine()) {
            return;
        }

        Matcher startRenderingMatcher = START_RENDERING_MESSAGE_PATTERN.matcher(logLine.message);
        if (startRenderingMatcher.find( )) {
            final String documentId = startRenderingMatcher.group(1);
            final String page = startRenderingMatcher.group(2);
            handleStartRenderingRequest(logLine, documentId, page);
            return;
        }

        Matcher renderingUidReturnedMatcher = RENDERING_UID_RETURNED_PATTERN.matcher(logLine.message);
        if (renderingUidReturnedMatcher.find( )) {
            final String renderUid = renderingUidReturnedMatcher.group(1).trim();
            handleRenderingUidReturned(logLine, renderUid);
            return;
        }

        Matcher getRenderingMatcher = GET_RENDERING_PATTERN.matcher(logLine.message);
        if (renderingUidReturnedMatcher.find( )) {
            // TODO
            handleGetRendering(renderingUidReturnedMatcher);
            return;
        }
    }

    private void handleStartRenderingRequest(LogLine logLine, String documentId, String page) {
        PendingStartRenderingRequest request = new PendingStartRenderingRequest(logLine.thread, documentId, page, logLine.timestamp);
        pendingRequests.add(request);
    }

    private void handleRenderingUidReturned(LogLine logLine, String renderUid) {
        Optional<PendingStartRenderingRequest> optionalMatchingPendingRequest = popMatchingPendingRequest(logLine.thread);
        if(optionalMatchingPendingRequest.isEmpty()) {
            // The pending request was before the start of the log file, ignore it, as we do not have a start timestamp...
            return;
        }
        PendingStartRenderingRequest matchingPendingRequest = optionalMatchingPendingRequest.get();

        var alreadyCompletedRenderingRequest = renderingInfos.stream().filter(it -> Objects.equals(it.renderUid, renderUid)).findAny();
        if(alreadyCompletedRenderingRequest.isPresent()) {
            RenderingInfo renderingInfo = alreadyCompletedRenderingRequest.get();
            renderingInfo.addStartRenderTimestamp(matchingPendingRequest.timestamp);
        } else {
            RenderingInfo renderingInfo = matchingPendingRequest.updateWithRenderUid(renderUid);
            renderingInfos.add(renderingInfo);
        }
    }

    private Optional<PendingStartRenderingRequest> popMatchingPendingRequest(String thread) {
        Predicate<PendingStartRenderingRequest> findMatchingPendingRequest = it -> Objects.equals(it.thread, thread);
        var matchingPendingRequest= pendingRequests.stream().filter(findMatchingPendingRequest).findAny();
        pendingRequests.removeIf(findMatchingPendingRequest);
        return matchingPendingRequest;
    }

    private void handleGetRendering(Matcher renderingUidReturnedMatcher) {
        String commandUid = renderingUidReturnedMatcher.group(1);
        // TODO: match with a startRendering.
        // TODO: it is possible that no match is found...
    }

    @Override
    public InputStream getResultAsXml() {
        // TODO
        return null;
    }


    private static class PendingStartRenderingRequest {

        public final LocalDateTime timestamp;
        public final String documentId;
        public final String thread;
        public final String page;

        public PendingStartRenderingRequest(String documentId, String thread, String page, LocalDateTime timestamp) {
            this.documentId = documentId;
            this.thread = thread;
            this.page = page;
            this.timestamp = timestamp;
        }

        public RenderingInfo updateWithRenderUid(String renderUid) {
            return new RenderingInfo(documentId, thread, page, timestamp, renderUid);
        }
    }

    private static class RenderingInfo {

        public final String renderUid;
        public final String documentId;
        public final String thread;
        public final String page;
        public final List<LocalDateTime> startRenderTimestamps;

        public RenderingInfo(String documentId, String thread, String page, LocalDateTime startRenderTimestamp, String renderUid) {
            this.documentId = documentId;
            this.thread = thread;
            this.page = page;
            this.renderUid = renderUid;
            this.startRenderTimestamps = new ArrayList<>();
            startRenderTimestamps.add(startRenderTimestamp);
        }

        public void addStartRenderTimestamp(LocalDateTime timestamp) {
            startRenderTimestamps.add(timestamp);
        }
    }
}
