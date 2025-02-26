package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.PurchaseDeviceInfo;
import cc.mrbird.febs.cos.service.IPurchaseDeviceInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 自习室采购记录
 *
 * @author Fank gmail - fan1ke2ke@gmail.com
 */
@RestController
@RequestMapping("/cos/purchase-device-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PurchaseDeviceInfoController {

    private final IPurchaseDeviceInfoService purchaseDeviceInfoService;

    /**
     * 分页获取自习室采购记录
     *
     * @param page               分页对象
     * @param purchaseDeviceInfo 自习室采购记录
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<PurchaseDeviceInfo> page, PurchaseDeviceInfo purchaseDeviceInfo) {
        return R.ok(purchaseDeviceInfoService.queryPurchaseDevicePage(page, purchaseDeviceInfo));
    }

    /**
     * 获取自习室采购记录详情
     *
     * @param code 采购单编号
     * @return 结果
     */
    @GetMapping("/queryPurchaseDetail")
    public R queryPurchaseDetail(@RequestParam("code") String code) {
        return R.ok(purchaseDeviceInfoService.queryPurchaseDetail(code));
    }

    /**
     * 自习室采购记录详情
     *
     * @param id 自习室ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(purchaseDeviceInfoService.getById(id));
    }

    /**
     * 自习室采购记录列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(purchaseDeviceInfoService.list());
    }

    /**
     * 新增自习室采购记录
     *
     * @param purchaseDeviceInfo 自习室采购记录
     * @return 结果
     */
    @PostMapping
    public R save(PurchaseDeviceInfo purchaseDeviceInfo) throws FebsException {
        return R.ok(purchaseDeviceInfoService.addPurchaseRecord(purchaseDeviceInfo));
    }

    /**
     * 修改自习室采购记录
     *
     * @param purchaseDeviceInfo 自习室采购记录
     * @return 结果
     */
    @PutMapping
    public R edit(PurchaseDeviceInfo purchaseDeviceInfo) {
        return R.ok(purchaseDeviceInfoService.updateById(purchaseDeviceInfo));
    }

    /**
     * 删除自习室采购记录
     *
     * @param ids ids
     * @return 自习室采购记录
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(purchaseDeviceInfoService.removeByIds(ids));
    }
}
