package com.cell.spzx.common.schedule;

import com.cell.spzx.common.feign.OrderStatisticsFeignClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@EnableScheduling
public class OrderStatisticsSchedule {

    // 配置 log
    private static final Logger log = Logger.getLogger(OrderStatisticsSchedule.class.getName());

    @Autowired
    private OrderStatisticsFeignClient orderStatisticsFeignClient;

    /**
     * 每天 2:00 统计前一天的营业额
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void calculateTurnover() throws Exception {
        // 获取前一天的日期
        LocalDateTime startTime = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime endTime = LocalDate.now().minusDays(1).atTime(23, 59, 59);
        // 格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startStr = startTime.format(formatter);
        String endStr = endTime.format(formatter);
        log.info("开始统计 " + startStr + "~" + endStr + " 的营业额！");
        orderStatisticsFeignClient.calculateTurnover();
    }

}
