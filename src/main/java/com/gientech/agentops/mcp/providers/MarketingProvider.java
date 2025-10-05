/*
 * MIT License
 *
 * Copyright (c) 2025 com.gientech.agentops
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.gientech.agentops.mcp.providers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MarketingProvider {
    private static final Logger logger = LoggerFactory.getLogger(MarketingProvider.class);
    private static final Random random = new Random();

    /**
     * 推荐适合客户的优惠券
     * @param customerId 客户ID
     * @param customerName 客户姓名
     * @param customerType 客户类型（VIP/普通/企业等）
     * @return 推荐的优惠券列表
     */
    @McpTool(description = "推荐适合客户的优惠券")
    public Map<String, Object> recommendCoupons(
            @McpToolParam(description = "客户ID") String customerId,
            @McpToolParam(description = "客户姓名") String customerName,
            @McpToolParam(description = "客户类型") String customerType) {

        try {
            // 模拟根据客户类型推荐不同优惠券
            List<Map<String, Object>> coupons = new ArrayList<>();

            // 根据客户类型生成推荐优惠券
            if ("VIP".equalsIgnoreCase(customerType)) {
                // VIP客户推荐高价值优惠券
                Map<String, Object> coupon1 = new HashMap<>();
                coupon1.put("couponId", "V" + System.currentTimeMillis());
                coupon1.put("couponName", "VIP专享大额券");
                coupon1.put("discountAmount", "100.00");
                coupon1.put("condition", "满500元可用");
                coupon1.put("validUntil", LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                coupon1.put("category", "全场通用");
                coupons.add(coupon1);

                Map<String, Object> coupon2 = new HashMap<>();
                coupon2.put("couponId", "V" + (System.currentTimeMillis() + 1));
                coupon2.put("couponName", "VIP专属优惠");
                coupon2.put("discountAmount", "50.00");
                coupon2.put("condition", "满200元可用");
                coupon2.put("validUntil", LocalDate.now().plusDays(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                coupon2.put("category", "指定商品");
                coupons.add(coupon2);
            } else {
                // 普通客户推荐一般优惠券
                Map<String, Object> coupon1 = new HashMap<>();
                coupon1.put("couponId", "C" + System.currentTimeMillis());
                coupon1.put("couponName", "新客户优惠券");
                coupon1.put("discountAmount", "20.00");
                coupon1.put("condition", "满100元可用");
                coupon1.put("validUntil", LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                coupon1.put("category", "全场通用");
                coupons.add(coupon1);

                Map<String, Object> coupon2 = new HashMap<>();
                coupon2.put("couponId", "C" + (System.currentTimeMillis() + 1));
                coupon2.put("couponName", "日常优惠券");
                coupon2.put("discountAmount", "10.00");
                coupon2.put("condition", "满50元可用");
                coupon2.put("validUntil", LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                coupon2.put("category", "指定分类");
                coupons.add(coupon2);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("customerId", customerId);
            result.put("customerName", customerName);
            result.put("recommendedCoupons", coupons);
            result.put("recommendationCount", coupons.size());

            logger.info("为客户[{}]推荐了{}张优惠券", customerName, coupons.size());
            return result;

        } catch (Exception e) {
            logger.error("推荐优惠券失败", e);
            return createErrorResponse("推荐失败：" + e.getMessage());
        }
    }

    /**
     * 查询客户的优惠券余额
     * @param customerId 客户ID
     * @param customerName 客户姓名
     * @return 客户持有的优惠券列表
     */
    @McpTool(description = "查询客户的优惠券余额")
    public Map<String, Object> queryCustomerCoupons(
            @McpToolParam(description = "客户ID") String customerId,
            @McpToolParam(description = "客户姓名") String customerName) {

        try {
            // 模拟查询客户持有的优惠券
            List<Map<String, Object>> coupons = new ArrayList<>();

            // 模拟一些优惠券数据
            String[] couponTypes = {"满减券", "折扣券", "免运费券", "积分翻倍券"};

            for (int i = 0; i < 5; i++) {
                Map<String, Object> coupon = new HashMap<>();
                coupon.put("couponId", "COUP" + (System.currentTimeMillis() + i));
                coupon.put("couponName", "优惠券" + (i+1));
                coupon.put("type", couponTypes[i % couponTypes.length]);
                coupon.put("discountAmount", new BigDecimal(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP).toString());
                coupon.put("condition", "满" + new BigDecimal(random.nextDouble() * 200 + 50).setScale(0, RoundingMode.HALF_UP).toString() + "元可用");
                coupon.put("validUntil", LocalDate.now().plusDays(random.nextInt(30) + 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                coupon.put("status", "未使用");
                coupons.add(coupon);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("customerId", customerId);
            result.put("customerName", customerName);
            result.put("coupons", coupons);
            result.put("totalCoupons", coupons.size());

            logger.info("查询客户[{}]的优惠券余额成功，共{}张", customerName, coupons.size());
            return result;

        } catch (Exception e) {
            logger.error("查询优惠券余额失败", e);
            return createErrorResponse("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询营销活动列表
     * @param status 活动状态（ACTIVE/ENDED/UPCOMING）
     * @return 营销活动列表
     */
    @McpTool(description = "查询营销活动列表")
    public Map<String, Object> queryMarketingActivities(
            @McpToolParam(description = "活动状态") String status) {

        try {
            List<Map<String, Object>> activities = new ArrayList<>();

            // 模拟营销活动数据
            String[] activityNames = {
                    "春季大促活动", "夏日清凉节", "秋季丰收季", "年终大促", "会员日特惠",
                    "新品首发活动", "品牌日促销", "节日特惠", "限时秒杀", "满减优惠"
            };

            String[] activityTypes = {"满减", "折扣", "买赠", "积分翻倍", "抽奖"};

            for (int i = 0; i < 5; i++) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("activityId", "ACT" + (System.currentTimeMillis() + i));
                activity.put("activityName", activityNames[i % activityNames.length]);
                activity.put("type", activityTypes[i % activityTypes.length]);
                activity.put("startDate", LocalDate.now().minusDays(random.nextInt(10)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                activity.put("endDate", LocalDate.now().plusDays(30 - random.nextInt(20)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                activity.put("status", status != null ? status : "ACTIVE");
                activity.put("targetAudience", "全体客户");
                activity.put("budget", new BigDecimal(random.nextDouble() * 10000 + 5000).setScale(2, RoundingMode.HALF_UP).toString());
                activities.add(activity);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("activities", activities);
            result.put("activityCount", activities.size());
            result.put("statusFilter", status != null ? status : "ALL");

            logger.info("查询营销活动列表成功，共{}个活动", activities.size());
            return result;

        } catch (Exception e) {
            logger.error("查询营销活动失败", e);
            return createErrorResponse("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询客户的积分余额
     * @param customerId 客户ID
     * @param customerName 客户姓名
     * @return 客户积分信息
     */
    @McpTool(description = "查询客户的积分余额")
    public Map<String, Object> queryCustomerPoints(
            @McpToolParam(description = "客户ID") String customerId,
            @McpToolParam(description = "客户姓名") String customerName) {

        try {
            // 模拟查询积分信息
            Map<String, Object> pointsInfo = new HashMap<>();
            pointsInfo.put("customerId", customerId);
            pointsInfo.put("customerName", customerName);
            pointsInfo.put("currentPoints", random.nextInt(5000) + 1000); // 1000-6000分
            pointsInfo.put("usedPoints", random.nextInt(2000)); // 已使用积分
            pointsInfo.put("expiredPoints", random.nextInt(500)); // 即将过期积分
            pointsInfo.put("totalEarned", random.nextInt(8000) + 2000); // 累计获得积分
            pointsInfo.put("level", "VIP" + (random.nextInt(3) + 1)); // VIP等级

            // 计算积分价值（假设100积分=1元）
            BigDecimal pointValue = new BigDecimal(pointsInfo.get("currentPoints").toString())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            pointsInfo.put("estimatedValue", pointValue.toString());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("pointsInfo", pointsInfo);

            logger.info("查询客户[{}]的积分余额成功，当前积分为{}", customerName, pointsInfo.get("currentPoints"));
            return result;

        } catch (Exception e) {
            logger.error("查询积分余额失败", e);
            return createErrorResponse("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询客户参与的营销活动
     * @param customerId 客户ID
     * @param customerName 客户姓名
     * @return 客户参与的活动列表
     */
    @McpTool(description = "查询客户参与的营销活动")
    public Map<String, Object> queryCustomerActivities(
            @McpToolParam(description = "客户ID") String customerId,
            @McpToolParam(description = "客户姓名") String customerName) {

        try {
            List<Map<String, Object>> activities = new ArrayList<>();

            // 模拟客户参与的活动
            String[] activityNames = {
                    "春节大促", "会员日", "新品体验", "夏季清仓", "品牌日"
            };

            String[] participationStatuses = {"已完成", "进行中", "已报名", "已放弃"};

            for (int i = 0; i < 4; i++) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("activityId", "PART" + (System.currentTimeMillis() + i));
                activity.put("activityName", activityNames[i % activityNames.length]);
                activity.put("joinDate", LocalDate.now().minusDays(random.nextInt(60)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                activity.put("status", participationStatuses[i % participationStatuses.length]);
                activity.put("rewardAmount", new BigDecimal(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP).toString());
                activity.put("rewardType", i % 2 == 0 ? "积分" : "优惠券");
                activities.add(activity);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("customerId", customerId);
            result.put("customerName", customerName);
            result.put("participatedActivities", activities);
            result.put("activityCount", activities.size());

            logger.info("查询客户[{}]参与的营销活动成功，共{}个活动", customerName, activities.size());
            return result;

        } catch (Exception e) {
            logger.error("查询客户营销活动失败", e);
            return createErrorResponse("查询失败：" + e.getMessage());
        }
    }

    /**
     * 发放优惠券给指定客户
     * @param customerId 客户ID
     * @param couponTemplateId 优惠券模板ID
     * @param reason 发放原因
     * @return 发放结果
     */
    @McpTool(description = "发放优惠券给指定客户")
    public Map<String, Object> issueCouponToCustomer(
            @McpToolParam(description = "客户ID") String customerId,
            @McpToolParam(description = "优惠券模板ID") String couponTemplateId,
            @McpToolParam(description = "发放原因") String reason) {

        try {
            // 模拟发放优惠券
            String couponInstanceId = "INST" + System.currentTimeMillis();
            String couponName = "系统发放优惠券";
            String discountAmount = new BigDecimal(random.nextDouble() * 200 + 10).setScale(2, RoundingMode.HALF_UP).toString();
            String validUntil = LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            Map<String, Object> issuedCoupon = new HashMap<>();
            issuedCoupon.put("couponInstanceId", couponInstanceId);
            issuedCoupon.put("couponTemplateId", couponTemplateId);
            issuedCoupon.put("couponName", couponName);
            issuedCoupon.put("discountAmount", discountAmount);
            issuedCoupon.put("validUntil", validUntil);
            issuedCoupon.put("status", "已发放");

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("customerId", customerId);
            result.put("reason", reason);
            result.put("issuedCoupon", issuedCoupon);
            result.put("message", "优惠券发放成功");

            logger.info("向客户[{}]发放优惠券成功，优惠券ID: {}", customerId, couponInstanceId);
            return result;

        } catch (Exception e) {
            logger.error("发放优惠券失败", e);
            return createErrorResponse("发放失败：" + e.getMessage());
        }
    }

    /**
     * 查询营销活动效果统计
     * @param activityId 活动ID
     * @param activityName 活动名称
     * @return 活动效果统计
     */
    @McpTool(description = "查询营销活动效果统计")
    public Map<String, Object> queryActivityStatistics(
            @McpToolParam(description = "活动ID") String activityId,
            @McpToolParam(description = "活动名称") String activityName) {

        try {
            // 模拟活动效果统计
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("activityId", activityId);
            statistics.put("activityName", activityName);
            statistics.put("participants", random.nextInt(10000) + 1000); // 参与人数
            statistics.put("conversionRate", new BigDecimal(random.nextDouble() * 30 + 5).setScale(2, RoundingMode.HALF_UP).toString() + "%"); // 转化率
            statistics.put("revenueGenerated", new BigDecimal(random.nextDouble() * 100000 + 10000).setScale(2, RoundingMode.HALF_UP).toString()); // 带来的收入
            statistics.put("cost", new BigDecimal(random.nextDouble() * 20000 + 5000).setScale(2, RoundingMode.HALF_UP).toString()); // 活动成本
            statistics.put("roi", new BigDecimal(random.nextDouble() * 5 + 1).setScale(2, RoundingMode.HALF_UP).toString()); // 投资回报率
            statistics.put("engagementRate", new BigDecimal(random.nextDouble() * 50 + 10).setScale(2, RoundingMode.HALF_UP).toString() + "%"); // 参与度

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("statistics", statistics);
            result.put("status", "统计完成");

            logger.info("查询营销活动[{}]的效果统计成功", activityName);
            return result;

        } catch (Exception e) {
            logger.error("查询活动统计失败", e);
            return createErrorResponse("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询营销渠道效果
     * @param channelType 渠道类型（SMS/EMAIL/PUSH/APP等）
     * @return 渠道效果统计
     */
    @McpTool(description = "查询营销渠道效果")
    public Map<String, Object> queryChannelEffectiveness(
            @McpToolParam(description = "渠道类型") String channelType) {

        try {
            List<Map<String, Object>> channelStats = new ArrayList<>();

            // 模拟多个营销渠道的效果
            String[] channels = {"SMS", "EMAIL", "PUSH", "APP", "WECHAT", "WEBSITE"};
            String[] channelNames = {"短信营销", "邮件营销", "推送通知", "APP消息", "微信消息", "网站横幅"};

            for (int i = 0; i < channels.length; i++) {
                Map<String, Object> channelStat = new HashMap<>();
                channelStat.put("channelType", channels[i]);
                channelStat.put("channelName", channelNames[i]);
                channelStat.put("sentCount", random.nextInt(50000) + 5000); // 发送数量
                channelStat.put("openRate", new BigDecimal(random.nextDouble() * 80 + 5).setScale(2, RoundingMode.HALF_UP).toString() + "%"); // 打开率
                channelStat.put("clickRate", new BigDecimal(random.nextDouble() * 20 + 1).setScale(2, RoundingMode.HALF_UP).toString() + "%"); // 点击率
                channelStat.put("conversionRate", new BigDecimal(random.nextDouble() * 10 + 0.1).setScale(2, RoundingMode.HALF_UP).toString() + "%"); // 转化率
                channelStat.put("cost", new BigDecimal(random.nextDouble() * 5000 + 100).setScale(2, RoundingMode.HALF_UP).toString()); // 成本
                channelStat.put("revenue", new BigDecimal(random.nextDouble() * 15000 + 500).setScale(2, RoundingMode.HALF_UP).toString()); // 收入
                channelStats.add(channelStat);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("channelStats", channelStats);
            result.put("channelCount", channelStats.size());
            result.put("filterChannel", channelType != null ? channelType : "ALL");

            logger.info("查询营销渠道效果成功，共{}个渠道", channelStats.size());
            return result;

        } catch (Exception e) {
            logger.error("查询渠道效果失败", e);
            return createErrorResponse("查询失败：" + e.getMessage());
        }
    }

    // 辅助方法：创建错误响应
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        return result;
    }
}
