package com.cell.spzx.common.schedule;

import com.cell.spzx.common.utils.MinioUtil;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@EnableScheduling
public class MinioSchedule {

    // 配置 log
    private static final Logger log = Logger.getLogger(MinioSchedule.class.getName());

    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private RedissonClient redissonClient;

    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLatest3Days() throws Exception {
        log.info("开始扫描 Minio 临时目录！");
        // 1. 获取分布式锁
        RLock rLock = redissonClient.getLock("minioDelete-lock");
        // 2. 加锁
        boolean isLock = rLock.tryLock(5, 10,TimeUnit.SECONDS); // 最多等待 5s，当锁持有时间 10 分钟自动释放
        if (!isLock) {
            log.info("其他实例正在执行 Minio 清理任务，本次任务跳过");
            return;
        }
        try {
            String tempPrefix = "/temp/";
            // 遍历获取指定目录下的所有文件
            Iterable<Result<Item>> results = minioUtil.getResults(tempPrefix);
            // 批量删除
            minioUtil.deleteBatchMinioFile(results);
        } finally {
            rLock.unlock();
        }
    }
}
