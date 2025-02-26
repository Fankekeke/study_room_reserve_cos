package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.PaymentRecordInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author Fank gmail - fan1ke2ke@gmail.com
 */
public interface PaymentRecordInfoMapper extends BaseMapper<PaymentRecordInfo> {

    /**
     * 分页获取支付记录
     *
     * @param page              分页对象
     * @param paymentRecordInfo 支付记录
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPaymentRecordPage(Page<PaymentRecordInfo> page, @Param("paymentRecordInfo") PaymentRecordInfo paymentRecordInfo);
}
