package com.loginspector.process;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class GatherStatisticsStrategyImpl implements GatherStatisticsStrategy {

    @Override
    public void consume(LogLine logLine) {
        // TODO
    }

    @Override
    public InputStream getResultAsXml() {
        // TODO
        return null;
    }


    private static class StartRenderingCommandInfo {

        public final List<GetRenderingCommandInfo> getRenderingsAssociated;
        public final List<LocalDateTime> startTimeStamps;
        public final String id;

        public StartRenderingCommandInfo(String id) {
            this.getRenderingsAssociated = new ArrayList<>();
            this.startTimeStamps = new ArrayList<>();
            this.id = id;
        }
    }

    private static class GetRenderingCommandInfo{

        public final LocalDateTime timeStamp;

        public GetRenderingCommandInfo(LocalDateTime timeStamp) {
            this.timeStamp = timeStamp;
        }
    }
}
