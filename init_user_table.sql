-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status` INT(1) DEFAULT 1 COMMENT '状态(0:禁用;1:启用)',
    `create_dt` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_dt` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试用户数据（密码为：123456）
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `status`)
VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', '13800138000', 'admin@example.com', 1),
('test', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', '13800138001', 'test@example.com', 1);
