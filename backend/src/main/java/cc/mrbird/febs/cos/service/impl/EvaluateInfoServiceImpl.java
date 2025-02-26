package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.EvaluateInfo;
import cc.mrbird.febs.cos.dao.EvaluateInfoMapper;
import cc.mrbird.febs.cos.service.IEvaluateInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author Fank gmail - fan1ke2ke@gmail.com
 */
@Service
public class EvaluateInfoServiceImpl extends ServiceImpl<EvaluateInfoMapper, EvaluateInfo> implements IEvaluateInfoService {

    /**
     * 分页获取订单评价
     *
     * @param page             分页对象
     * @param evaluateInfo 订单评价
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryEvaluatePage(Page<EvaluateInfo> page, EvaluateInfo evaluateInfo) {
        return baseMapper.queryEvaluatePage(page, evaluateInfo);
    }
}
