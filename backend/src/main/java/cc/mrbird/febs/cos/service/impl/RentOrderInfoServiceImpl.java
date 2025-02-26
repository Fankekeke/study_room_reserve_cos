package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.dao.DeviceInfoMapper;
import cc.mrbird.febs.cos.dao.StaffInfoMapper;
import cc.mrbird.febs.cos.dao.UserInfoMapper;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.dao.RentOrderInfoMapper;
import cc.mrbird.febs.cos.service.*;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Fank gmail - fan1ke2ke@gmail.com
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RentOrderInfoServiceImpl extends ServiceImpl<RentOrderInfoMapper, RentOrderInfo> implements IRentOrderInfoService {

    private final DeviceInfoMapper deviceInfoMapper;

    private final IDeviceInfoService deviceInfoService;

    private final UserInfoMapper userInfoMapper;

    private final IPaymentRecordInfoService paymentRecordInfoService;

    private final ICreditRecordInfoService creditRecordInfoService;

    private final IMessageInfoService messageInfoService;

    private final StaffInfoMapper staffInfoMapper;

    private final IBulletinInfoService bulletinInfoService;

    private final TemplateEngine templateEngine;

    private final IMailService mailService;

    @Autowired
    private PayService payService;

    /**
     * 分页获取租借订单
     *
     * @param page          分页对象
     * @param rentOrderInfo 租借订单
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryRentOrderPage(Page<RentOrderInfo> page, RentOrderInfo rentOrderInfo) {
        return baseMapper.queryRentOrderPage(page, rentOrderInfo);
    }

    /**
     * 计算订单金额
     *
     * @param rentOrderInfo 订单信息
     * @return 结果
     */
    @Override
    public RentOrderInfo calculateOrderPrice(RentOrderInfo rentOrderInfo) throws FebsException {
        // 设置起租时间
        rentOrderInfo.setStartDate(DateUtil.formatDateTime(new Date()));
        // 判断维修结束时间是否小于等于当前时间
        if (DateUtil.compare(DateUtil.parseDateTime(rentOrderInfo.getEndDate()), new Date()) <= 0) {
            throw new FebsException("租赁结束时间不能小于当前时间");
        }
        // 计算租赁小时
        if (rentOrderInfo.getStartDate() != null && rentOrderInfo.getEndDate() != null) {
            long startTime = DateUtil.parse(rentOrderInfo.getStartDate()).getTime();
            long endTime = DateUtil.parse(rentOrderInfo.getEndDate()).getTime();
            long hours = (endTime - startTime) / (1000 * 60 * 60);
            rentOrderInfo.setRentHour((int) hours);
        }

        rentOrderInfo.setStatus("0");
        rentOrderInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        rentOrderInfo.setStartDate(DateUtil.formatDateTime(new Date()));

        // 获取用户信息
        UserInfo userInfo = userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, rentOrderInfo.getUserId()));
        // 获取自习室信息
        if (rentOrderInfo.getDeviceId() != null) {
            // 获取自习室信息
            DeviceInfo deviceInfo = deviceInfoMapper.selectById(rentOrderInfo.getDeviceId());
            // 租借价格
            BigDecimal rentPrice = NumberUtil.mul(deviceInfo.getUnitPrice(), rentOrderInfo.getRentHour());
            rentOrderInfo.setUnitPrice(deviceInfo.getUnitPrice());

            // 押金
            if (userInfo != null && userInfo.getCreditScore() > 90) {
                rentOrderInfo.setDepositPrice(BigDecimal.ZERO);
            } else {
                rentOrderInfo.setDepositPrice(deviceInfo.getDepositPrice());
            }
            rentOrderInfo.setTotalPrice(NumberUtil.add(rentPrice, rentOrderInfo.getDepositPrice()));
        }
        return rentOrderInfo;
    }

    /**
     * 新增租借订单
     *
     * @param rentOrderInfo 租借订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addRentOrder(RentOrderInfo rentOrderInfo) throws AlipayApiException {
        rentOrderInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        // 设置租借订单编号
        rentOrderInfo.setCode("OR-" + System.currentTimeMillis());
        // 设置用户ID
        UserInfo userInfo = userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, rentOrderInfo.getUserId()));
        if (userInfo != null) {
            rentOrderInfo.setUserId(userInfo.getId());
        }
        rentOrderInfo.setStatus("0");
        this.save(rentOrderInfo);

        // 更新设备状态
        // 获取自习室信息
        DeviceInfo deviceInfo = deviceInfoMapper.selectById(rentOrderInfo.getDeviceId());
        deviceInfo.setStatus("2");
        deviceInfoMapper.updateById(deviceInfo);

        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(rentOrderInfo.getCode());
        alipayBean.setSubject(rentOrderInfo.getCode() + "缴费信息");
        alipayBean.setTotal_amount(String.valueOf(rentOrderInfo.getTotalPrice()));
        alipayBean.setBody("");
        String result = payService.aliPay(alipayBean);
        return result;
    }

    /**
     * 支付回调
     *
     * @param orderCode 订单编号
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean callbackPayment(String orderCode) {
        RentOrderInfo rentOrderInfo = this.getOne(Wrappers.<RentOrderInfo>lambdaQuery().eq(RentOrderInfo::getCode, orderCode));
        // 设置自习室状态
        DeviceInfo deviceInfo = deviceInfoMapper.selectOne(Wrappers.<DeviceInfo>lambdaQuery().eq(DeviceInfo::getId, rentOrderInfo.getDeviceId()));
        deviceInfo.setStatus("2");
        deviceInfoMapper.updateById(deviceInfo);
        // 更新订单状态
        rentOrderInfo.setStatus("1");
        // 添加支付记录
        PaymentRecordInfo paymentInfo = new PaymentRecordInfo();
        paymentInfo.setOrderCode(orderCode);
        paymentInfo.setUserId(rentOrderInfo.getUserId());
        paymentInfo.setOrderPrice(rentOrderInfo.getTotalPrice());
        paymentInfo.setPayDate(DateUtil.formatDateTime(new Date()));
        paymentRecordInfoService.save(paymentInfo);

        // 添加支付消息通知
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setUserId(rentOrderInfo.getUserId());
        messageInfo.setContent("您有一笔租借订单已完成支付，请在 " + rentOrderInfo.getEndDate() + " 前及时归还设备");
        messageInfo.setStatus("0");
        messageInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        messageInfoService.save(messageInfo);

        // 获取用户信息
        UserInfo userInfo = userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, rentOrderInfo.getUserId()));
        // 发送邮件
        if (userInfo != null && StrUtil.isNotEmpty(userInfo.getEmail())) {
            Context context = new Context();
            context.setVariable("today", DateUtil.formatDate(new Date()));
            context.setVariable("custom", userInfo.getName() + "，您有一笔租借订单已完成支付，请在 " + rentOrderInfo.getEndDate() + " 前及时归还设备，请注意查看");
            String emailContent = templateEngine.process("registerEmail", context);
            mailService.sendHtmlMail(userInfo.getEmail(), DateUtil.formatDate(new Date()) + "支付提示", emailContent);
        }

        return this.updateById(rentOrderInfo);
    }

    /**
     * 校验用户是否还有未归还订单
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public boolean checkOrderByUser(Integer userId) throws FebsException {
        // 获取用户信息
        UserInfo userInfo = userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
        if (userInfo != null) {
            if (userInfo.getCreditScore() < 60) {
                throw new FebsException("用户信用分不足，无法进行租借");
            }
            // 获取用户未归还订单
            int count = this.count(Wrappers.<RentOrderInfo>lambdaQuery().eq(RentOrderInfo::getUserId, userInfo.getId()).eq(RentOrderInfo::getStatus, "1"));
            if (count > 0) {
                throw new FebsException("用户还有未归还订单，无法进行租借");
            }
            // 获取用户是否还有未支付订单
            int count1 = this.count(Wrappers.<RentOrderInfo>lambdaQuery().eq(RentOrderInfo::getUserId, userInfo.getId()).eq(RentOrderInfo::getStatus, "0"));
            if (count1 > 0) {
                throw new FebsException("用户还有未支付的订单，无法进行租借");
            }
        }
        return true;
    }

    /**
     * 定时任务计算已过期订单
     */
    @Scheduled(cron ="0 0/5 * * * ? ")
    void checkReturnOrderList() {
        // 获取所有未支付订单
        List<RentOrderInfo> list = this.list(Wrappers.<RentOrderInfo>lambdaQuery().eq(RentOrderInfo::getStatus, "0"));
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        // 待更新订单
        List<RentOrderInfo> updateList = new ArrayList<>();
        // 消息通知
        List<MessageInfo> messageList = new ArrayList<>();
        // 自习室IDS
        List<Integer> deviceIdList = new ArrayList<>();

        // 校验订单是否过期
        for (RentOrderInfo rentOrderInfo : list) {
            if (DateUtil.compare(DateUtil.parseDateTime(rentOrderInfo.getEndDate()), new Date()) < 0) {
                rentOrderInfo.setStatus("2");
                // 设置消息内容
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setUserId(rentOrderInfo.getUserId());
                messageInfo.setContent("你好你的订单 “" + rentOrderInfo.getCode() + "” 已过期");
                messageInfo.setStatus("0");
                messageInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
                updateList.add(rentOrderInfo);
                messageList.add(messageInfo);

                deviceIdList.add(rentOrderInfo.getDeviceId());
            }
        }

        if (CollectionUtil.isNotEmpty(updateList)) {
            this.updateBatchById(updateList);
        }
        if (CollectionUtil.isNotEmpty(messageList)) {
            messageInfoService.saveBatch(messageList);
        }
        if (CollectionUtil.isNotEmpty(deviceIdList)) {
            deviceInfoService.update(Wrappers.<DeviceInfo>lambdaUpdate().set(DeviceInfo::getStatus, "1").in(DeviceInfo::getId, deviceIdList));
        }
    }

    /**
     * 归还设备
     *
     * @param orderCode 订单编号
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnDevice(String orderCode) {
        // 获取订单信息
        RentOrderInfo rentOrderInfo = this.getOne(Wrappers.<RentOrderInfo>lambdaQuery().eq(RentOrderInfo::getCode, orderCode));
        // 获取用户信息
        UserInfo userInfo = userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getId, rentOrderInfo.getUserId()));
        // 逾期一次扣10分
        if (DateUtil.compare(DateUtil.parseDateTime(rentOrderInfo.getEndDate()), new Date()) < 0) {
            // 获取用户信息
            userInfo.setCreditScore(userInfo.getCreditScore() - 10);
            userInfoMapper.updateById(userInfo);
            // 添加信用积分记录
            CreditRecordInfo creditRecordInfo = new CreditRecordInfo();
            creditRecordInfo.setUserId(userInfo.getId());
            creditRecordInfo.setType("2");
            creditRecordInfo.setScore(10);
            creditRecordInfo.setContent("订单-" + rentOrderInfo.getCode() + "逾期归还，信用分-10");
            creditRecordInfo.setAfterScore(userInfo.getCreditScore());
            creditRecordInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
            creditRecordInfoService.save(creditRecordInfo);

            // 添加消息通知
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUserId(rentOrderInfo.getUserId());
            messageInfo.setContent("订单-" + rentOrderInfo.getCode() + "逾期归还，信用分-10");
            messageInfo.setStatus("0");
            messageInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
            messageInfoService.save(messageInfo);

            // 发送邮件
            if (StrUtil.isNotEmpty(userInfo.getEmail())) {
                Context context = new Context();
                context.setVariable("today", DateUtil.formatDate(new Date()));
                context.setVariable("custom", userInfo.getName() + "，订单-" + rentOrderInfo.getCode() + "逾期归还，信用分-10，请注意查看");
                String emailContent = templateEngine.process("registerEmail", context);
                mailService.sendHtmlMail(userInfo.getEmail(), DateUtil.formatDate(new Date()) + "归还提示", emailContent);
            }
        } else {
            userInfo.setCreditScore(userInfo.getCreditScore() + 10);
            userInfoMapper.updateById(userInfo);
            // 添加信用积分记录
            CreditRecordInfo creditRecordInfo = new CreditRecordInfo();
            creditRecordInfo.setUserId(userInfo.getId());
            creditRecordInfo.setType("1");
            creditRecordInfo.setScore(10);
            creditRecordInfo.setContent("订单-" + rentOrderInfo.getCode() + "在 " + rentOrderInfo.getEndDate() + " 前归还，信用分+10");
            creditRecordInfo.setAfterScore(userInfo.getCreditScore());
            creditRecordInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
            creditRecordInfoService.save(creditRecordInfo);

            // 添加消息通知
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setUserId(rentOrderInfo.getUserId());
            messageInfo.setContent("订单-" + rentOrderInfo.getCode() + "在 " + rentOrderInfo.getEndDate() + " 前归还，信用分+10");
            messageInfo.setStatus("0");
            messageInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
            messageInfoService.save(messageInfo);

            // 发送邮件
            if (StrUtil.isNotEmpty(userInfo.getEmail())) {
                Context context = new Context();
                context.setVariable("today", DateUtil.formatDate(new Date()));
                context.setVariable("custom", userInfo.getName() + "，订单-" + rentOrderInfo.getCode() + "在 " + rentOrderInfo.getEndDate() + " 前归还，信用分+10，请注意查看");
                String emailContent = templateEngine.process("registerEmail", context);
                mailService.sendHtmlMail(userInfo.getEmail(), DateUtil.formatDate(new Date()) + "归还提示", emailContent);
            }
        }
        rentOrderInfo.setStatus("2");
        rentOrderInfo.setReturnDate(DateUtil.formatDateTime(new Date()));
        return this.updateById(rentOrderInfo);
    }

    /**
     * 自习室归还审核入库
     *
     * @param orderCode 订单编号
     * @return 结果
     */
    @Override
    public boolean checkReturnDevice(String orderCode) {
        // 获取订单信息
        RentOrderInfo rentOrderInfo = this.getOne(Wrappers.<RentOrderInfo>lambdaQuery().eq(RentOrderInfo::getCode, orderCode));
        // 设置自习室状态
        DeviceInfo deviceInfo = deviceInfoMapper.selectOne(Wrappers.<DeviceInfo>lambdaQuery().eq(DeviceInfo::getId, rentOrderInfo.getDeviceId()));
        deviceInfo.setStatus("1");
        deviceInfoMapper.updateById(deviceInfo);
        // 订单状态完成
        rentOrderInfo.setStatus("3");
        return this.updateById(rentOrderInfo);
    }

    /**
     * 检测用户是否存在快超时租借订单
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public List<RentOrderInfo> rendOrderMessage(Integer userId) {
        // 获取用户信息
        UserInfo userInfo = userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
        if (userInfo == null) {
            return null;
        }
        // 获取用户订单未归还订单
        List<RentOrderInfo> orderList = this.list(Wrappers.<RentOrderInfo>lambdaQuery().eq(RentOrderInfo::getUserId, userInfo.getId()).eq(RentOrderInfo::getStatus, 1));
        if (CollectionUtil.isEmpty(orderList)) {
            return null;
        }

        List<RentOrderInfo> result = new ArrayList<>();
        // 校验订单中是否存在三小时后过期
        for (RentOrderInfo rentOrderInfo : orderList) {
            // 获取订单剩余时间
            long remainTime = DateUtil.between(new Date(), DateUtil.parseDateTime(rentOrderInfo.getEndDate()), DateUnit.HOUR);
            if (remainTime <= 3) {
                result.add(rentOrderInfo);
            }
        }
        return result;
    }

    /**
     * 根据订单编号查询订单详情
     *
     * @param orderCode 订单编号
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> queryOrderDetail(String orderCode) {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        // 查询订单信息
        RentOrderInfo rentOrderInfo = this.getOne(Wrappers.<RentOrderInfo>lambdaQuery().eq(RentOrderInfo::getCode, orderCode));
        // 查询用户信息
        UserInfo userInfo = userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getId, rentOrderInfo.getUserId()));
        // 查询设备信息
        DeviceInfo deviceInfo = deviceInfoMapper.selectOne(Wrappers.<DeviceInfo>lambdaQuery().eq(DeviceInfo::getId, rentOrderInfo.getDeviceId()));
        result.put("orderInfo", rentOrderInfo);
        result.put("userInfo", userInfo);
        result.put("deviceInfo", deviceInfo);
        return result;
    }

    /**
     * 首页数据
     *
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> homeData() {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        // 查询员工信息
        Integer staffNum = staffInfoMapper.selectCount(Wrappers.<StaffInfo>lambdaQuery().eq(StaffInfo::getDelFlag, "0"));

        // 总订单数量
        Integer orderNum = this.count(Wrappers.<RentOrderInfo>lambdaQuery().ne(RentOrderInfo::getStatus, 0));
        // 总收益
        List<PaymentRecordInfo> paymentRecordList = paymentRecordInfoService.list(Wrappers.<PaymentRecordInfo>lambdaQuery());
        BigDecimal amount = CollectionUtil.isEmpty(paymentRecordList) ? BigDecimal.ZERO : paymentRecordList.stream().map(PaymentRecordInfo::getOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<RentOrderInfo> orderListMonth = baseMapper.selectOrderInfoByMonth();
        List<RentOrderInfo> orderListYear = baseMapper.selectOrderInfoByYear();
        // 本月订单量
        Integer orderNumMonth = orderListMonth.size();
        // 本月收益
        BigDecimal orderAmountMonth = orderListMonth.stream().filter(e -> !"0".equals(e.getStatus())).map(RentOrderInfo::getTotalPrice).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 本年订单量
        Integer orderNumYear = orderListYear.size();
        // 本年收益
        BigDecimal orderAmountYear = orderListYear.stream().filter(e -> !"0".equals(e.getStatus())).map(RentOrderInfo::getTotalPrice).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 近十天内订单数量统计
        List<LinkedHashMap<String, Object>> orderNumDays = baseMapper.selectOrderNumDays();
        // 近十天内订单收益统计
        List<LinkedHashMap<String, Object>> orderAmountDays = baseMapper.selectOrderAmountDays();
        // 公告
        List<BulletinInfo> bulletinInfoList = bulletinInfoService.list();
        result.put("orderNumMonth", orderNumMonth);
        result.put("orderAmountMonth", orderAmountMonth);
        result.put("orderNumYear", orderNumYear);
        result.put("orderAmountYear", orderAmountYear);

        result.put("staffNum", staffNum);
        result.put("orderNum", orderNum);
        result.put("totalPrice", amount);
        result.put("orderNumDays", orderNumDays);
        result.put("orderAmountDays", orderAmountDays);
        result.put("bulletin", bulletinInfoList);
        return result;
    }
}
