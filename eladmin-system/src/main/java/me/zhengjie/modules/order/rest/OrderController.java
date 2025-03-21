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
package me.zhengjie.modules.order.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.modules.order.domain.Order;
import me.zhengjie.modules.order.domain.dto.OrderQueryCriteria;
import me.zhengjie.modules.order.service.OrderService;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author xk
 * @date 2025-03-16
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "业务-订单管理")
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
//
//    @ApiOperation("导出数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize("@el.check('order:list')")
//    public void exportOrder(HttpServletResponse response, OrderQueryCriteria criteria) throws IOException {
//        orderService.download(orderService.queryAll(criteria), response);
//    }

    @GetMapping("list")
    @ApiOperation("查询订单列表")
    @PreAuthorize("@el.check('order:list')")
    public ResponseEntity<PageResult<Order>> queryOrder(OrderQueryCriteria criteria) {
        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
        return new ResponseEntity<>(orderService.queryAll(criteria, page), HttpStatus.OK);
    }

    @AnonymousPostMapping("orderNumList")
    @ApiOperation("查询所有订单编号(小程序端，无需鉴权)")
    public ResponseEntity<List<Order>> orderNumList(OrderQueryCriteria criteria) {
        return new ResponseEntity<>(orderService.queryAll(criteria), HttpStatus.OK);
    }

    @GetMapping("info")
    @ApiOperation("查询订单信息")
    @PreAuthorize("@el.check('order:info')")
    public ResponseEntity<Order> queryOrderInfo(@ApiParam(value = "订单ID") @RequestParam("id") Long id) {
        Order order = orderService.getById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PostMapping("save")
    @Log("新增订单")
    @ApiOperation("新增订单")
    @PreAuthorize("@el.check('order:add')")
    public ResponseEntity<Object> createOrder(@Validated @RequestBody Order resources) {
        resources.setCreateUser(SecurityUtils.getCurrentUserId());
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resources.setIsDelete(false);
        orderService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("update")
    @Log("修改订单")
    @ApiOperation("修改订单")
    @PreAuthorize("@el.check('order:edit')")
    public ResponseEntity<Object> updateOrder(@Validated @RequestBody Order resources) {
        resources.setUpdateUser(SecurityUtils.getCurrentUserId());
        resources.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        orderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("delete")
    @Log("删除订单")
    @ApiOperation("删除订单")
    @PreAuthorize("@el.check('order:del')")
    public ResponseEntity<Object> deleteOrder(@ApiParam(value = "传ID数组[]") @RequestBody List<Long> ids) {
        orderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}