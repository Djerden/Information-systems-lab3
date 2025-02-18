package com.djeno.backend_lab1.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final String bucketName = "imported-files";

    public MinioService(@Value("${minio.endpoint}") String endpoint,
                        @Value("${minio.accessKey}") String accessKey,
                        @Value("${minio.secretKey}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @SneakyThrows
    public String uploadFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName; // UUID для получения файла

        createBucketIfNotExist();

        try (InputStream inputStream = file.getInputStream()) {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("filename", originalFileName);

            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .userMetadata(metadata)
                            .build());
            return uniqueFileName;
        }
    }

    @SneakyThrows
    public InputStream downloadFile(String uniqueFileName) {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uniqueFileName)
                        .build());
    }

    public boolean deleteFile(String uniqueFileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .build());
            return true;
        } catch (MinioException | IOException e) {
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Метод для создания бакета, если он не существовал
    @SneakyThrows
    public void createBucketIfNotExist() {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }
}
