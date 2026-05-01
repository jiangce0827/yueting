package com.jiangce.yueting.ai;

import com.jiangce.yueting.common.BaseContext;
import com.jiangce.yueting.domain.po.Album;
import com.jiangce.yueting.domain.result.PageResult;
import com.jiangce.yueting.domain.vo.*;
import com.jiangce.yueting.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MusicTools {

    private final SongService songService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final PlaylistService playlistService;
    private final UserService userService;

    @Tool(description = "根据关键词搜索歌曲，返回歌曲名称、艺人、专辑等信息")
    public String searchSongs(@ToolParam(description = "歌曲名称或关键词") String keyword) {
        log.info("【AI工具调用】searchSongs, keyword={}", keyword);
        PageResult result = songService.searchSong(keyword, 1, 10);
        log.info("【AI工具调用】searchSongs 返回 {} 条记录", result != null && result.getRecords() != null ? result.getRecords().size() : 0);
        return formatSongs(result);
    }

    @Tool(description = "根据关键词搜索艺人，返回艺人的基本信息。如果用户想了解该艺人的歌曲或专辑，你应该继续调用 getArtistWorks 工具查询其作品")
    public String searchArtists(@ToolParam(description = "艺人名称或关键词") String keyword) {
        log.info("【AI工具调用】searchArtists, keyword={}", keyword);
        List<ArtistBaseVO> result = artistService.searchArtistBasic(keyword, 1, 10);
        log.info("【AI工具调用】searchArtists 返回 {} 条记录", result != null ? result.size() : 0);
        return formatArtists(result);
    }

    @Tool(description = "获取指定艺人的热门歌曲和专辑列表")
    public String getArtistWorks(@ToolParam(description = "艺人ID") Long artistId) {
        log.info("【AI工具调用】getArtistWorks, artistId={}", artistId);
        List<SongBaseVO> songs = songService.getHotSongsByArtistId(artistId);
        PageResult albumsResult = albumService.searchAlbumsByArtistId(artistId, 1, 10);
        log.info("【AI工具调用】getArtistWorks 返回 {} 首歌曲, {} 张专辑", songs != null ? songs.size() : 0, albumsResult != null && albumsResult.getRecords() != null ? albumsResult.getRecords().size() : 0);

        StringBuilder sb = new StringBuilder();
        sb.append("该艺人的热门歌曲：\n");
        if (songs != null && !songs.isEmpty()) {
            for (SongBaseVO song : songs) {
                sb.append("• [").append(song.getSongName()).append("](/song?id=").append(song.getSongId()).append(")")
                        .append(" - ").append(formatArtistLinks(song.getArtistIds(), song.getArtistNames())).append("\n");
            }
        } else {
            sb.append("暂无热门歌曲\n");
        }

        sb.append("\n该艺人的专辑：\n");
        if (albumsResult != null && albumsResult.getRecords() != null && !albumsResult.getRecords().isEmpty()) {
            for (Object obj : albumsResult.getRecords()) {
                Long albumId = null;
                String albumName = null;
                java.time.LocalDate releaseDate = null;
                if (obj instanceof Album album) {
                    albumId = album.getAlbumId();
                    albumName = album.getAlbumName();
                    releaseDate = album.getReleaseDate();
                } else if (obj instanceof java.util.Map<?, ?> map) {
                    Object idObj = map.get("albumId") != null ? map.get("albumId") : map.get("album_id");
                    if (idObj instanceof Number n) albumId = n.longValue();
                    Object nameObj = map.get("albumName") != null ? map.get("albumName") : map.get("album_name");
                    if (nameObj != null) albumName = String.valueOf(nameObj);
                    Object dateObj = map.get("releaseDate") != null ? map.get("releaseDate") : map.get("release_date");
                    if (dateObj instanceof java.time.LocalDate d) releaseDate = d;
                    else if (dateObj instanceof String s && !s.isEmpty()) releaseDate = java.time.LocalDate.parse(s);
                }
                if (albumId != null && albumName != null) {
                    sb.append("• [").append(albumName).append("](/album?id=").append(albumId).append(")");
                    if (releaseDate != null) {
                        sb.append("（").append(releaseDate).append("）");
                    }
                    sb.append("\n");
                }
            }
        } else {
            sb.append("暂无专辑\n");
        }
        return sb.toString();
    }

    @Tool(description = "根据关键词搜索专辑")
    public String searchAlbums(@ToolParam(description = "专辑名称或关键词") String keyword) {
        log.info("【AI工具调用】searchAlbums, keyword={}", keyword);
        PageResult result = albumService.searchAlbumsByKeyword(keyword, 1, 10);
        log.info("【AI工具调用】searchAlbums 返回 {} 条记录", result != null && result.getRecords() != null ? result.getRecords().size() : 0);
        return formatAlbums(result);
    }

    @Tool(description = "根据关键词搜索歌单")
    public String searchPlaylists(@ToolParam(description = "歌单名称或关键词") String keyword) {
        log.info("【AI工具调用】searchPlaylists, keyword={}", keyword);
        PageResult result = playlistService.getPlaylistByName(keyword, 1, 10);
        log.info("【AI工具调用】searchPlaylists 返回 {} 条记录", result != null && result.getRecords() != null ? result.getRecords().size() : 0);
        return formatPlaylists(result);
    }

    @Tool(description = "获取当前登录用户最近的听歌记录")
    public String getRecentPlayHistory(@ToolParam(description = "当前用户ID") Long userId) {
        log.info("【AI工具调用】getRecentPlayHistory, userId={}", userId);
        if (userId == null) {
            return "你还没有登录哦，登录后我才能看到你的听歌记录~";
        }
        PageResult result = userService.getUserPlayHistory(userId, 1, 20);
        log.info("【AI工具调用】getRecentPlayHistory 返回 {} 条记录", result != null && result.getRecords() != null ? result.getRecords().size() : 0);
        return formatPlayHistory(result);
    }

    @Tool(description = "根据用户听歌记录推荐歌曲。必须返回数据库中真实存在的歌曲，禁止编造任何歌曲")
    public String recommendSongs(@ToolParam(description = "当前用户ID") Long userId) {
        log.info("【AI工具调用】recommendSongs, userId={}", userId);
        if (userId == null) {
            return "你还没有登录哦，登录后我才能给你推荐歌曲~";
        }
        List<UserPlayRankingVO> ranking = userService.getUserPlayRanking(userId, false);
        if (ranking == null || ranking.isEmpty()) {
            return "你还没有听歌记录呢，多去听几首歌，我就能给你推荐啦！";
        }

        Set<Long> artistIds = ranking.stream()
                .limit(5)
                .flatMap(r -> {
                    String ids = r.getArtistIds();
                    if (ids == null || ids.isEmpty()) return java.util.stream.Stream.empty();
                    return Arrays.stream(ids.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Long::parseLong);
                })
                .collect(Collectors.toSet());

        List<String> recommendations = new ArrayList<>();
        for (Long artistId : artistIds) {
            List<SongBaseVO> hotSongs = songService.getHotSongsByArtistId(artistId);
            if (hotSongs != null) {
                for (SongBaseVO song : hotSongs.stream().limit(3).toList()) {
                    recommendations.add("[" + song.getSongName() + "](/song?id=" + song.getSongId() + ") - " + formatArtistLinks(song.getArtistIds(), song.getArtistNames()));
                }
            }
        }

        if (recommendations.isEmpty()) {
            log.info("【AI工具调用】recommendSongs 未找到推荐歌曲");
            return "暂时没有找到合适的推荐歌曲，不如去排行榜看看？";
        }

        String list = recommendations.stream()
                .distinct()
                .limit(10)
                .map(s -> "• " + s)
                .collect(Collectors.joining("\n"));

        log.info("【AI工具调用】recommendSongs 返回 {} 首推荐歌曲", recommendations.size());
        return "根据你的听歌口味，为你推荐以下歌曲：\n" + list;
    }

    private String formatArtistLinks(String artistIds, String artistNames) {
        if (artistIds == null || artistIds.isEmpty() || artistNames == null || artistNames.isEmpty()) {
            return artistNames != null ? artistNames : "";
        }
        String[] ids = artistIds.split(",");
        String[] names = artistNames.contains("/") ? artistNames.split("/") : artistNames.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(ids.length, names.length); i++) {
            if (i > 0) sb.append(" / ");
            sb.append("[").append(names[i].trim()).append("](/artist?id=").append(ids[i].trim()).append(")");
        }
        return sb.toString();
    }

    private String formatSongs(PageResult result) {
        if (result == null || result.getRecords() == null || result.getRecords().isEmpty()) {
            return "没有找到相关歌曲哦，换个关键词试试？";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("为你找到以下歌曲：\n");
        for (Object obj : result.getRecords()) {
            if (obj instanceof SongBaseVO song) {
                sb.append("• [").append(song.getSongName()).append("](/song?id=").append(song.getSongId()).append(")")
                        .append(" - ").append(formatArtistLinks(song.getArtistIds(), song.getArtistNames()))
                        .append("（专辑：[").append(song.getAlbumName()).append("](/album?id=").append(song.getAlbumId()).append(")）\n");
            }
        }
        return sb.toString();
    }

    private String formatArtists(List<ArtistBaseVO> result) {
        if (result == null || result.isEmpty()) {
            return "没有找到相关艺人哦，换个关键词试试？";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("为你找到以下艺人：\n");
        for (ArtistBaseVO artist : result) {
            sb.append("• ").append(artist.getArtistName())
                    .append("（艺人ID：").append(artist.getArtistId()).append("）\n");
            if (artist.getArtistBio() != null && !artist.getArtistBio().isEmpty()) {
                sb.append("  简介：").append(artist.getArtistBio()).append("\n");
            }
            if (artist.getArtistDescription() != null && !artist.getArtistDescription().isEmpty()) {
                sb.append("  详情：").append(artist.getArtistDescription()).append("\n");
            }
            sb.append("  如需查看该艺人的热门歌曲和专辑，请调用 getArtistWorks(artistId=")
                    .append(artist.getArtistId()).append(")\n");
        }
        return sb.toString();
    }

    private String formatAlbums(PageResult result) {
        if (result == null || result.getRecords() == null || result.getRecords().isEmpty()) {
            return "没有找到相关专辑哦，换个关键词试试？";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("为你找到以下专辑：\n");
        for (Object obj : result.getRecords()) {
            if (obj instanceof Album album) {
                sb.append("• [").append(album.getAlbumName()).append("](/album?id=").append(album.getAlbumId()).append(")");
                if (album.getReleaseDate() != null) {
                    sb.append("（发行时间：").append(album.getReleaseDate()).append("）");
                }
                sb.append("\n");
            } else if (obj instanceof AlbumWithAuthor album) {
                sb.append("• [").append(album.getAlbumName()).append("](/album?id=").append(album.getAlbumId()).append(")")
                        .append(" - ").append(formatArtistLinks(album.getArtistIds(), album.getArtistNames())).append("\n");
            } else if (obj instanceof AlbumWithSongs album) {
                sb.append("• [").append(album.getAlbumName()).append("](/album?id=").append(album.getAlbumId()).append(")")
                        .append(" - ").append(formatArtistLinks(album.getArtistIds(), album.getArtistNames())).append("\n");
            }
        }
        return sb.toString();
    }

    private String formatPlaylists(PageResult result) {
        if (result == null || result.getRecords() == null || result.getRecords().isEmpty()) {
            return "没有找到相关歌单哦，换个关键词试试？";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("为你找到以下歌单：\n");
        for (Object obj : result.getRecords()) {
            if (obj instanceof PlaylistWithUser playlist) {
                sb.append("• [").append(playlist.getPlaylistName()).append("](/playlist?id=").append(playlist.getPlaylistId()).append(")")
                        .append(" - by [").append(playlist.getUserName()).append("](/user/home?id=").append(playlist.getUserId()).append(")\n");
            } else if (obj instanceof PlaylistWithSongs playlist) {
                sb.append("• [").append(playlist.getPlaylistName()).append("](/playlist?id=").append(playlist.getPlaylistId()).append(")")
                        .append(" - by [").append(playlist.getUserName()).append("](/user/home?id=").append(playlist.getUserId()).append(")\n");
            }
        }
        return sb.toString();
    }

    private String formatPlayHistory(PageResult result) {
        if (result == null || result.getRecords() == null || result.getRecords().isEmpty()) {
            return "你还没有听歌记录哦，快去听音乐吧！";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("你最近的听歌记录：\n");
        for (Object obj : result.getRecords()) {
            if (obj instanceof UserPlayHistoryVO record) {
                sb.append("• [").append(record.getSongName()).append("](/song?id=").append(record.getSongId()).append(")")
                        .append(" - ").append(formatArtistLinks(record.getArtistIds(), record.getArtistNames())).append("\n");
            }
        }
        return sb.toString();
    }
}
