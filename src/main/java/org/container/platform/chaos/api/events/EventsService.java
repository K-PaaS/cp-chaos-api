package org.container.platform.chaos.api.events;

import org.container.platform.chaos.api.common.model.Params;
import org.springframework.stereotype.Service;

/**
 * Events Service 클래스
 *
 * @author luna
 * @version 1.0
 * @since 2024.06.18
 **/
@Service
public class EventsService {

    /**
     * Events 목록 조회 (Get Events List)
     *
     * @param params the params
     * @return the EventsList
     */
    public EventsList getEventsList(Params params) {
        return null;
    }

    /**
     * Events 상세 조회(Get Events Detail)
     *
     * @param params the params
     * @return the events detail
     */
    public Events getEvents(Params params) {
        return null;
    }
}
