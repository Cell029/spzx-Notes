package com.cell.spzx.common.utils;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.cell.spzx.common.properties.MinioProperties;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class MinioUtil {

    // 配置 log
    private static final Logger log = Logger.getLogger(MinioUtil.class.getName());

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final Set<String> ALLOWED = Set.of("image/png", "image/jpeg", "image/jpg", "image/gif");

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;

    /**
     * 上传文件到 Minio 临时目录
     * @param file
     * @return
     */
    public String upload(MultipartFile file) {
        // 获取文件的扩展名
        String ext = getExt(file);
        // 设置日期目录
        String datePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
        // 时间戳 + 随机数，避免重复
        String dir = datePath + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6).replace("_", "") + "." + ext;
        // 存入桶中的文件最终路径
        String avatarName = "/" + minioProperties.getRegion() + "/temp/" + dir;
        try (InputStream is = file.getInputStream()) {
            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(avatarName)
                    .stream(is, file.getSize(), -1) // partSize(-1 表示 sdk 自己决定)
                    .contentType(file.getContentType()) // 文件类型
                    .build();
            minioClient.putObject(putArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return minioProperties.getEndpointUrl() + "/" + minioProperties.getBucketName() + avatarName;
    }

    /**
     * 获取文件扩展名
     * @param file
     * @return
     */
    @NotNull
    public String getExt(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件太大，最大 " + (MAX_FILE_SIZE / 1024 / 1024) + " MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("不支持的文件类型: " + contentType);
        }

        // 扩展名
        String original = file.getOriginalFilename();
        String ext = "png";
        // 当原始文件没有后缀时，则默认使用 png，否则使用原始图片的类型
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.') + 1);
        }
        return ext;
    }

    /**
     * 将临时目录下的文件拷贝一份到使用中目录
     * 只有当前的 URL 是临时路径，才进行 Minio 中的路径拷贝
     */
    public String copyMinioTempToCurrent(String currentUrl) {
        String region = minioProperties.getRegion();
        // 只有新增用户时点击了上传头像再进行路径的转移
        if (StringUtils.isNotBlank(currentUrl) && currentUrl.contains(region + "/temp")) {
            // 把存储在 Minio 临时目录的 URL 保存到固定目录下，同时把固定目录 URL 保存到数据库中
            String useUrl = currentUrl.replace(region + "/temp/", region + "/use/");
            String newMinioUrl = getMinioObjectName(useUrl);
            String tempMinioUrl = getMinioObjectName(currentUrl);
            try {
                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(minioProperties.getBucketName())
                                .object(newMinioUrl)
                                .source(CopySource.builder()
                                        .bucket(minioProperties.getBucketName())
                                        .object(tempMinioUrl)
                                        .build())
                                .build()
                );
                log.info("成功将临时目录中的 Logo 拷贝到使用中目录：" + newMinioUrl);
                return useUrl;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * 处理单个完整路径为 Minio 文件对象名
     * @param url
     * @return
     */
    @NotNull
    public String getMinioObjectName(String url) {
        return url.substring(
                url.indexOf(minioProperties.getBucketName())
                        + minioProperties.getBucketName().length() + 1 // 最后加 1 是用来去掉桶名后的斜杠 "/"
        );
    }

    /**
     * 批量处理完整路径为 Minio 文件对象名
     * @param urls
     * @return
     */
    @NotNull
    public List<String> getBatchMinioObjectName(List<String> urls) {
        return urls.stream().map(url -> url.substring(
                url.indexOf(minioProperties.getBucketName())
                        + minioProperties.getBucketName().length() + 1 // 最后加 1 是用来去掉桶名后的斜杠 "/"
        )).collect(Collectors.toList());
    }

    /**
     * 获取指定目录下的所有文件
     * @param prefix 传递指定的前缀名（是哪个目录）
     */
    public Iterable<Result<Item>> getResults(String prefix) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        // 通过配置文件获取需要遍历的具体目录
                        .prefix(minioProperties.getRegion() + prefix)
                        .recursive(true) // 递归遍历子目录
                        .build()
        );
    }

    /*@NotNull
    public List<DeleteObject> getDeleteObjects(List<String> urls) {
        return urls.stream().map(url -> new DeleteObject(url.substring(
                url.indexOf(minioProperties.getBucketName())
                        + minioProperties.getBucketName().length() + 1 // 最后加 1 是用来去掉桶名后的斜杠 "/"
        ))).collect(Collectors.toList());
    }*/

    /**
     * 批量删除 Minio 中的文件
     * @param t 传递值可以为 List<String>（完整路径集合）；或者为 Iterable<Result<Item>>（从 Minio 指定文件夹下遍历出的所有文件）
     * @param <T>
     */
    public <T> void deleteBatchMinioFile(T t) {
        List<DeleteObject> deleteObjects = new ArrayList<>();
        if (t == null) {
            log.warning("批量删除文件列表为空");
            return;
        }
        // 如果传递来的是集合，那证明传来的是完整路径的文件 URL
        if (t instanceof List<?>) {
            for (Object obj : (List<?>) t) {
                if (obj instanceof String) {
                    deleteObjects.add(new DeleteObject(getMinioObjectName((String) obj)));
                }
            }
        } else if (t instanceof Iterable) {
            for (Object obj : (Iterable<?>) t) {
                if (obj instanceof Result) {
                    Result<Item> resultItem = (Result<Item>) obj;
                    String objectName;
                    try {
                        objectName = resultItem.get().objectName();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    deleteObjects.add(new DeleteObject(objectName));
                }
            }
        }
        if (!deleteObjects.isEmpty()) {
            // 调用批量删除
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .objects(deleteObjects)
                            .build()
            );
            // 必须遍历结果，否则删除可能不会执行
            int errorCount = 0;
            for (Result<DeleteError> result : results) {
                errorCount++;
                try {
                    DeleteError error = result.get();
                    // 打印错误信息
                    log.log(Level.SEVERE, "MinIO 批量删除失败，文件：" + error.objectName() + "，错误：" + error.message());
                } catch (Exception e) {
                    log.log(Level.SEVERE, "批量删除 MinIO 文件时发生异常", e);
                }
            }
            log.info("批量删除完成，共处理 " + (deleteObjects.size() - errorCount) + " 个文件");
        } else {
            log.log(Level.SEVERE, "MinIO 文件为空，无需删除!");
        }
    }

    /**
     * 根据传递的完整文件路径删除 Minio 中的文件
     * @param url
     */
    public void deleteMinioOldFile(String url) {
        if (url != null && !url.isEmpty()) {
            String deleteMinioUrl = getMinioObjectName(url);
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(deleteMinioUrl)
                        .build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
