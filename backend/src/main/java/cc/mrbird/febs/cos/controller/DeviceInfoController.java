package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.DeviceInfo;
import cc.mrbird.febs.cos.service.IDeviceInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 自习室管理
 *
 * @author Fank gmail - fan1ke2ke@gmail.com
 */
@RestController
@RequestMapping("/cos/device-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeviceInfoController {

    private final IDeviceInfoService deviceInfoService;

    /**
     * 分页获取自习室信息
     *
     * @param page       分页对象
     * @param deviceInfo 自习室信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<DeviceInfo> page, DeviceInfo deviceInfo) {
        return R.ok(deviceInfoService.queryDevicePage(page, deviceInfo));
    }

    /**
     * 获取设备状态信息
     *
     * @return 结果
     */
    @GetMapping("/queryDeviceList")
    public R queryDeviceList() {
        return R.ok(deviceInfoService.queryDeviceList());
    }

    /**
     * 更新自习室上下架状态
     *
     * @param id   主键
     * @param flag 状态
     * @return 结果
     */
    @GetMapping("/updateDevicePutFlag")
    public R updateDevicePutFlag(@RequestParam("id") Integer id, @RequestParam("flag") Integer flag) {
        return R.ok(deviceInfoService.update(Wrappers.<DeviceInfo>lambdaUpdate().set(DeviceInfo::getOutFlag, flag).eq(DeviceInfo::getId, id)));
    }

    /**
     * 获取当前可用自习室列表
     *
     * @return 结果
     */
    @GetMapping("/queryOnlineDevice")
    public R queryOnlineDevice() {
        return R.ok(deviceInfoService.queryOnlineDevice());
    }

    /**
     * 自习室信息详情
     *
     * @param id 自习室ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(deviceInfoService.getById(id));
    }

    /**
     * 自习室信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(deviceInfoService.list());
    }

    /**
     * 新增自习室信息
     *
     * @param deviceInfo 自习室信息
     * @return 结果
     */
    @PostMapping
    public R save(DeviceInfo deviceInfo) {
        deviceInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        return R.ok(deviceInfoService.save(deviceInfo));
    }

    /**
     * 修改自习室信息
     *
     * @param deviceInfo 自习室信息
     * @return 结果
     */
    @PutMapping
    public R edit(DeviceInfo deviceInfo) {
        return R.ok(deviceInfoService.updateById(deviceInfo));
    }

    /**
     * 删除自习室信息
     *
     * @param ids ids
     * @return 自习室信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(deviceInfoService.removeByIds(ids));
    }
}
