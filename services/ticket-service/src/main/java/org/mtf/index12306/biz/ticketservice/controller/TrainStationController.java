package org.mtf.index12306.biz.ticketservice.controller;

import lombok.RequiredArgsConstructor;
import org.mtf.index12306.biz.ticketservice.dto.resp.TrainStationQueryRespDTO;
import org.mtf.index12306.biz.ticketservice.service.TrainStationService;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.mtf.index12306.framework.starter.web.Results;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 列车站点控制层
 */
@RestController
@RequiredArgsConstructor
public class TrainStationController {

    private final TrainStationService trainStationService;

    /**
     * 根据列车 ID 查询经停站点信息
     */
    @GetMapping("/api/ticket-service/train-station")
    public Result<List<TrainStationQueryRespDTO>> listTrainStationQuery(String trainId) {
        return Results.success(trainStationService.listTrainStationQuery(trainId));
    }
}
