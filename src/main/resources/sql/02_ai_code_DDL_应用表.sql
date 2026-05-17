-- 应用表
DROP TABLE IF EXISTS app;
CREATE TABLE app
(
    id           bigint auto_increment comment 'id' primary key,
    app_name      varchar(256)                       null comment '应用名称',
    cover        varchar(512)                       null comment '应用封面',
    init_prompt   text                               null comment '应用初始化的 prompt',
    code_gen_type  varchar(64)                        null comment '代码生成类型（枚举）',
    deploy_key    varchar(64)                        null comment '部署标识',
    deployed_time datetime                           null comment '部署时间',
    priority     int      default 0                 not null comment '优先级',
    user_id       bigint                             not null comment '创建用户id',
    edit_time     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_deployKey (deploy_key), -- 确保部署标识唯一
    INDEX idx_appName (app_name),         -- 提升基于应用名称的查询性能
    INDEX idx_userId (user_id)            -- 提升基于用户 ID 的查询性能
) comment '应用';
