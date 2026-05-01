package com.jiangce.yueting.common;

public final class ArtistNotificationContent {
    private ArtistNotificationContent() {
    }

    public static final Integer TYPE_SONG_APPROVED = 9;
    public static final Integer TYPE_SONG_REJECTED = 10;
    public static final Integer TYPE_ARTIST_APPLICATION_APPROVED = 11;
    public static final Integer TYPE_ARTIST_APPLICATION_REJECTED = 12;
    public static final Integer TYPE_ARTIST_IDENTITY_APPROVED = 13;
    public static final Integer TYPE_ARTIST_IDENTITY_REJECTED = 14;

    public static final String TYPE_STRING = "9,10,13,14";

    public static final Integer TARGET_TYPE_SONG_APPLICATION = 7;
    public static final Integer TARGET_TYPE_ARTIST_APPLICATION = 8;
    public static final Integer TARGET_TYPE_ARTIST_IDENTITY_APPLICATION = 9;
}
