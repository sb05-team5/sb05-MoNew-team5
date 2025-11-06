package com.sprint.project.monew.articleBackup.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.articleBackup.entity.ArticleBackup;
import com.sprint.project.monew.articleBackup.repository.ArticleBackupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3BackupService {
    private final ArticleRepository articleRepository;
    private final ArticleBackupRepository articleBackupRepository;
    private final S3Client s3Client;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // Instant 지원
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // unknown 필드 무시
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    @Value("${aws.s3.bucket}")
    private String bucket;
//AWS_S3_PREFIX
    @Value("${aws.s3.prefix}")
    private String prefix;

    public void uploadApplicationLogToS3() {
        // 로그 파일 경로 지정
        Path logPath = Paths.get("logs/application.log");

        if (!Files.exists(logPath)) {
            System.err.println("❌ 로그 파일이 존재하지 않습니다: " + logPath);
            return;
        }

        // 업로드될 파일명 (날짜 기준)
        String key = "application-log-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".log";

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(prefix + "/" + key)
                            .build(),
                    RequestBody.fromFile(logPath)
            );

            System.out.println("✅ 로그 파일 S3 업로드 완료: " + key);

        } catch (Exception e) {
            System.err.println("❌ 로그 파일 S3 업로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }





    // 전체 데이터를 S3에 백업
    public void backupArticlesToS3() throws JsonProcessingException {
        List<Article> articles = articleRepository.findAllArticle();

        // JSON 직렬화
        String json = objectMapper.writeValueAsString(articles);

        // 파일 이름 생성
        String key = "articles-backup-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".json";

        // S3 업로드
        uploadFile(key, json);

        System.out.println("✅ 백업 완료: " + key);
    }

    // S3에서 특정 백업 파일 가져오기
    public List<ArticleBackup> restoreArticlesFromS3(String key) throws JsonProcessingException {
        String json = downloadFile(key);
        return List.of(objectMapper.readValue(json, ArticleBackup[].class));
    }



    // S3 업로드
    public void uploadFile(String key, String content) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(prefix + "/" + key)
                        .build(),
                RequestBody.fromString(content)
        );
    }

    // ==============================
    // S3 다운로드
    // ==============================
    public String downloadFile(String key) {
        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(fullKey(key)) // prefix 포함
                        .build());
             BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object))) {

            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            throw new RuntimeException("Failed to read S3 object: " + fullKey(key), e);
        }
    }

    // ==============================
    // 최신 백업 복원
    // ==============================
    public List<ArticleBackup> restoreLatestBackup() throws JsonProcessingException {
        List<String> keys = listFiles("articles-backup-"); // prefix 없이 호출
        if (keys.isEmpty()) {
            throw new RuntimeException("백업 파일이 없습니다.");
        }

        String latestKey = keys.get(0); // 최신 파일
        System.out.println("✅ 최신 백업 파일: " + latestKey);
        return restoreArticlesFromS3(latestKey);
    }

    // ==============================
    // S3 파일 리스트 조회
    // prefix 내부에서 자동 결합
    // ==============================
    public List<String> listFiles(String filePrefix) {
        String fullPrefix = fullKey(filePrefix); // prefix 포함
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(fullPrefix)
                .build();

        ListObjectsV2Response result = s3Client.listObjectsV2(request);

        return result.contents().stream()
                .map(S3Object::key)
                .map(this::removePrefix) // 호출 시 prefix 제거
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    // ==============================
    // key에 prefix 붙이기
    // ==============================
    private String fullKey(String key) {
        return prefix + "/" + key;
    }

    // ==============================
    // S3 전체 key → prefix 제거
    // ==============================
    private String removePrefix(String key) {
        if (key.startsWith(prefix + "/")) {
            return key.substring(prefix.length() + 1);
        }
        return key;
    }
}
