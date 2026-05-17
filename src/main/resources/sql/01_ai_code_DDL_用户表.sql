-- 用户表

DROP TABLE IF EXISTS user;

create table  user
(
    id           bigint auto_increment comment 'id' primary key,
    user_account  varchar(256)                       not null comment '账号',
    user_password varchar(512)                       not null comment '密码',
    user_name     varchar(256)                       null comment '用户昵称',
    user_avatar   varchar(1024)                      null comment '用户头像',
    user_profile  varchar(512)                       null comment '用户简介',
    user_role     tinyint  default 0                 not null comment '用户角色：0-user,1-admin',
#     vip_expire_time datetime                               null comment '会员过期时间',
#     vip_code       varchar(128)                           null comment '会员兑换码',
#     vip_number     bigint                                 null comment '会员编号',
#     share_code     varchar(20)  default                   null comment '分享码',
#     invite_user    bigint       default                   null comment '邀请用户 id',
    edit_time     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userAccount (user_account),
    INDEX idx_userName (user_name)
) comment '用户';


-- 插入20条用户虚拟数据，id从10开始，头像/图片为空
INSERT INTO user (id, user_account, user_password, user_name, user_avatar, user_profile, user_role, edit_time, create_time, update_time, is_delete)
VALUES
    (10, 'user1001', 'e10adc3949ba59abbe56e057f20f883e', '张三', '', '普通用户，热爱生活', 0, NOW(), NOW(), NOW(), 0),
    (11, 'user1002', 'e10adc3949ba59abbe56e057f20f883e', '李四', '', '技术爱好者，喜欢编程', 0, NOW(), NOW(), NOW(), 0),
    (12, 'user1003', 'e10adc3949ba59abbe56e057f20f883e', '王五', '', '专注后端开发', 0, NOW(), NOW(), NOW(), 0),
    (13, 'user1004', 'e10adc3949ba59abbe56e057f20f883e', '赵六', '', '前端开发工程师', 0, NOW(), NOW(), NOW(), 0),
    (14, 'user1005', 'e10adc3949ba59abbe56e057f20f883e', '钱七', '', '全栈开发者', 0, NOW(), NOW(), NOW(), 0),
    (15, 'user1006', 'e10adc3949ba59abbe56e057f20f883e', '孙八', '', '数据库管理员', 0, NOW(), NOW(), NOW(), 0),
    (16, 'user1007', 'e10adc3949ba59abbe56e057f20f883e', '周九', '', '测试工程师', 0, NOW(), NOW(), NOW(), 0),
    (17, 'user1008', 'e10adc3949ba59abbe56e057f20f883e', '吴十', '', '运维工程师', 0, NOW(), NOW(), NOW(), 0),
    (18, 'user1009', 'e10adc3949ba59abbe56e057f20f883e', '郑十一', '', '产品经理', 0, NOW(), NOW(), NOW(), 0),
    (19, 'user1010', 'e10adc3949ba59abbe56e057f20f883e', '王十二', '', 'UI设计师', 0, NOW(), NOW(), NOW(), 0),
    (20, 'user1011', 'e10adc3949ba59abbe56e057f20f883e', '刘十三', '', '运营专员', 0, NOW(), NOW(), NOW(), 0),
    (21, 'user1012', 'e10adc3949ba59abbe56e057f20f883e', '陈十四', '', '市场专员', 0, NOW(), NOW(), NOW(), 0),
    (22, 'user1013', 'e10adc3949ba59abbe56e057f20f883e', '杨十五', '', '销售顾问', 0, NOW(), NOW(), NOW(), 0),
    (23, 'user1014', 'e10adc3949ba59abbe56e057f20f883e', '黄十六', '', '人力资源', 0, NOW(), NOW(), NOW(), 0),
    (24, 'user1015', 'e10adc3949ba59abbe56e057f20f883e', '秦十七', '', '财务专员', 0, NOW(), NOW(), NOW(), 0),
    (25, 'admin001', 'e10adc3949ba59abbe56e057f20f883e', '管理员1', '', '系统管理员', 1, NOW(), NOW(), NOW(), 0),
    (26, 'admin002', 'e10adc3949ba59abbe56e057f20f883e', '超级管理员', '', '最高权限管理员', 1, NOW(), NOW(), NOW(), 0),
    (27, 'user1016', 'e10adc3949ba59abbe56e057f20f883e', '朱十八', '', '自由开发者', 0, NOW(), NOW(), NOW(), 0),
    (28, 'user1017', 'e10adc3949ba59abbe56e057f20f883e', '高十九', '', '技术博主', 0, NOW(), NOW(), NOW(), 0),
    (29, 'user1018', 'e10adc3949ba59abbe56e057f20f883e', '林二十', '', '编程学习者', 0, NOW(), NOW(), NOW(), 0);


