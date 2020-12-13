package com.loginspector.process;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GatherStatisticsStrategyImpl implements GatherStatisticsStrategy {

    private static final Pattern START_RENDERING_MESSAGE_PATTERN = Pattern.compile("Executing request startRendering with arguments \\[(.*?),(.*?)]]");
    private static final Pattern ADD_RENDERING_COMMAND_TO_QUEUE_PATTERN = Pattern.compile("Adding command to queue: \\{ RenderingCommand - uid: (.*?) }");
    private static final Pattern GET_RENDERING_PATTERN = Pattern.compile("Executing request getRendering with arguments [(.*?)]");

    private final List<PendingStartRenderingRequest> pendingRequests = new ArrayList<>();

    @Override
    public void consume(LogLine logLine) {
        if(!logLine.isStructuredLogLine()) {
            return;
        }

        Matcher startRenderingMatcher = START_RENDERING_MESSAGE_PATTERN.matcher(logLine.message);
        if (startRenderingMatcher.find( )) {
            handleStartRenderingRequest(logLine, startRenderingMatcher);
            return;
        }

        Matcher addCommandToQueue = ADD_RENDERING_COMMAND_TO_QUEUE_PATTERN.matcher(logLine.message);
        if (addCommandToQueue.find( )) {
            String commandUid = addCommandToQueue.group(1);
            // TODO: if uid already exists, the open request started actually belongs to another command info, merge it
            // TODO: else, the open request is promoted to started
            return;
        }

        Matcher getRenderingMatcher = GET_RENDERING_PATTERN.matcher(logLine.message);
        if (addCommandToQueue.find( )) {
            String commandUid = addCommandToQueue.group(1);
            // TODO: match with a startRendering.
            // TODO: it is possible that no match is found...
            return;
        }
    }

    private void handleStartRenderingRequest(LogLine logLine, Matcher startRenderingMatcher) {
        String documentId = startRenderingMatcher.group(1);
        String page = startRenderingMatcher.group(2);

        var request = new PendingStartRenderingRequest(logLine.thread, documentId, page);
        pendingRequests.add(request);
    }

    @Override
    public InputStream getResultAsXml() {
        // TODO
        return null;
    }


    private static class PendingStartRenderingRequest {

        public final String documentId;
        public final String thread;
        public final String page;

        public PendingStartRenderingRequest(String documentId, String thread, String page) {
            this.documentId = documentId;
            this.thread = thread;
            this.page = page;
        }
    }
}
