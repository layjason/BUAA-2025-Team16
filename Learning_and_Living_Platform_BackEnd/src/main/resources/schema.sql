CREATE DATABASE IF NOT EXISTS lal_platform
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE lal_platform;

DROP VIEW IF EXISTS UserOnline;
DROP TABLE IF EXISTS DownloadHistory;
DROP TABLE IF EXISTS ResourceCategories;
DROP TABLE IF EXISTS Resource;
DROP TABLE IF EXISTS `Like`;
DROP TABLE IF EXISTS Reply;
DROP TABLE IF EXISTS Comment;
DROP TABLE IF EXISTS Post;
DROP TABLE IF EXISTS Log;
DROP TABLE IF EXISTS Experience;
DROP TABLE IF EXISTS `User`;
DROP TABLE IF EXISTS Admin;
DROP VIEW IF EXISTS `UserOnline`;

CREATE TABLE `User` (
  id                INT NOT NULL AUTO_INCREMENT,
  `name`            VARCHAR(20),
  `password`        VARCHAR(255) NOT NULL,
  email             VARCHAR(40) NOT NULL,
  gender            ENUM('male','female','unknown') NOT NULL DEFAULT 'unknown',
  birthday          DATE NULL,
  registerTime      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  profilePhotoUrl   VARCHAR(255),
  LogInNum          INT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `User` AUTO_INCREMENT = 10000001;

CREATE TABLE Admin (
  id          INT NOT NULL AUTO_INCREMENT,
  email       VARCHAR(100) NOT NULL,
  `password`  VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_admin_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Experience (
  id         INT NOT NULL,
  exp        INT NOT NULL DEFAULT 0,
  dailyExp   INT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT fk_experience_user
    FOREIGN KEY (id) REFERENCES `User`(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Log (
  id          INT NOT NULL AUTO_INCREMENT,
  userId      INT NOT NULL,
  loginTime   DATETIME NOT NULL,
  logoutTime  DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_log_userid_time (userId, loginTime),
  CONSTRAINT fk_log_user
    FOREIGN KEY (userId) REFERENCES `User`(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Post (
  id            INT NOT NULL AUTO_INCREMENT,
  title         VARCHAR(200) NOT NULL,
  userId        INT NOT NULL,
  content       MEDIUMTEXT,
  floorCount    INT NOT NULL DEFAULT 0,
  postTime      DATETIME NOT NULL,
  updateTime    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  imageurls     MEDIUMTEXT,
  likeCount     INT NOT NULL DEFAULT 0,
  browseCount   INT NOT NULL DEFAULT 0,
  authority     INT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_post_user_time (userId, postTime),
  KEY idx_post_update (updateTime),
  CONSTRAINT fk_post_user
    FOREIGN KEY (userId) REFERENCES `User`(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Comment (
  id            INT NOT NULL AUTO_INCREMENT,
  postId        INT NOT NULL,
  userId        INT NOT NULL,
  publishTime   DATETIME NOT NULL,
  content       MEDIUMTEXT,
  imageUrl      VARCHAR(255),
  floor         INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  KEY idx_comment_post (postId),
  KEY idx_comment_user (userId),
  CONSTRAINT fk_comment_post
    FOREIGN KEY (postId) REFERENCES Post(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_comment_user
    FOREIGN KEY (userId) REFERENCES `User`(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Reply (
  id            INT NOT NULL AUTO_INCREMENT,
  postId        INT NOT NULL,
  commentId     INT NOT NULL,
  userId        INT NOT NULL,
  publishTime   DATETIME NOT NULL,
  content       MEDIUMTEXT,
  PRIMARY KEY (id),
  KEY idx_reply_comment (commentId),
  KEY idx_reply_post (postId),
  KEY idx_reply_user (userId),
  CONSTRAINT fk_reply_post
    FOREIGN KEY (postId) REFERENCES Post(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_reply_comment
    FOREIGN KEY (commentId) REFERENCES Comment(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_reply_user
    FOREIGN KEY (userId) REFERENCES `User`(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Like` (
  id         INT NOT NULL AUTO_INCREMENT,
  postId     INT NOT NULL,
  userId     INT NOT NULL,
  likeTime   DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_like_post_user (postId, userId),
  KEY idx_like_post (postId),
  KEY idx_like_user (userId),
  CONSTRAINT fk_like_post
    FOREIGN KEY (postId) REFERENCES Post(id)
    ON DELETE CASCADE,
  CONSTRAINT fk_like_user
    FOREIGN KEY (userId) REFERENCES `User`(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Resource (
  id            INT NOT NULL AUTO_INCREMENT,
  userId        INT NOT NULL,
  title         VARCHAR(255) NOT NULL,
  subject       INT NOT NULL DEFAULT 0,
  publishTime   DATETIME NOT NULL,
  size          BIGINT NOT NULL DEFAULT 0,
  content       MEDIUMTEXT,
  `path`        VARCHAR(512),
  imageUrl      VARCHAR(255),
  downloadCount INT NOT NULL DEFAULT 0,
  authority     INT NOT NULL DEFAULT 0,
  downloadUrl   VARCHAR(512),
  fileName      VARCHAR(255),
  PRIMARY KEY (id),
  KEY idx_resource_user_time (userId, publishTime),
  CONSTRAINT fk_resource_user
    FOREIGN KEY (userId) REFERENCES `User`(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ResourceCategories (
  id          INT NOT NULL AUTO_INCREMENT,
  resourceId  INT NOT NULL,
  category    INT NOT NULL,
  PRIMARY KEY (id),
  KEY idx_rescat_res (resourceId),
  CONSTRAINT fk_rescat_resource
    FOREIGN KEY (resourceId) REFERENCES Resource(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS DownloadHistory;

CREATE TABLE DownloadHistory (
  id            INT NOT NULL AUTO_INCREMENT,
  userId        INT NULL,
  resourceId    INT NOT NULL,
  title         VARCHAR(255),
  fileName      VARCHAR(255),
  downloadTime  DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY idx_dh_user_time (userId, downloadTime),
  KEY idx_dh_res_time (resourceId, downloadTime),
  CONSTRAINT fk_dh_user
    FOREIGN KEY (userId) REFERENCES `User`(id)
    ON DELETE SET NULL,
  CONSTRAINT fk_dh_resource
    FOREIGN KEY (resourceId) REFERENCES Resource(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `UserOnline` (
  `id`              INT          NOT NULL,           -- same as User.id (do NOT AUTO_INCREMENT)
  `name`            VARCHAR(20)  NULL,
  `password`        VARCHAR(255) NOT NULL,
  `email`           VARCHAR(40)  NOT NULL,
  `gender`          ENUM('male','female','unknown') NOT NULL DEFAULT 'unknown',
  `birthday`        DATE NULL,
  `registerTime`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `profilePhotoUrl` VARCHAR(255),
  `LogInNum`        INT          NOT NULL DEFAULT 0,
  `token`           VARCHAR(512) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_useronline_user`
    FOREIGN KEY (`id`) REFERENCES `User`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELIMITER //
CREATE TRIGGER trg_user_after_insert
AFTER INSERT ON `User`
FOR EACH ROW
BEGIN
  INSERT INTO Experience (id, exp, dailyExp) VALUES (NEW.id, 0, 0);
END//
DELIMITER ;

INSERT INTO Admin (email, `password`)
VALUES ('admin@example.com', 'AdR13N@9895');

-- Get-Content src/main/resources/schema.sql | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
-- (run this first)