package com.loginspector.statistics;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RenderingStatistics {

    public final List<RenderingInfo> renderingInfos;

    public RenderingStatistics(List<RenderingInfo> renderingInfos) {
        this.renderingInfos = renderingInfos;
    }

    public static class PendingStartRenderingRequest {

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

    public static class RenderingInfo {

        public final List<LocalDateTime> getRenderingTimestamps;
        public final List<LocalDateTime> startRenderTimestamps;
        public final String renderUid;
        public final String documentId;
        public final String thread;
        public final String page;

        public RenderingInfo(String documentId, String thread, String page, LocalDateTime startRenderTimestamp, String renderUid) {
            this.documentId = documentId;
            this.thread = thread;
            this.page = page;
            this.renderUid = renderUid;

            this.startRenderTimestamps = new ArrayList<>();
            startRenderTimestamps.add(startRenderTimestamp);
            this.getRenderingTimestamps = new ArrayList<>();
        }

        public void addStartRenderTimestamp(LocalDateTime timestamp) {
            startRenderTimestamps.add(timestamp);
        }

        public void addGetRenderingTimestamp(LocalDateTime timestamp) {
            getRenderingTimestamps.add(timestamp);
        }
    }
}
