/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80400 (8.4.0)
 Source Host           : localhost:3306
 Source Schema         : yueting

 Target Server Type    : MySQL
 Target Server Version : 80400 (8.4.0)
 File Encoding         : 65001

 Date: 26/04/2026 14:54:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_message`;
CREATE TABLE `ai_chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `chat_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_chat`(`user_id` ASC, `chat_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_chat_message
-- ----------------------------

-- ----------------------------
-- Table structure for album
-- ----------------------------
DROP TABLE IF EXISTS `album`;
CREATE TABLE `album`  (
  `album_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `album_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专辑名称',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专辑封面地址',
  `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '专辑描述',
  `release_date` date NULL DEFAULT NULL COMMENT '发行日期',
  PRIMARY KEY (`album_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '专辑表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of album
-- ----------------------------

-- ----------------------------
-- Table structure for album_artist
-- ----------------------------
DROP TABLE IF EXISTS `album_artist`;
CREATE TABLE `album_artist`  (
  `album_id` bigint NOT NULL COMMENT '专辑id',
  `artist_id` bigint NOT NULL COMMENT '歌手id',
  PRIMARY KEY (`album_id`, `artist_id`) USING BTREE,
  INDEX `artist_id`(`artist_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '专辑-歌手关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of album_artist
-- ----------------------------

-- ----------------------------
-- Table structure for artist
-- ----------------------------
DROP TABLE IF EXISTS `artist`;
CREATE TABLE `artist`  (
  `artist_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户id，（外键，允许为空）',
  `artist_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '艺名，唯一',
  `artist_avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '艺人头像地址',
  `artist_cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '艺人封面地址',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别：0-组合，1-男，2-女 ，(默认0)',
  `language` tinyint NULL DEFAULT NULL COMMENT '语种:0：其他，1-华语，2：欧美，3：日本，4：韩国（默认0）',
  `artist_bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '歌手简介',
  `artist_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '歌手详情',
  `hot` bigint NOT NULL DEFAULT 0 COMMENT '热度',
  `real_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `status` tinyint NULL DEFAULT NULL COMMENT '状态：0：未激活，1：正常',
  `status_expired_at` datetime NULL DEFAULT NULL COMMENT '封禁时间',
  PRIMARY KEY (`artist_id`) USING BTREE,
  UNIQUE INDEX `artist_name`(`artist_name` ASC) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '歌手表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of artist
-- ----------------------------

-- ----------------------------
-- Table structure for artist_application
-- ----------------------------
DROP TABLE IF EXISTS `artist_application`;
CREATE TABLE `artist_application`  (
  `application_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '申请人id',
  `apply_type` tinyint NOT NULL COMMENT '申请类型：0新艺人，0申领已有艺人',
  `claimed_artist_id` bigint NULL DEFAULT NULL COMMENT '认领艺人id',
  `artist_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '艺名',
  `artist_avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '艺人头像地址',
  `artist_cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '艺人页头图',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女 (默认0)',
  `language` tinyint NULL DEFAULT NULL COMMENT '0:其他，1：华语，2：欧美，3：日本，4：韩国',
  `artist_bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '个人简介（背景、经历、技能等）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `id_card` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证号',
  `half_body` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '半身照',
  `front` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '正面照',
  `back` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '反面照',
  `reviewed_by` bigint NULL DEFAULT NULL COMMENT '审核管理员ID（外键到admin表）',
  `rejection_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '驳回原因',
  `applied_at` datetime NULL DEFAULT NULL COMMENT '申请时间',
  `reviewed_at` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '0-待审核，1-通过，2-驳回（默认0）',
  PRIMARY KEY (`application_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `reviewed_by`(`reviewed_by` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '歌手申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of artist_application
-- ----------------------------

-- ----------------------------
-- Table structure for artist_identity
-- ----------------------------
DROP TABLE IF EXISTS `artist_identity`;
CREATE TABLE `artist_identity`  (
  `artist_id` bigint NOT NULL,
  `identity_type_id` int NOT NULL,
  PRIMARY KEY (`artist_id`, `identity_type_id`) USING BTREE,
  INDEX `fk_artist_identity_type`(`identity_type_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '艺人身份关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of artist_identity
-- ----------------------------

-- ----------------------------
-- Table structure for artist_identity_application
-- ----------------------------
DROP TABLE IF EXISTS `artist_identity_application`;
CREATE TABLE `artist_identity_application`  (
  `artist_identity_application_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `artist_id` bigint NOT NULL COMMENT '艺人id',
  `identity_type` tinyint NOT NULL COMMENT '申请身份',
  `file_type` tinyint NULL DEFAULT NULL COMMENT '0：原作者，1：翻作者',
  `file_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件路径',
  `file_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文件内容',
  `introduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '简介',
  `rejection_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `reviewed_by` bigint NULL DEFAULT NULL,
  `reviewed_at` datetime NULL DEFAULT NULL,
  `applied_at` datetime NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`artist_identity_application_id`) USING BTREE,
  INDEX `singer_application_id`(`artist_identity_application_id` ASC, `status` ASC) USING BTREE,
  INDEX `artist_id`(`artist_id` ASC) USING BTREE,
  INDEX `reviewed_by`(`reviewed_by` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '艺人具体身份申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of artist_identity_application
-- ----------------------------

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `banner_id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '轮播图标题',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片地址',
  `target_type` tinyint NOT NULL DEFAULT 0 COMMENT '0-无跳转，1-歌曲，2-专辑，3-歌单，4-外部链接',
  `target_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转目标ID',
  `target_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '外部链接',
  `sort_order` int NOT NULL DEFAULT 0,
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0-禁用，1-启用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`banner_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '轮播图表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '悦听音乐', '/banner_default.png', 0, NULL, NULL, 0, 1, '2026-04-26 13:58:41', '2026-04-26 14:50:35');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `comment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `target_type` tinyint NOT NULL COMMENT '评论目标类型：1-歌曲，2-歌单，3-专辑',
  `target_id` bigint NOT NULL COMMENT '评论目标ID（歌曲ID/歌单ID/专辑ID）',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父评论ID（用于回复）',
  `reply_to_user_id` bigint NULL DEFAULT NULL COMMENT '回复目标用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `fk_comment_reply_user`(`reply_to_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for comment_like
-- ----------------------------
DROP TABLE IF EXISTS `comment_like`;
CREATE TABLE `comment_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_comment_id`(`comment_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_comment_user`(`comment_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment_like
-- ----------------------------

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `employee_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '加密密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用（1-是，0-否）',
  `last_login_at` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`employee_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES (1, 'jiangce', '$2a$10$PIe9UA3Lc5J2aeFcG28UYeL5n32F1XRXgenaQiZ4u1mSNBC5XgYPe', '江恻', 1, NULL, '2025-07-01 12:59:33');

-- ----------------------------
-- Table structure for employee_role
-- ----------------------------
DROP TABLE IF EXISTS `employee_role`;
CREATE TABLE `employee_role`  (
  `employee_id` bigint NOT NULL COMMENT '管理员ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`employee_id`, `role_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of employee_role
-- ----------------------------
INSERT INTO `employee_role` VALUES (1, 1);

-- ----------------------------
-- Table structure for identity_type
-- ----------------------------
DROP TABLE IF EXISTS `identity_type`;
CREATE TABLE `identity_type`  (
  `identity_type_id` int NOT NULL AUTO_INCREMENT COMMENT '身份类型ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份名称（如：作词人/作曲人/歌手）',
  PRIMARY KEY (`identity_type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '身份类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of identity_type
-- ----------------------------
INSERT INTO `identity_type` VALUES (1, '歌手');
INSERT INTO `identity_type` VALUES (2, '作词');
INSERT INTO `identity_type` VALUES (3, '作曲');

-- ----------------------------
-- Table structure for note
-- ----------------------------
DROP TABLE IF EXISTS `note`;
CREATE TABLE `note`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '笔记ID',
  `user_id` bigint NOT NULL COMMENT '发布用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '笔记内容',
  `music_id` bigint NULL DEFAULT NULL COMMENT '配乐ID',
  `music_type` tinyint NULL DEFAULT NULL COMMENT '配乐类型：1-单曲，2-歌手，3-专辑，4-歌单',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NOT NULL DEFAULT 0 COMMENT '评论数',
  `forward_count` int NOT NULL DEFAULT 0 COMMENT '转发数',
  `forward_source_id` bigint NULL DEFAULT NULL COMMENT '转发的源笔记ID',
  `is_forwarded` tinyint NOT NULL DEFAULT 0 COMMENT '是否转发生成的笔记',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_forward_source_id`(`forward_source_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔记表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of note
-- ----------------------------

-- ----------------------------
-- Table structure for note_comment
-- ----------------------------
DROP TABLE IF EXISTS `note_comment`;
CREATE TABLE `note_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `note_id` bigint NOT NULL COMMENT '笔记ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父评论ID',
  `reply_to_user_id` bigint NULL DEFAULT NULL COMMENT '回复目标用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_note_id`(`note_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `fk_note_comment_reply`(`reply_to_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔记评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of note_comment
-- ----------------------------

-- ----------------------------
-- Table structure for note_forward
-- ----------------------------
DROP TABLE IF EXISTS `note_forward`;
CREATE TABLE `note_forward`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '转发ID',
  `note_id` bigint NOT NULL COMMENT '原笔记ID',
  `user_id` bigint NOT NULL COMMENT '转发用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '转发时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_note_user`(`note_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔记转发表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of note_forward
-- ----------------------------

-- ----------------------------
-- Table structure for note_image
-- ----------------------------
DROP TABLE IF EXISTS `note_image`;
CREATE TABLE `note_image`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `note_id` bigint NOT NULL COMMENT '笔记ID',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_note_id`(`note_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔记图片表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of note_image
-- ----------------------------

-- ----------------------------
-- Table structure for note_like
-- ----------------------------
DROP TABLE IF EXISTS `note_like`;
CREATE TABLE `note_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `note_id` bigint NOT NULL COMMENT '笔记ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_note_user`(`note_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '笔记点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of note_like
-- ----------------------------

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `notification_id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者用户ID',
  `sender_id` bigint NULL DEFAULT NULL COMMENT '触发者用户ID（系统通知为NULL）',
  `type` tinyint NOT NULL COMMENT '通知类型：1-评论点赞，2-笔记点赞，3-评论回复，4-新增关注，5-系统公告，6-私信',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知内容（私信内容或系统公告内容）',
  `target_id` bigint NULL DEFAULT NULL COMMENT '关联目标ID（如评论ID、笔记ID、用户ID等）',
  `target_type` tinyint NULL DEFAULT NULL COMMENT '关联目标类型：1-评论，2-笔记，3-歌曲，4-歌单，5-专辑，6-用户',
  `target_sub_id` bigint NULL DEFAULT NULL COMMENT '关联目标的父级ID（如评论所属的歌曲/歌单ID）',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-接收者删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`notification_id`) USING BTREE,
  INDEX `idx_receiver`(`receiver_id` ASC) USING BTREE,
  INDEX `idx_receiver_read`(`receiver_id` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_sender`(`sender_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notification
-- ----------------------------

-- ----------------------------
-- Table structure for playlist
-- ----------------------------
DROP TABLE IF EXISTS `playlist`;
CREATE TABLE `playlist`  (
  `playlist_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `playlist_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '歌单名称',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面地址',
  `description` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '简介',
  `is_public` tinyint(1) NULL DEFAULT 1 COMMENT '是否公开（默认true）',
  `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否为默认歌单(不可删除)',
  `song_count` int NULL DEFAULT 0 COMMENT '歌曲总数（默认0）',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数（默认0）',
  `play_count` bigint NULL DEFAULT 0 COMMENT '播放次数（默认0）',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `is_system` tinyint(1) NULL DEFAULT 0 COMMENT '是否为系统榜单',
  PRIMARY KEY (`playlist_id`) USING BTREE,
  INDEX `idx_playlist_title`(`playlist_name` ASC) USING BTREE,
  INDEX `uniq_user_default`(`user_id` ASC, `is_default` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '歌单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of playlist
-- ----------------------------
INSERT INTO `playlist` VALUES (1, 1, '飙升榜', 'https://p2.music.126.net/rIi7Qzy2i2Y_1QD7cd0MYA==/109951170048506929.jpg?param=150y150', NULL, 1, 0, 0, 0, 0, '2025-07-01 12:39:39', 1);
INSERT INTO `playlist` VALUES (2, 1, '热歌榜', '	https://p2.music.126.net/0SUEG8yDACfx0Bw2MYFv4Q==/109951170048519512.jpg?param=150y150', NULL, 1, 0, 0, 0, 0, '2025-07-01 12:40:10', 1);
INSERT INTO `playlist` VALUES (3, 1, '新歌榜', 'https://p2.music.126.net/5guhqPBTcIrrhLBotgaT6w==/109951170048511751.jpg?param=150y150', NULL, 1, 0, 0, 0, 0, '2025-07-01 12:40:07', 1);
INSERT INTO `playlist` VALUES (4, 1, '我喜欢的音乐', '/playlist_default_cover.png', NULL, 1, 1, 0, 0, 0, NULL, 0);

-- ----------------------------
-- Table structure for playlist_tag
-- ----------------------------
DROP TABLE IF EXISTS `playlist_tag`;
CREATE TABLE `playlist_tag`  (
  `playlist_id` bigint NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`playlist_id`, `tag_id`) USING BTREE,
  INDEX `fk_pl_tag`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '歌单标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of playlist_tag
-- ----------------------------

-- ----------------------------
-- Table structure for private_message
-- ----------------------------
DROP TABLE IF EXISTS `private_message`;
CREATE TABLE `private_message`  (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者用户ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-发送者删除，2-接收者删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_sender_receiver`(`sender_id` ASC, `receiver_id` ASC) USING BTREE,
  INDEX `idx_receiver`(`receiver_id` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '私信表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of private_message
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色标识',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-禁用',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `uk_role_key`(`role_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '超级管理员', 'SUPER_ADMIN', '拥有所有菜单权限', 0, '2026-04-09 03:23:31', NULL);
INSERT INTO `role` VALUES (2, '歌曲审批人员', 'SONG_APPROVER', '负责歌曲审批', 0, '2026-04-09 03:23:31', NULL);
INSERT INTO `role` VALUES (3, '艺人审批人员', 'ARTIST_APPROVER', '负责艺人审批', 0, '2026-04-09 03:23:31', NULL);

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE,
  INDEX `idx_menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_menu
-- ----------------------------
INSERT INTO `role_menu` VALUES (1, 1);
INSERT INTO `role_menu` VALUES (1, 2);
INSERT INTO `role_menu` VALUES (1, 3);
INSERT INTO `role_menu` VALUES (1, 4);
INSERT INTO `role_menu` VALUES (1, 5);
INSERT INTO `role_menu` VALUES (1, 6);
INSERT INTO `role_menu` VALUES (1, 7);
INSERT INTO `role_menu` VALUES (1, 8);
INSERT INTO `role_menu` VALUES (2, 8);
INSERT INTO `role_menu` VALUES (1, 9);
INSERT INTO `role_menu` VALUES (3, 9);
INSERT INTO `role_menu` VALUES (1, 10);
INSERT INTO `role_menu` VALUES (3, 10);
INSERT INTO `role_menu` VALUES (1, 11);
INSERT INTO `role_menu` VALUES (1, 12);
INSERT INTO `role_menu` VALUES (1, 13);
INSERT INTO `role_menu` VALUES (2, 13);
INSERT INTO `role_menu` VALUES (3, 13);

-- ----------------------------
-- Table structure for song
-- ----------------------------
DROP TABLE IF EXISTS `song`;
CREATE TABLE `song`  (
  `song_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `song_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '歌曲名称',
  `song_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '播放地址',
  `song_type` tinyint NULL DEFAULT NULL COMMENT '0:原唱，1翻唱',
  `language` tinyint NULL DEFAULT NULL COMMENT '语种',
  `duration` int NULL DEFAULT NULL COMMENT '时长',
  `lyrics` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '歌词',
  `album_id` bigint NULL DEFAULT NULL COMMENT '专辑id',
  `play_count` bigint NULL DEFAULT NULL COMMENT '播放次数',
  `like_count` bigint NULL DEFAULT NULL COMMENT '喜欢次数',
  `create_at` datetime NULL DEFAULT NULL COMMENT '发行时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '0：正常，1：下架',
  PRIMARY KEY (`song_id`) USING BTREE,
  INDEX `album_id`(`album_id` ASC) USING BTREE,
  INDEX `release_data`(`create_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '歌曲表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of song
-- ----------------------------

-- ----------------------------
-- Table structure for song_application
-- ----------------------------
DROP TABLE IF EXISTS `song_application`;
CREATE TABLE `song_application`  (
  `application_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `artist_id` bigint NOT NULL COMMENT '申请人ID（外键到user表）',
  `song_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '歌曲名称',
  `song_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '歌曲文件地址',
  `song_type` tinyint NULL DEFAULT NULL COMMENT '0:原唱 1翻唱',
  `language` tinyint NULL DEFAULT NULL COMMENT '语种',
  `tag_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `singer_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `lyricist_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `composer_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `duration` int NOT NULL COMMENT '时长（秒）',
  `lyrics` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '歌词文件地址',
  `album_id` bigint NULL DEFAULT NULL COMMENT '所属专辑ID（外键到album表）',
  `reviewed_by` bigint NULL DEFAULT NULL COMMENT '审核管理员ID（外键到admin表）',
  `rejection_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '驳回原因',
  `applied_at` datetime NOT NULL COMMENT '提交时间',
  `reviewed_at` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态:0-待审核,1-待艺人确认,2-可发布,3-已发布,4-驳回,5-合作方拒绝',
  PRIMARY KEY (`application_id`) USING BTREE,
  INDEX `reviewed_by`(`reviewed_by` ASC) USING BTREE,
  INDEX `song_application_ibfk_1`(`artist_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '歌曲申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of song_application
-- ----------------------------

-- ----------------------------
-- Table structure for song_application_approval
-- ----------------------------
DROP TABLE IF EXISTS `song_application_approval`;
CREATE TABLE `song_application_approval`  (
  `approval_id` bigint NOT NULL AUTO_INCREMENT,
  `application_id` bigint NOT NULL COMMENT '关联的歌曲申请ID',
  `artist_id` bigint NOT NULL COMMENT '需确认的合作艺人ID',
  `status` tinyint NOT NULL COMMENT '0-待处理,1-同意,2-拒绝',
  `notified_at` datetime NULL DEFAULT NULL COMMENT '通知发送时间',
  `responded_at` datetime NULL DEFAULT NULL COMMENT '艺人响应时间',
  PRIMARY KEY (`approval_id`) USING BTREE,
  UNIQUE INDEX `uniq_application_artist`(`application_id` ASC, `artist_id` ASC) USING BTREE,
  INDEX `artist_id`(`artist_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '歌曲申请合作艺人审批表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of song_application_approval
-- ----------------------------

-- ----------------------------
-- Table structure for song_artist
-- ----------------------------
DROP TABLE IF EXISTS `song_artist`;
CREATE TABLE `song_artist`  (
  `song_id` bigint NOT NULL,
  `artist_id` bigint NOT NULL,
  `role_type` tinyint NOT NULL COMMENT '0:歌手，1作词，2作曲',
  PRIMARY KEY (`song_id`, `artist_id`, `role_type`) USING BTREE,
  INDEX `artist_id`(`artist_id` ASC) USING BTREE,
  INDEX `song_id`(`song_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '歌曲-歌手关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of song_artist
-- ----------------------------

-- ----------------------------
-- Table structure for song_daily_play_count
-- ----------------------------
DROP TABLE IF EXISTS `song_daily_play_count`;
CREATE TABLE `song_daily_play_count`  (
  `song_id` bigint NOT NULL COMMENT '歌曲ID',
  `play_date` date NOT NULL COMMENT '播放日期',
  `play_count` int NOT NULL DEFAULT 0 COMMENT '当日播放量',
  PRIMARY KEY (`song_id`, `play_date`) USING BTREE,
  INDEX `idx_play_date`(`play_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '歌曲每日播放量统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of song_daily_play_count
-- ----------------------------

-- ----------------------------
-- Table structure for song_playlist
-- ----------------------------
DROP TABLE IF EXISTS `song_playlist`;
CREATE TABLE `song_playlist`  (
  `song_id` bigint NOT NULL COMMENT '歌曲id',
  `playlist_id` bigint NOT NULL COMMENT '歌单id',
  `added_at` datetime NULL DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`playlist_id`, `song_id`) USING BTREE,
  INDEX `song_id`(`song_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '歌曲-歌单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of song_playlist
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父菜单ID，0表示顶级菜单',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `sort` int NOT NULL DEFAULT 0 COMMENT '显示顺序，数字越小越靠前',
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '菜单类型：1-目录，2-菜单，3-按钮',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-禁用',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '系统管理', NULL, NULL, 'Setting', 1, 1, 0, '2026-04-09 03:23:21', '2026-04-09 03:23:21');
INSERT INTO `sys_menu` VALUES (2, 1, '菜单管理', '/admin/menu-management', 'Layout', 'Menu', 2, 2, 0, '2026-04-09 03:23:21', '2026-04-09 03:35:46');
INSERT INTO `sys_menu` VALUES (3, 1, '角色管理', '/admin/role-management', 'Layout', 'UserFilled', 3, 2, 0, '2026-04-09 03:23:21', '2026-04-09 03:35:46');
INSERT INTO `sys_menu` VALUES (4, 0, '悦听管理', NULL, NULL, 'User', 1, 1, 0, '2026-04-09 03:23:21', '2026-04-09 03:31:38');
INSERT INTO `sys_menu` VALUES (5, 4, '用户列表', '/admin/user-management', 'Layout', 'List', 2, 2, 0, '2026-04-09 03:23:21', '2026-04-09 03:31:38');
INSERT INTO `sys_menu` VALUES (6, 4, '艺人管理', '/admin/artist-management', 'Layout', 'User', 3, 2, 0, '2026-04-09 03:23:21', '2026-04-09 03:31:38');
INSERT INTO `sys_menu` VALUES (7, 4, '歌曲管理', '/admin/song-management', 'Layout', 'VideoPlay', 4, 2, 0, '2026-04-09 03:23:21', '2026-04-09 03:31:38');
INSERT INTO `sys_menu` VALUES (8, 4, '歌曲审批', '/admin/song-review', 'Layout', 'DocumentChecked', 5, 2, 0, '2026-04-09 03:23:21', '2026-04-09 03:31:38');
INSERT INTO `sys_menu` VALUES (9, 4, '艺人审批', '/admin/artist-review', 'Layout', 'Stamp', 6, 2, 0, '2026-04-09 03:23:21', '2026-04-09 03:31:38');
INSERT INTO `sys_menu` VALUES (10, 4, '身份审批', '/admin/artistIdentity-review', 'Layout', 'Medal', 7, 2, 0, '2026-04-09 03:23:21', '2026-04-09 03:31:38');
INSERT INTO `sys_menu` VALUES (11, 4, '轮播图管理', '/admin/banner-management', 'Layout', 'Picture', 8, 2, 0, '2026-04-09 03:23:21', '2026-04-09 03:31:38');
INSERT INTO `sys_menu` VALUES (12, 1, '员工管理', '/admin/employee-management', 'Layout', 'User', 4, 2, 0, '2026-04-09 03:39:40', '2026-04-09 03:39:40');
INSERT INTO `sys_menu` VALUES (13, 0, '控制台', '/admin/dashboard', 'Layout', 'DataBoard', 0, 2, 0, '2026-04-09 14:06:06', '2026-04-09 14:06:06');

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `tag_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  PRIMARY KEY (`tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, '华语');
INSERT INTO `tag` VALUES (2, '欧美');
INSERT INTO `tag` VALUES (3, '日韩');
INSERT INTO `tag` VALUES (101, '流行');
INSERT INTO `tag` VALUES (102, '摇滚');
INSERT INTO `tag` VALUES (103, '民谣');
INSERT INTO `tag` VALUES (104, '电子');
INSERT INTO `tag` VALUES (105, '说唱');
INSERT INTO `tag` VALUES (106, '轻音乐');
INSERT INTO `tag` VALUES (107, '古典');
INSERT INTO `tag` VALUES (108, '民族');
INSERT INTO `tag` VALUES (109, '古风');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像地址',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女 (默认0)',
  `signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个性签名',
  `listen_count` int NULL DEFAULT 0 COMMENT '累计听歌数 （默认0）',
  `experience` int NULL DEFAULT 0 COMMENT '累计经验值，（默认0）',
  `following_count` int NULL DEFAULT 0 COMMENT '关注数',
  `follower_count` int NULL DEFAULT 0 COMMENT '粉丝数',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `status` tinyint NULL DEFAULT 0 COMMENT '账号状态：0-正常，1-禁用 (默认0)',
  `status_expire_at` datetime NULL DEFAULT NULL COMMENT '封禁到期时间',
  `user_type` tinyint NULL DEFAULT 0 COMMENT '用户类型:0-普通用户,1-系统账号',
  `password_status` tinyint NULL DEFAULT 0 COMMENT '0:未设置账密,1:已启用账密登录，2:已禁用账密登录',
  `privacy_allow_messages` tinyint NULL DEFAULT 0 COMMENT '私信权限:0-所有人,1-仅关注的人',
  `privacy_show_listening_habits` tinyint NULL DEFAULT 0 COMMENT '听歌习惯公开:0-公开,1-不公开',
  `created_at` datetime NULL DEFAULT NULL COMMENT '账号创建时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_user_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '悦听音乐', 'yueting', '123', 'yueting@qq.com', NULL, '/user_yueting_avatar.png', 0, '悦听音乐官方', 0, 0, 0, 1, NULL, 0, NULL, 0, 1, 0, 0, '2025-07-01 12:20:50');

-- ----------------------------
-- Table structure for user_follow
-- ----------------------------
DROP TABLE IF EXISTS `user_follow`;
CREATE TABLE `user_follow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `follower_id` bigint NOT NULL COMMENT '关注者用户ID',
  `following_id` bigint NOT NULL COMMENT '被关注者用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_follower_following`(`follower_id` ASC, `following_id` ASC) USING BTREE,
  INDEX `idx_following_id`(`following_id` ASC) USING BTREE,
  INDEX `idx_follower_id`(`follower_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户关注关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_follow
-- ----------------------------

-- ----------------------------
-- Table structure for user_play_record
-- ----------------------------
DROP TABLE IF EXISTS `user_play_record`;
CREATE TABLE `user_play_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `song_id` bigint NOT NULL COMMENT '歌曲ID',
  `play_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '播放时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_play_time`(`user_id` ASC, `play_time` ASC) USING BTREE,
  INDEX `idx_song_id`(`song_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户播放记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_play_record
-- ----------------------------

-- ----------------------------
-- Table structure for user_playlist
-- ----------------------------
DROP TABLE IF EXISTS `user_playlist`;
CREATE TABLE `user_playlist`  (
  `user_id` bigint NOT NULL COMMENT '用户id',
  `playlist_id` bigint NOT NULL COMMENT '歌单id',
  `relation_type` tinyint NOT NULL COMMENT '0-创建，1-收藏，2-喜欢',
  `created_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`, `playlist_id`, `relation_type`) USING BTREE,
  INDEX `user_playlist_2`(`playlist_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户-歌单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_playlist
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
