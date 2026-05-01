package com.jiangce.yueting.scheduleds;

import com.jiangce.yueting.common.SongContent;
import com.jiangce.yueting.mapper.SongDailyPlayCountMapper;
import com.jiangce.yueting.mapper.SongMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTask {
    private static final String TOPLIST_SONGS_CACHE_PATTERN = "toplistSongs:*";

    private static final DefaultRedisScript<Long> DELETE_IF_UNCHANGED_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('hget', KEYS[1], ARGV[1]) == ARGV[2] then " +
                    "return redis.call('hdel', KEYS[1], ARGV[1]) " +
                    "else return 0 end",
            Long.class
    );

    private final StringRedisTemplate stringRedisTemplate;
    private final SongMapper songMapper;
    private final SongDailyPlayCountMapper songDailyPlayCountMapper;
    private final SongContent songContent;

    @Scheduled(fixedRate = 10 * 60 * 1000) // 十分钟
    @Transactional
    public void syncPlayCounts() {
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
        Map<String, String> counts = hashOps.entries(SongContent.PLAY_COUNT_KEY);

        if (counts.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();
        counts.forEach((songIdStr, count) -> {
            Long songId = Long.parseLong(songIdStr);
            int playCount = Integer.parseInt(count);
            songMapper.addPlayCountByMusic(songId, playCount);
            songDailyPlayCountMapper.insertOrUpdate(songId, today, playCount);
        });

        counts.forEach((songIdStr, count) -> stringRedisTemplate.execute(
                DELETE_IF_UNCHANGED_SCRIPT,
                Collections.singletonList(SongContent.PLAY_COUNT_KEY),
                songIdStr,
                count
        ));

        deleteToplistSongsCache();
    }

    private void deleteToplistSongsCache() {
        Set<String> keys = stringRedisTemplate.keys(TOPLIST_SONGS_CACHE_PATTERN);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
            log.info("Deleted toplist songs cache: {}", keys);
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOrphanedSongFiles() {
        log.info("开始清理孤儿歌曲文件...");

        try {
            Set<String> validFiles = new HashSet<>(songMapper.getAllSongFileNames());
            log.info("数据库中有 {} 个有效歌曲文件", validFiles.size());

            Path storageDir = Paths.get(songContent.storagePath);
            if (!Files.exists(storageDir)) {
                log.warn("存储目录不存在: {}", songContent.storagePath);
                return;
            }

            Files.walkFileTree(storageDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        if (Files.isDirectory(file)) {
                            return FileVisitResult.CONTINUE;
                        }

                        String fileName = file.getFileName().toString();
                        LocalDateTime creationTime = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(attrs.creationTime().toMillis()),
                                ZoneId.systemDefault()
                        );
                        long fileAgeInDays = TimeUnit.MILLISECONDS.toDays(
                                System.currentTimeMillis() - attrs.creationTime().toMillis()
                        );

                        boolean isOld = fileAgeInDays >= 1;
                        boolean isOrphan = !validFiles.contains(fileName);

                        if (isOld && isOrphan) {
                            log.info("删除孤儿文件: {} (创建于 {}, 文件年龄: {} 天)",
                                    fileName, creationTime, fileAgeInDays);

                            try {
                                Files.delete(file);
                                log.info("文件删除成功: {}", fileName);
                            } catch (IOException e) {
                                log.error("删除文件失败: " + file, e);
                            }
                        }

                        return FileVisitResult.CONTINUE;
                    } catch (Exception e) {
                        log.error("处理文件时出错: " + file, e);
                        return FileVisitResult.CONTINUE;
                    }
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    log.error("访问文件失败: " + file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });

            log.info("歌曲文件清理完成");
        } catch (Exception e) {
            log.error("清理孤儿歌曲文件时发生错误", e);
        }
    }
}
