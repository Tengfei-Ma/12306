package org.mtf.index12306.biz.ticketservice.controller;

import lombok.RequiredArgsConstructor;
import org.mtf.index12306.biz.ticketservice.dto.req.RegionStationQueryReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.RegionStationQueryRespDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.StationQueryRespDTO;
import org.mtf.index12306.biz.ticketservice.service.RegionStationService;
import org.mtf.index12306.framework.starter.convention.result.Result;
import org.mtf.index12306.framework.starter.web.Results;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 地区以及车站控制层
 */
@RestController
@RequiredArgsConstructor
public class RegionStationController {

    private final RegionStationService regionStationService;

    /**
     * 查询车站&城市站点集合信息
     */
    @GetMapping("/api/ticket-service/region-station")
    public Result<List<RegionStationQueryRespDTO>> listRegionStation(RegionStationQueryReqDTO requestParam) {
        return Results.success(regionStationService.listRegionStation(requestParam));
    }

    /**
     * 查询车站站点集合信息
     */
    @GetMapping("/api/ticket-service/stations")
    public Result<List<StationQueryRespDTO>> listAllStation() {
        return Results.success(regionStationService.listAllStation());
    }
}
