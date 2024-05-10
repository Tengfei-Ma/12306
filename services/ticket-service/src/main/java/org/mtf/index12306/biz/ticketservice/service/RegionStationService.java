package org.mtf.index12306.biz.ticketservice.service;

import org.mtf.index12306.biz.ticketservice.dto.req.RegionStationQueryReqDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.RegionStationQueryRespDTO;
import org.mtf.index12306.biz.ticketservice.dto.resp.StationQueryRespDTO;

import java.util.List;

/**
 * 地区以及车站接口层
 */
public interface RegionStationService {
    /**
     * 查询车站集合信息
     *
     * @param requestParam 车站查询参数
     * @return 车站返回数据集合
     */
    List<RegionStationQueryRespDTO> listRegionStation(RegionStationQueryReqDTO requestParam);
    /**
     * 查询所有车站集合信息
     *
     * @return 车站返回数据集合
     */
    List<StationQueryRespDTO> listAllStation();
}
