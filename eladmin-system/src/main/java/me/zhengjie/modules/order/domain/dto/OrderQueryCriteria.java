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
package me.zhengjie.modules.order.domain.dto;

import lombok.Data;
import io.swagger.annotations.ApiModelProperty;

/**
* @author xk
* @date 2025-03-16
**/
@Data
public class OrderQueryCriteria{

    @ApiModelProperty(value = "页码", example = "1")
    private Integer page = 1;

    @ApiModelProperty(value = "每页数据量", example = "10")
    private Integer size = 10;

    @ApiModelProperty(value = "生产单号")
    private String orderNum;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "产品名称")
    private String productTitle;

    @ApiModelProperty(value = "订单是否完成（默认未完成）：0-未完成；1-已完成")
    private Boolean isFinish;

    @ApiModelProperty(value = "成品尺寸")
    private String productSize;

    @ApiModelProperty(value = "面纸配置")
    private String facialTissueSet;

    @ApiModelProperty(value = "开始时间（yyyy-MM-dd HH:mm:ss）")
    private String startTime;

    @ApiModelProperty(value = "结束时间（yyyy-MM-dd HH:mm:ss）")
    private String endTime;

}