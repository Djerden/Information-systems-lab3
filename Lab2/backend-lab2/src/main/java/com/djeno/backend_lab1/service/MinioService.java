package com.djeno.backend_lab1.service;

import io.minio.*;
import io.minio.errors.MinioException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MinioService {
    private final MinioClient minioClient;
    // private final String bucketName = "imported-files";

    public MinioService(@Value("${minio.endpoint}") String endpoint,
                        @Value("${minio.accessKey}") String accessKey,
                        @Value("${minio.secretKey}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @SneakyThrows
    public String uploadFile(MultipartFile file, String bucketName) {
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName; // UUID для получения файла

        createBucketIfNotExist(bucketName); // imported-files

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

    public void testUploadFile(MultipartFile file) {

        try {
            String bucketName = "test-bucket";
            createBucketIfNotExist(bucketName);

            String fileUrl = uploadFile(file, bucketName); // Записываем файл
            deleteFile(fileUrl, bucketName); // Удаляем файл

            System.out.println("Тестовая загрузка файла в Minio успешно выполнена");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при тестовой загрузке файла в Minio: " + e.getMessage());
        }
    }

    @SneakyThrows
    public InputStream downloadFile(String uniqueFileName, String bucketName) {
        createBucketIfNotExist(bucketName);

        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uniqueFileName)
                        .build());
    }

    public void deleteFile(String uniqueFileName, String bucketName) {
        createBucketIfNotExist(bucketName);

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .build());
        } catch (MinioException | IOException e) {
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Метод для создания бакета, если он не существовал
    @SneakyThrows
    public void createBucketIfNotExist(String bucketName) {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }
}
