package org.container.platform.chaos.api.chaos;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.chaos.api.chaos.model.*;
import org.container.platform.chaos.api.common.model.Params;
import org.container.platform.chaos.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Experiments Controller 클래스
 *
 * @author luna
 * @version 1.0
 * @since 2024.06.21
 **/
@Tag(name = "ExperimentsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/experiments")
public class ExperimentsController {
    private final ExperimentsService experimentsService;

    /**
     * Instantiates a new Experiments controller
     *
     * @param experimentsService the Experiments service
     */
    @Autowired
    public ExperimentsController(ExperimentsService experimentsService) {
        this.experimentsService = experimentsService;
    }

    /**
     * Experiments 목록 조회(Get Experiments List)
     *
     * @return the Experiments List
     */
    @Operation(summary = "Experiments 목록 조회(Get Experiments List)", operationId = "getExperimentsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(type  = "common.model.Params"))
    @GetMapping
    public ExperimentsList getExperimentsList(Params params) {
        return experimentsService.getExperimentsList(params);
    }

    /**
     * Experiments 상세 조회(Get Experiments Detail)
     *
     * @param params the params
     * @return the experiments detail
     */
    @Operation(summary = "Experiments 상세 조회(Get Experiments Detail)", operationId = "getExperiments")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(type  = "common.model.Params"))
    @GetMapping(value = "/{kind:.+}/{name:.+}")
    public Experiments getExperiments(Params params) {
        return experimentsService.getExperiments(params);
    }

    /**
     * Experiments Status 목록 조회(Get Experiments Status List)
     *
     * @return the Experiments Status List
     */
    @Operation(summary = "Experiments Status 목록 조회(Get Experiments Status List)", operationId = "getExperimentsStatusList")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(type  = "common.model.Params"))
    @GetMapping("/status")
    public ExperimentsDashboardList getExperimentsStatusList(Params params) {
        return experimentsService.getExperimentsStatusList(params);
    }

    /**
     * Experiments 상세 Status 조회(Get Experiments Detail Status)
     *
     * @return the Experiments Detail Status
     */
    @Operation(summary = "Experiments 상세 Status 조회(Get Experiments Detail Status)", operationId = "getExperimentsStatus")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(type  = "common.model.Params"))
    @GetMapping("/status/{uid:.+}")
    public ExperimentsDashboard getExperimentsStatus(Params params) {
        return experimentsService.getExperimentsStatus(params);
    }

    /**
     * Experiments 생성(Create Experiments)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Experiments 생성(Create Experiments)", operationId = "createExperiments")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(type  = "common.model.Params"))
    @PostMapping
    public Object createExperiments(@RequestBody Params params) {
        return experimentsService.createExperiments(params);
    }

    /**
     * Experiments 삭제(Delete Experiments)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Experiments 삭제(Delete Experiments)", operationId = "deleteExperiments")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(type  = "common.model.Params"))
    @DeleteMapping("/{kind:.+}/{name:.+}")
    public ResultStatus deleteExperiments(Params params) {
        return experimentsService.deleteExperiments(params);
    }

    /**
     * Experiments Events 목록 조회(Get Experiments Events List)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "Experiments Events 목록 조회(Get Experiments Events List)", operationId = "getExperimentsEventsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(type  = "common.model.Params"))
    @GetMapping("/events")
    public ExperimentsEventsList getExperimentsEventsList(Params params) {
        return experimentsService.getExperimentsEventsList(params);
    }

    /**
     * Resource usage by selected Pods during chaos 조회(Get Resource Usage by selected Pods during chaos)
     *
     * @param params the params
     * @return the ResourceUsage
     */
    @Operation(summary = "Resource usage by selected Pods during chaos 조회(Get Resource Usage by selected Pods during chaos)", operationId = "getResourceUsageByPod")
    @Parameter(name = "params", description = "request parameters", required = true)
    @GetMapping("/resourceUsageByPod/{name:.+}")
    public ResourceUsage getResourceUsageByPod(Params params) {
        return experimentsService.getResourceUsageByPod(params);
    }

    /**
     * Resource usage by workload for selected Pods during chao 조회(Get Resource usage by workload for selected Pods during chao)
     *
     * @param params the params
     * @return the ResourceUsage
     */
    @Operation(summary = "Resource usage by workload for selected Pods during chao 조회(Get Resource usage by workload for selected Pods during chao)", operationId = "getResourceUsageByWorkload")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(type  = "common.model.Params"))
    @GetMapping("/resourceUsageByWorkload/{name:.+}")
    public ResourceUsage getResourceUsageByWorkload(Params params) {
        return experimentsService.getResourceUsageByWorkload(params);
    }

    /**
     * Resource usage by node during chaos 조회(Get Resource usage by node during chaos)
     *
     * @param params the params
     * @return the ResourceUsage
     */
    @Operation(summary = "Resource usage by node during chaos 조회(Get Resource usage by node during chaos)", operationId = "getResourceUsageByNode")
    @Parameter(name = "params", description = "request parameters", required = true, schema = @Schema(type  = "common.model.Params"))
    @GetMapping("/resourceUsageByNode/{name:.+}")
    public ResourceUsage getResourceUsageByNode(Params params) {
        return experimentsService.getResourceUsageByNode(params);
    }
}


