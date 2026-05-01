package com.jiangce.yueting.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SongContent {
    public static final Integer STATUS_USABLE = 0;
    public static final Integer STATUS_DELETED = 1;
    public static final String PLAY_COUNT_KEY = "song:play:counts";

    @Value("${yueting.song.path}")
    public String storagePath;
}
