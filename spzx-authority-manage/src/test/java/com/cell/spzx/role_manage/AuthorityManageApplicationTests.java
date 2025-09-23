package com.cell.spzx.role_manage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
class AuthorityManageApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testMinIOFileUpload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 创建一个Minio的客户端对象
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://192.168.149.101:9001")
                .credentials("admin", "admin123")
                .build();

        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("spzx-bucket").build());

        // 如果不存在，那么此时就创建一个新的桶
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("spzx-bucket").build());
        } else {  // 如果存在打印信息
            System.out.println("Bucket 'spzx-bucket' already exists.");
        }

        FileInputStream fis = new FileInputStream("E://001.jpg") ;
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket("spzx-bucket") // 选择桶
                .stream(fis, fis.available(), -1) // 设置要上传的文件，文件大小，以及分块大小（-1 表示自动处理分块大小）
                .object("001.jpg") // 设置上传文件后存储在桶中的文件名
                .build();
        minioClient.putObject(putObjectArgs) ;

    }

}
