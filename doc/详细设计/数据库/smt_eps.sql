/*
Navicat MySQL Data Transfer

Source Server         : 本地连接（SMT）
Source Server Version : 50640
Source Host           : localhost:3306
Source Database       : smt_eps

Target Server Type    : MYSQL
Target Server Version : 50640
File Encoding         : 65001

Date: 2018-12-28 19:01:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for action_log
-- ----------------------------
DROP TABLE IF EXISTS `action_log`;
CREATE TABLE `action_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `time` datetime NOT NULL,
  `url` varchar(1024) NOT NULL,
  `ip` varchar(16) NOT NULL,
  `parameters` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19559 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for center_login
-- ----------------------------
DROP TABLE IF EXISTS `center_login`;
CREATE TABLE `center_login` (
  `line` int(11) NOT NULL COMMENT 'line的id',
  `mac` varchar(32) NOT NULL COMMENT '树莓派mac',
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(16) NOT NULL COMMENT '树莓派ip',
  `port` int(11) NOT NULL DEFAULT '12345' COMMENT '值为12345，不可修改',
  PRIMARY KEY (`id`),
  KEY `mac` (`mac`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for center_state
-- ----------------------------
DROP TABLE IF EXISTS `center_state`;
CREATE TABLE `center_state` (
  `line` int(11) NOT NULL,
  `alarming` bit(1) NOT NULL,
  `converyPaused` bit(1) NOT NULL,
  PRIMARY KEY (`line`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL COMMENT '配置项名',
  `value` varchar(128) DEFAULT NULL COMMENT '配置项值',
  `description` varchar(128) DEFAULT NULL COMMENT '配置项描述',
  `line` int(11) DEFAULT NULL COMMENT '线别',
  `enabled` bit(1) DEFAULT b'1' COMMENT '是否启用',
  `alias` varchar(128) DEFAULT NULL COMMENT '别名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for display
-- ----------------------------
DROP TABLE IF EXISTS `display`;
CREATE TABLE `display` (
  `line` int(11) NOT NULL,
  `work_order` varchar(128) DEFAULT NULL,
  `board_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`line`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for epsappversion
-- ----------------------------
DROP TABLE IF EXISTS `epsappversion`;
CREATE TABLE `epsappversion` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `version_code` int(11) NOT NULL COMMENT '版本码',
  `version_name` varchar(255) NOT NULL COMMENT '版本名',
  `version_des` varchar(255) DEFAULT NULL COMMENT '版本描述',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for line
-- ----------------------------
DROP TABLE IF EXISTS `line`;
CREATE TABLE `line` (
  `id` int(11) NOT NULL DEFAULT '0' COMMENT '从0开始，逐步加1',
  `line` varchar(16) NOT NULL COMMENT '线号',
  PRIMARY KEY (`id`),
  KEY `line` (`line`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for material_info
-- ----------------------------
DROP TABLE IF EXISTS `material_info`;
CREATE TABLE `material_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `material_no` varchar(128) NOT NULL COMMENT '物料号',
  `perifd_of_validity` int(11) DEFAULT NULL COMMENT '保质期',
  `enable` int(11) DEFAULT NULL COMMENT '是否有效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for operation
-- ----------------------------
DROP TABLE IF EXISTS `operation`;
CREATE TABLE `operation` (
  `id` bigint(32) unsigned NOT NULL AUTO_INCREMENT,
  `operator` varchar(32) NOT NULL COMMENT '操作者',
  `time` datetime NOT NULL COMMENT '操作时间',
  `type` int(11) NOT NULL COMMENT '0:上料 1:换料 2:检查 3:全料检查 4:仓库上料',
  `result` varchar(32) NOT NULL COMMENT '操作结果',
  `lineseat` varchar(32) NOT NULL COMMENT '操作站位',
  `material_no` varchar(32) NOT NULL COMMENT '操作料号',
  `old_material_no` varchar(32) DEFAULT NULL COMMENT '旧的操作料号',
  `scanlineseat` varchar(32) NOT NULL COMMENT '扫描的站位',
  `remark` varchar(32) NOT NULL COMMENT '操作失败原因或是其它备注',
  `program_id` varchar(32) NOT NULL COMMENT 'PROGRAM_ID',
  `line` int(11) NOT NULL,
  `work_order` varchar(128) NOT NULL,
  `board_type` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `time` (`time`) USING BTREE,
  KEY `type` (`type`) USING BTREE,
  KEY `result` (`result`) USING BTREE,
  KEY `lineseat` (`lineseat`) USING BTREE,
  KEY `material_no` (`material_no`) USING BTREE,
  KEY `fileid` (`program_id`) USING BTREE,
  KEY `board_type` (`board_type`) USING BTREE,
  KEY `work_order` (`work_order`) USING BTREE,
  KEY `line` (`line`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2402472 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for program
-- ----------------------------
DROP TABLE IF EXISTS `program`;
CREATE TABLE `program` (
  `id` varchar(32) NOT NULL,
  `file_name` varchar(128) NOT NULL COMMENT '文件名',
  `client` varchar(32) DEFAULT NULL COMMENT '产品客户',
  `machine_name` varchar(32) DEFAULT NULL COMMENT '机器名',
  `version` varchar(32) DEFAULT NULL COMMENT '版本',
  `machine_config` varchar(32) DEFAULT NULL COMMENT '机器配置',
  `program_no` varchar(32) DEFAULT NULL COMMENT '程序编号',
  `line` int(11) NOT NULL COMMENT '线别',
  `effective_date` varchar(32) DEFAULT NULL COMMENT '生效日期',
  `PCB_no` varchar(32) DEFAULT NULL COMMENT 'PCB编号',
  `BOM` varchar(256) DEFAULT NULL COMMENT 'BOM文件',
  `program_name` varchar(128) DEFAULT NULL COMMENT '程序名',
  `auditor` varchar(32) DEFAULT NULL COMMENT '审核者',
  `create_time` datetime NOT NULL COMMENT '创建日期，该日期为此数据进入数据库的日期',
  `work_order` varchar(128) NOT NULL COMMENT '工单',
  `board_type` int(11) NOT NULL DEFAULT '0' COMMENT '0：默认 1：AB面 2：A面 3:B面',
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '0:未开始，1：进行中，2：已完成，3：已作废',
  `structure` int(11) DEFAULT '1' COMMENT '几联板',
  `plan_product` int(11) DEFAULT '0' COMMENT '计划生产数量',
  `already_product` int(11) DEFAULT '0' COMMENT '已经生产数量',
  PRIMARY KEY (`id`),
  KEY `client` (`client`) USING BTREE,
  KEY `program_no` (`program_no`) USING BTREE,
  KEY `line` (`line`) USING BTREE,
  KEY `program_name` (`program_name`) USING BTREE,
  KEY `create_time` (`create_time`) USING BTREE,
  KEY `state` (`state`) USING BTREE,
  KEY `work_order` (`work_order`) USING BTREE,
  KEY `board_type` (`board_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for program_item
-- ----------------------------
DROP TABLE IF EXISTS `program_item`;
CREATE TABLE `program_item` (
  `program_id` varchar(32) NOT NULL COMMENT '排位表的ID',
  `lineseat` varchar(32) NOT NULL COMMENT '站位',
  `alternative` bit(1) NOT NULL COMMENT '是否属于替补料',
  `material_no` varchar(128) NOT NULL COMMENT '料号',
  `specitification` varchar(1024) DEFAULT NULL COMMENT '规格',
  `position` varchar(1024) DEFAULT NULL COMMENT '单板位置',
  `quantity` int(11) DEFAULT NULL COMMENT '数量',
  `serial_no` int(11) NOT NULL DEFAULT '0' COMMENT '序列号',
  PRIMARY KEY (`program_id`,`lineseat`,`material_no`),
  KEY `lineseat` (`lineseat`) USING BTREE,
  KEY `alternative` (`alternative`) USING BTREE,
  KEY `material_no` (`material_no`) USING BTREE,
  KEY `program_id` (`program_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for program_item_visit
-- ----------------------------
DROP TABLE IF EXISTS `program_item_visit`;
CREATE TABLE `program_item_visit` (
  `program_id` varchar(32) NOT NULL COMMENT '对应的工单主键',
  `lineseat` varchar(32) NOT NULL COMMENT '原始操作站位',
  `material_no` varchar(32) NOT NULL COMMENT '原始操作料号',
  `scan_lineseat` varchar(32) DEFAULT NULL COMMENT '扫描的站位',
  `scan_material_no` varchar(32) DEFAULT NULL COMMENT '扫描的料号',
  `last_operation_type` int(11) DEFAULT NULL COMMENT '最后一次操作类型：0:上料 1:换料 2:检查 3:全料检查 4:仓库发料',
  `store_issue_result` int(11) NOT NULL DEFAULT '2' COMMENT '发料结果 对应 last_operation_type 4:仓库发料',
  `store_issue_time` datetime NOT NULL COMMENT '发料时间',
  `feed_result` int(11) NOT NULL DEFAULT '2' COMMENT '上料结果 对应 last_operation_type 0:上料',
  `feed_time` datetime NOT NULL COMMENT '上料时间',
  `change_result` int(11) NOT NULL DEFAULT '2' COMMENT '换料结果 对应 last_operation_type 1:换料',
  `change_time` datetime NOT NULL COMMENT '换料时间',
  `check_result` int(11) NOT NULL DEFAULT '2' COMMENT '检料结果 对应 last_operation_type 2:检查',
  `check_time` datetime NOT NULL COMMENT '检料时间',
  `check_all_result` int(11) NOT NULL DEFAULT '2' COMMENT '全检结果 对应 last_operation_type 3:全检',
  `check_all_time` datetime NOT NULL COMMENT '全检时间',
  `first_check_all_result` int(11) NOT NULL DEFAULT '2' COMMENT '首检结果 对应 last_operation_type 5:首检',
  `first_check_all_time` datetime NOT NULL COMMENT '首检时间',
  `last_operation_time` datetime DEFAULT NULL COMMENT '最后一次操作时间',
  PRIMARY KEY (`lineseat`,`material_no`,`program_id`),
  KEY `lineseat` (`lineseat`) USING BTREE,
  KEY `material_no` (`material_no`) USING BTREE,
  KEY `scan_lineseat` (`scan_lineseat`) USING BTREE,
  KEY `scan_material_no` (`scan_material_no`) USING BTREE,
  KEY `program_id` (`program_id`) USING BTREE,
  KEY `last_operation_type` (`last_operation_type`) USING BTREE,
  KEY `store_issue_result` (`store_issue_result`) USING BTREE,
  KEY `store_issue_time` (`store_issue_time`) USING BTREE,
  KEY `feed_result` (`feed_result`) USING BTREE,
  KEY `feed_time` (`feed_time`) USING BTREE,
  KEY `change_result` (`change_result`) USING BTREE,
  KEY `change_time` (`change_time`) USING BTREE,
  KEY `check_result` (`check_result`) USING BTREE,
  KEY `check_time` (`check_time`) USING BTREE,
  KEY `check_all_result` (`check_all_result`) USING BTREE,
  KEY `check_all_time` (`check_all_time`) USING BTREE,
  KEY `last_operation_time` (`last_operation_time`) USING BTREE,
  KEY `first_check_all_time` (`first_check_all_time`) USING BTREE,
  KEY `first_check_all_result` (`first_check_all_result`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for socket_log
-- ----------------------------
DROP TABLE IF EXISTS `socket_log`;
CREATE TABLE `socket_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `protocol` varchar(64) NOT NULL,
  `serial_no` smallint(6) NOT NULL,
  `data` varchar(256) NOT NULL,
  `sender_ip` varchar(16) NOT NULL,
  `receiver_ip` varchar(16) NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `protocol` (`protocol`) USING BTREE,
  KEY `time` (`time`) USING BTREE,
  KEY `sender_ip` (`sender_ip`) USING BTREE,
  KEY `receiver_ip` (`receiver_ip`) USING BTREE,
  KEY `serial_no` (`serial_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1549391 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for stock_log
-- ----------------------------
DROP TABLE IF EXISTS `stock_log`;
CREATE TABLE `stock_log` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `timestamp` varchar(32) NOT NULL,
  `material_no` varchar(128) NOT NULL,
  `quantity` int(11) NOT NULL,
  `operator` varchar(32) NOT NULL,
  `operation_time` datetime NOT NULL,
  `position` varchar(32) NOT NULL,
  `custom` varchar(32) NOT NULL,
  `production_date` date DEFAULT NULL,
  `target_work_order` varchar(32) DEFAULT NULL,
  `target_line` varchar(32) DEFAULT NULL,
  `target_board_type` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `material_no` (`material_no`) USING BTREE,
  KEY `quantity` (`quantity`) USING BTREE,
  KEY `operator` (`operator`) USING BTREE,
  KEY `operation_time` (`operation_time`) USING BTREE,
  KEY `position` (`position`) USING BTREE,
  KEY `custom` (`custom`) USING BTREE,
  KEY `target_work_order` (`target_work_order`) USING BTREE,
  KEY `target_line` (`target_line`) USING BTREE,
  KEY `target_board_type` (`target_board_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=193704228 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(128) NOT NULL,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `name` varchar(16) DEFAULT NULL,
  `type` int(11) NOT NULL COMMENT '0:仓库操作员;1:厂线操作员;2:IPQC;3:超级管理员;4:生产管理员；5：品质管理员；6：工程管理员',
  `password` varchar(32) DEFAULT NULL COMMENT '管理员密码',
  `create_time` datetime NOT NULL,
  `class_type` int(11) NOT NULL COMMENT '0:白班；1：夜班',
  PRIMARY KEY (`id`),
  KEY `enabled` (`enabled`) USING BTREE,
  KEY `type` (`type`) USING BTREE,
  KEY `create_time` (`create_time`) USING BTREE,
  KEY `name` (`name`) USING BTREE,
  KEY `class_type` (`class_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
