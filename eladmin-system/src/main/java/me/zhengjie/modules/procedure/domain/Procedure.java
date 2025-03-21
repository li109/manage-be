/*
 *  Copyright 2019-2025 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.procedure.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author xk
 * @description /
 * @date 2025-03-16
 **/
@Data
@TableName("tb_procedure")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Procedure implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "工艺要求")
    private String workmanship;

    @ApiModelProperty(value = "生产数量")
    private Integer produceNum;

    @ApiModelProperty(value = "损耗数量")
    private Integer lossNum;

    @ApiModelProperty(value = "是否审核：0-未审核；1-已审核")
    private Boolean isCheck;

    @ApiModelProperty(value = "审核时间")
    private Timestamp checkTime;

    @ApiModelProperty(value = "审核人ID")
    private Long checkUser;

    @ApiModelProperty(value = "创建用户ID（机长签字）")
    private Long createUser;

    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @ApiModelProperty(value = "修改用户ID")
    private Long updateUser;

    @ApiModelProperty(value = "修改时间")
    private Timestamp updateTime;

    @ApiModelProperty(value = "是否删除：0-未删除；1-已删除")
    private Boolean isDelete;

    @ApiModelProperty(value = "删除用户ID")
    private Long deleteUser;

    @ApiModelProperty(value = "删除时间")
    private Timestamp deleteTime;

    @ApiModelProperty(value = "关联procedure_status数据字典详情value")
    private String detailValue;

    @ApiModelProperty(value = "机长签字")
    @TableField(exist = false)
    private String createUserName;

    @ApiModelProperty(value = "工序名称")
    @TableField(exist = false)
    private String label;

    @ApiModelProperty(value = "工序排序")
    @TableField(exist = false)
    private Integer dictSort;

    public void copy(Procedure source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
