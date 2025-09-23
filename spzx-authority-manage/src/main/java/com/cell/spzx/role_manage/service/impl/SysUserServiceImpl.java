package com.cell.spzx.role_manage.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cell.model.dto.h5.SysUserQueryDto;
import com.cell.model.entity.system.SysUser;
import com.cell.model.vo.h5.PageResult;
import com.cell.spzx.common.properties.MinioProperties;
import com.cell.spzx.role_manage.mapper.SysUserMapper;
import com.cell.spzx.role_manage.service.SysUserService;
import io.minio.*;
import io.minio.errors.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    // 配置 log
    private static final Logger log = Logger.getLogger(SysUserServiceImpl.class.getName());

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final Set<String> ALLOWED = Set.of("image/png", "image/jpeg", "image/jpg", "image/gif");

    @Override
    public PageResult<SysUser> selectSysUserPage(SysUserQueryDto sysUserQueryDto) {
        // 构造查询条件
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (sysUserQueryDto.getUsername() != null && !sysUserQueryDto.getUsername().isEmpty()) {
            lambdaQueryWrapper.like(SysUser::getUsername, sysUserQueryDto.getUsername());
        }
        if (sysUserQueryDto.getName() != null && !sysUserQueryDto.getName().isEmpty()) {
            lambdaQueryWrapper.like(SysUser::getName, sysUserQueryDto.getName());
        }
        if (sysUserQueryDto.getPhone() != null && !sysUserQueryDto.getPhone().isEmpty()) {
            lambdaQueryWrapper.eq(SysUser::getPhone, sysUserQueryDto.getPhone());
        }
        // 构造分页对象
        Page<SysUser> page = new Page<>(sysUserQueryDto.getPage(), sysUserQueryDto.getSize());
        // 查询后的结果
        Page<SysUser> sysRolePage = page(page, lambdaQueryWrapper);

        return new PageResult<SysUser>(sysRolePage.getTotal(), sysUserQueryDto.getPage(), sysRolePage.getRecords());
    }

    /*@Override
    public String uploadAvatar(MultipartFile file, Long id) {

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

        // 设置日期目录
        String datePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
        // 时间戳 + 随机数，避免重复
        String dir = datePath + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6).replace("_", "") + "." + ext;
        // 存入桶中的文件最终路径
        String avatarName = "/avatars/" + dir;
        try (InputStream is = file.getInputStream()) {
            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(avatarName)
                    .stream(is, file.getSize(), -1) // partSize(-1 表示 sdk 自己决定)
                    .contentType(file.getContentType()) // 文件类型
                    .build();
            minioClient.putObject(putArgs);
        } catch (Exception  e) {
            throw new RuntimeException(e);
        }

        // 去掉掉 MinIO 访问地址末尾的 "/"
        String endpoint = minioProperties.getEndpointUrl().replaceAll("/$", "");
        String publicUrl = endpoint + "/" + minioProperties.getBucketName() + "/" + avatarName;

        return publicUrl;
    }*/

    @Override
    public void addSysUser(SysUser sysUser) {
        // 检查传递来的 SysUser 中是否包含头像 URL
        String currentAvatarUrl = sysUser.getAvatar();
        String newAvatarUrl = copyMinioTempToCurrent(currentAvatarUrl);
        if (currentAvatarUrl != null) {
            sysUser.setAvatar(newAvatarUrl);
        }
        // 当 newAvatarUrl 为空时，说明没有进行拷贝，也就是没有进行上传头像的操作，那么存入数据库时使用空的头像 URL　即可（前端未点击上传时即为空）
        save(sysUser);
    }

    @Override
    public String uploadAvatar(MultipartFile file) {

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

        // 设置日期目录
        String datePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
        // 时间戳 + 随机数，避免重复
        String dir = datePath + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6).replace("_", "") + "." + ext;
        // 存入桶中的文件最终路径
        String avatarName = "/avatars/temp/" + dir;
        try (InputStream is = file.getInputStream()) {
            PutObjectArgs putArgs = PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(avatarName)
                    .stream(is, file.getSize(), -1) // partSize(-1 表示 sdk 自己决定)
                    .contentType(file.getContentType()) // 文件类型
                    .build();
            minioClient.putObject(putArgs);
        } catch (Exception  e) {
            throw new RuntimeException(e);
        }

        String publicUrl = minioProperties.getEndpointUrl() + "/" + minioProperties.getBucketName() + avatarName;

        return publicUrl;
    }

    @Override
    public SysUser selectSysUserById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional
    public void deleteSysUser(Long id) {
        // 根据 id 查询出当前用户的头像 URL，然后删除　Minio　中对应的文件
        String avatarUrl = getById(id).getAvatar();
        deleteMinioOldAvatar(avatarUrl);
        // 删除数据库中的用户信息
        removeById(id);
    }

    @Override
    @Transactional
    public void updateSysUse(SysUser sysUser) {

        // 先查询旧头像信息
        SysUser existingUser = getById(sysUser.getId());
        String oldAvatarUrl = existingUser != null ? existingUser.getAvatar() : null;

        /*if (StringUtils.isNotBlank(currentAvatarUrl) && currentAvatarUrl.contains("avatars/temp")) {
            // 把存储在 Minio 临时目录的头像 URL 保存到固定目录下，同时把固定目录的头像 URL 保存到数据库中
            String newAvatarUrl = currentAvatarUrl.replace("avatars/temp/", "avatars/use");
            String newMinioUrl = getMinioObjectName(newAvatarUrl);
            String tempMinioUrl = getMinioObjectName(currentAvatarUrl);
            // 将临时目录的文件拷贝到固定目录
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
                // 更新头像信息
                sysUser.setAvatar(newAvatarUrl);
                // 更新数据库
                updateById(sysUser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // 能走到这证明就是在修改用户信息时点击了上传头像，因此需要根据上一次保存在数据库中的头像的 URL 去删除 Minio 中的头像
            if (oldAvatarUrl != null) {
                String deleteAvatarUrl = getMinioObjectName(oldAvatarUrl);
                try {
                    minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(deleteAvatarUrl) // 因为此时是已经点击了上传新的头像，所以必须查询数据库获取旧的头像的 URL
                            .build());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }*/

        // 检查传递来的 SysUser 中是否包含头像 URL
        String currentAvatarUrl = sysUser.getAvatar();
        String newAvatarUrl = copyMinioTempToCurrent(currentAvatarUrl);
        // 拷贝成功，证明修改信息时也同时修改了头像（此时传递的头像 URL 就是最新的 URL），那么就要删除旧的头像
        if (newAvatarUrl != null) {
            // 更新头像信息
            sysUser.setAvatar(newAvatarUrl);
            // 更新数据库
            updateById(sysUser);
            // 删除旧的头像
            deleteMinioOldAvatar(oldAvatarUrl);
        }
    }

    /**
     * 因为删除和拷贝 Minio 中的文件时使用的是对象名，也就是桶中该文件的路径，因此需要从完整的 URL 中进行截串操作
     *   桶名: spzx-bucket
     *   对象名: avatars/use/2025-09-23/123456.png
     */
    @NotNull
    private String getMinioObjectName(String avatarUrl) {
        return avatarUrl.substring(
                avatarUrl.indexOf(minioProperties.getBucketName())
                        + minioProperties.getBucketName().length() + 1 // 最后加 1 是用来去掉桶名后的斜杠 "/"
        );
    }

    /**
     * 只有当前用户的头像 URL 是临时路径（也就是刚刚上传了头像），才进行 Minio 中的路径拷贝
     */
    private String copyMinioTempToCurrent(String currentAvatarUrl) {
        // 只有新增用户时点击了上传头像再进行路径的转移
        if (StringUtils.isNotBlank(currentAvatarUrl) && currentAvatarUrl.contains("avatars/temp")) {
            // 把存储在 Minio 临时目录的头像 URL 保存到固定目录下，同时把固定目录的头像 URL 保存到数据库中
            String newAvatarUrl = currentAvatarUrl.replace("avatars/temp/", "avatars/use/");
            String newMinioUrl = getMinioObjectName(newAvatarUrl);
            String tempMinioUrl = getMinioObjectName(currentAvatarUrl);
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
                log.info("成功将临时目录中的头像拷贝到使用中目录：" + newMinioUrl);
                return newAvatarUrl;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private void deleteMinioOldAvatar(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.contains("avatars/temp")) {
            String deleteMinioUrl = getMinioObjectName(avatarUrl);
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
