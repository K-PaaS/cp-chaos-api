package org.container.platform.chaos.api.events;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.chaos.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Events Controller 클래스
 *
 * @author luna
 * @version 1.0
 * @since 2024.06.18
 **/
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/experiments/events")
public class EventsController {

    private final EventsService eventsService;

    /**
     * Instantiates a new Events controller
     *
     * @param eventsService the Events service
     */
    @Autowired
    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    /**
     * Events 목록 조회(Get Events List)
     *
     * @return the EventsList
     */
    @ApiOperation(value="Events 목록 조회(Get Events List)", nickname="getEventsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public EventsList getEventsList(Params params) {
        return eventsService.getEventsList(params);
    }

    /**
     * Events 상세 조회(Get Events Detail)
     *
     * @param params the params
     * @return the Events detail
     */

    @ApiOperation(value = "Events 상세 조회(Get Events Detail)", nickname = "getEvents")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{uid:.+}")
    public Events getEvents(Params params) {
        return eventsService.getEvents(params);
    }


}
