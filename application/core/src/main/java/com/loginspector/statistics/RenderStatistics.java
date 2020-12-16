package com.loginspector.statistics;

import java.time.LocalDateTime;
import java.util.*;

public class RenderStatistics {

    public static final String UNKNOWN_DATA = "UNKNOWN";
    public final Collection<RenderInfo> renderInfos;

    public RenderStatistics(Collection<RenderInfo> renderInfos) {
        this.renderInfos = renderInfos;
    }

    public int getRendersWithMultipleStarts() {
        long numberOfRendersWithMultipleStarts = this.renderInfos.stream()
                .filter(it -> it.startRenderTimestamps.size() > 1)
                .count();
        return Math.toIntExact(numberOfRendersWithMultipleStarts);
    }

    public int getRendersWithStartButNoGets() {
        long numberOfRenderWithStartButNoGets = this.renderInfos.stream()
                .filter(it -> it.startRenderTimestamps.size() > 1 && it.getRenderTimestamps.size() == 0)
                .count();
        return Math.toIntExact(numberOfRenderWithStartButNoGets);
    }


    public static class PendingStartRenderRequest {

        public final LocalDateTime timestamp;
        public final String documentId;
        public final String thread;
        public final String page;

        public PendingStartRenderRequest(String documentId, String thread, String page, LocalDateTime timestamp) {
            this.documentId = documentId;
            this.thread = thread;
            this.page = page;
            this.timestamp = timestamp;
        }

        public RenderInfo asCompletedRender(String renderUid) {
            return RenderInfo.create(documentId, page, timestamp, renderUid);
        }
    }

    public static class RenderInfo {

        public final List<LocalDateTime> getRenderTimestamps;
        public final List<LocalDateTime> startRenderTimestamps;
        public final String renderUid;
        public final String documentId;
        public final String page;

        public RenderInfo(List<LocalDateTime> startRenderTimestamps, String renderUid, String documentId, String page) {
            this.getRenderTimestamps = new ArrayList<>();
            this.startRenderTimestamps = startRenderTimestamps;
            this.renderUid = renderUid;
            this.documentId = documentId;
            this.page = page;
        }

        public static RenderInfo create(String documentId, String page, LocalDateTime startRenderTimestamp, String renderUid) {
            List<LocalDateTime> startRenderTimestamps = new ArrayList<>();
            startRenderTimestamps.add(startRenderTimestamp);
            return new RenderInfo(startRenderTimestamps, renderUid, documentId, page);
        }

        public static RenderInfo createWithoutStartRenderingInfo(String renderUid) {
            return new RenderInfo(new ArrayList<>(), renderUid, UNKNOWN_DATA, "UNKNOWN");
        }

        public static RenderInfo createWithOnlyStartRenderingInfo(String documentId, String page, LocalDateTime startRenderTimestamp) {
            List<LocalDateTime> startRenderTimestamps = new ArrayList<>();
            startRenderTimestamps.add(startRenderTimestamp);
            return new RenderInfo(startRenderTimestamps, "UNKNOWN", documentId, page);
        }

        public void addStartRenderTimestamp(LocalDateTime timestamp) {
            startRenderTimestamps.add(timestamp);
        }

        public void addGetRenderTimestamp(LocalDateTime timestamp) {
            getRenderTimestamps.add(timestamp);
        }
    }
}
