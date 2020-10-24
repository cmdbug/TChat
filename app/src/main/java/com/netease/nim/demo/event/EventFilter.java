package com.netease.nim.demo.event;

import com.netease.nimlib.sdk.event.model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzchenkang on 2017/4/10.
 */

public class EventFilter {

    private Map<KeyModel, Long> timeFilter;

    private EventFilter() {
        timeFilter = new HashMap<>();
    }

    private static class Instance {
        private static EventFilter instance = new EventFilter();
    }

    public static EventFilter getInstance() {
        return Instance.instance;
    }

    /**
     * 一般地，先发布事件先下发，但是可能存在同一事件先后顺序错乱的情况，因为事件以最后时间为准，因此这里过滤掉无效旧的事件
     *
     * @param events
     * @return
     */
    public List<Event> filterOlderEvent(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return null;
        }
        List<Event> results = new ArrayList<>();
        for (Event event : events) {
            KeyModel key = new KeyModel(event.getEventType(), event.getPublisherAccount());
            long eventTime = event.getPublishTime();

            if (timeFilter.containsKey(key)) {
                long lastEventTime = timeFilter.get(key);
                if (eventTime < lastEventTime) {
                    continue;
                }
            }
            timeFilter.put(key, eventTime);
            results.add(event);
        }
        return results;
    }

    private static class KeyModel {
        private int eventType;
        private String id = "";

        public KeyModel(int eventType, String id) {
            this.eventType = eventType;
            this.id = id;
        }

        @Override
        public int hashCode() {
            if (id == null) {
                return eventType;
            } else {
                return eventType + 32 * id.hashCode();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof KeyModel)) {
                return false;
            }
            KeyModel other = (KeyModel) o;
            if (eventType == other.eventType) {
                if (id == null) {
                    return other.id == null;
                } else {
                    return id.equals(other.id);
                }
            } else {
                return false;
            }
        }
    }
}
