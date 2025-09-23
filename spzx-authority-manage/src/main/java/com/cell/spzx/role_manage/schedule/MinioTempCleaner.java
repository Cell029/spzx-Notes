package com.cell.spzx.role_manage.schedule;

import com.cell.spzx.common.properties.MinioProperties;
import com.cell.spzx.role_manage.service.impl.SysUserServiceImpl;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

@Service
public class MinioTempCleaner {

    // 配置 log
    private static final Logger log = Logger.getLogger(SysUserServiceImpl.class.getName());

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;


    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLatest3Days() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("开始扫描 Minio 临时目录！");
        String bucketName = minioProperties.getBucketName();
        String tempPrefix = "avatars/temp/";
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(tempPrefix)
                        .recursive(true) // 递归遍历子目录
                        .build()
        );
        for (Result<Item> result : results) {
            Item item = result.get();
            String objectName = item.objectName();
            log.info("当前文件：" + objectName);
            // 判断文件时间是否超过一天（避免刚上传的临时文件立刻被删）
            /*if (isExpired(item)) {
                log.info("执行删除 Minio 临时目录！");
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );
            }*/
            log.info("执行删除 Minio 临时目录！");
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        }
    }

    /**
     * 判断对象是否超过一天
     */
    private boolean isExpired(Item item) {
        // Minio Item 的 lastModified() 返回 OffsetDateTime
        OffsetDateTime lastModified = item.lastModified().toOffsetDateTime();

        // 转换为带具体时区的 ZonedDateTime
        ZoneId zone = ZoneId.of("Asia/Shanghai");
        ZonedDateTime localTime = lastModified.atZoneSameInstant(zone);

        log.info("该文件 " + item.objectName() + " 最后修改时间为：" + localTime);

        // 判断是否过期（按北京时间来算）
        return localTime.isBefore(ZonedDateTime.now(zone).minusDays(1));
    }



}
