-- 对话历史表
DROP  TABLE IF EXISTS chat_history;
CREATE TABLE chat_history
(
    id          bigint auto_increment comment 'id' primary key,
    message     text                               not null comment '消息',
    message_type smallint                           not null comment '1-user,2-ai',
    app_id       bigint                             not null comment '应用id',
    user_id      bigint                             not null comment '创建用户id',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint  default 0                 not null comment '是否删除',
    INDEX idx_appId (app_id),                       -- 提升基于应用的查询性能
    INDEX idx_createTime (create_time),             -- 提升基于时间的查询性能
    INDEX idx_appId_createTime (app_id, create_time) -- 游标查询核心索引
) comment '对话历史' ;
