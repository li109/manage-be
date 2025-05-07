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
package me.zhengjie.modules.order.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.zhengjie.modules.procedure.domain.Procedure;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author xk
 * @description /
 * @date 2025-03-16
 **/
@Data
@TableName("tb_order")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Order implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "生产单号")
    private String orderNum;

//    @ApiModelProperty(value = "下单时间")
//    private Timestamp orderTime;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "产品名称")
    private String productTitle;

    @ApiModelProperty(value = "交货日期")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Timestamp deliveryDate;

    @ApiModelProperty(value = "成品数量")
    private String productCount;

    @ApiModelProperty(value = "成品尺寸")
    private String productSize;

    @ApiModelProperty(value = "拼版尺寸")
    private String makeUpSize;

    @ApiModelProperty(value = "面纸配置")
    private String facialTissueSet;

    @ApiModelProperty(value = "面纸尺寸")
    private String facialTissueSize;

    @ApiModelProperty(value = "调纸尺寸")
    private String adjustPaperSize;

    @ApiModelProperty(value = "切纸尺寸")
    private String cutPaperSize;

    @ApiModelProperty(value = "印刷颜色")
    private String printColor;

    @ApiModelProperty(value = "印刷专色")
    private String spotColor;

    @ApiModelProperty(value = "瓦纸配置")
    private String tilePaperSet;

    @ApiModelProperty(value = "瓦纸尺寸")
    private String tilePaperSize;

    @ApiModelProperty(value = "刀模")
    private String knifeMold;

    @ApiModelProperty(value = "卡格要求")
    private String cardRequirements;

    @ApiModelProperty(value = "出货方式")
    private String shipmentWay;

    @ApiModelProperty(value = "打包要求")
    private String packRequire;

    @ApiModelProperty(value = "重要备注")
    private String remarks;

    @ApiModelProperty(value = "创建用户ID")
    private Long createUser;

    @ApiModelProperty(value = "开单员")
    @TableField(exist = false)
    private String createUserName;

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

    @ApiModelProperty(value = "工序列表")
    @TableField(exist = false)
    private List<Procedure> list;

//    @ApiModelProperty(value = "完成进度（%）")
//    private String progress;

    @ApiModelProperty(value = "当前完成工序")
    private String finishProcedure;

    @ApiModelProperty(value = "附件地址")
    private String fileUrl;

    @ApiModelProperty(value = "订单是否完成：0-未完成；1-已完成")
    private Boolean isFinish;

    @ApiModelProperty(value = "完成数量")
    private String finishCount;

    public void copy(Order source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
