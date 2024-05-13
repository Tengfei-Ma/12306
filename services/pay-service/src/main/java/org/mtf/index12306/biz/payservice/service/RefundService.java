package org.mtf.index12306.biz.payservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mtf.index12306.biz.payservice.dao.entity.RefundDO;
import org.mtf.index12306.biz.payservice.dto.req.RefundReqDTO;
import org.mtf.index12306.biz.payservice.dto.resp.RefundRespDTO;

/**
 * 退款接口层
 */
public interface RefundService extends IService<RefundDO> {

    /**
     * 公共退款接口
     *
     * @param requestParam 退款请求参数
     * @return 退款返回详情
     */
    RefundRespDTO commonRefund(RefundReqDTO requestParam);
}
