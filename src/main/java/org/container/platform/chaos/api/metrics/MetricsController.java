package org.container.platform.chaos.api.metrics;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
import org.container.platform.chaos.api.metrics.model.StressChaos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Metrics Controller 클래스
 *
 * @author luna
 * @version 1.0
 * @since 2024.08.07
 **/
@Api(value = "MetricsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/metrics")
public class MetricsController {
    private final MetricsService metricsService;

    /**
     * Instantiates a new Metrics controller
     *
     * @param metricsService the Metrics service
     */
    @Autowired
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    /**
     * Metrics Resource Usage of Chaos DB 조회(Get Experiments Resource Usage of Chaos DB)
     *
     * @param params the params
     * @return the resultStatus
     */

    @ApiOperation(value="Metrics Resource Usage of Chaos DB 조회(Get Metrics Resource Usage of Chaos DB)", nickname="getResourceUsageOfChaos")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping("/resourceUsageOfChaos")
    public ResultStatus getResourceUsageOfChaos(Params params) {
        return metricsService.createStressChaos(params);}
}
