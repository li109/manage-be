package me.zhengjie.modules.order.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.modules.order.domain.Order;
import me.zhengjie.modules.order.domain.dto.OrderQueryCriteria;
import me.zhengjie.modules.order.service.OrderService;
import me.zhengjie.utils.PageResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
//    @PreAuthorize("@el.check('order:list')")
    public ResponseEntity<PageResult<Order>> queryOrder(OrderQueryCriteria criteria) {
//        if (criteria.getIsFinish() == null) {
//            criteria.setIsFinish(false);
//        }
        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
        return new ResponseEntity<>(orderService.queryAll(criteria, page), HttpStatus.OK);
    }

    @AnonymousGetMapping("orderNumList")
    @ApiOperation("查询所有未完成的订单编号(小程序端)")
    public ResponseEntity<List<Order>> orderNumList(OrderQueryCriteria criteria) {
        criteria.setIsFinish(false);
        return new ResponseEntity<>(orderService.queryAll(criteria), HttpStatus.OK);
    }

    @AnonymousGetMapping("info")
    @ApiOperation("查询订单信息(PC、小程序端)")
//    @GetMapping("info")
//    @PreAuthorize("@el.check('order:info')")
    public ResponseEntity<Order> queryOrderInfo(@ApiParam(value = "订单ID") @RequestParam("id") Long id) {
        Order order = orderService.getById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PostMapping("save")
    @Log("新增订单")
    @ApiOperation("新增订单")
//    @PreAuthorize("@el.check('order:add')")
    public ResponseEntity<Object> createOrder(@Validated @RequestBody Order resources) {
        orderService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("copy")
    @Log("生成新订单")
    @ApiOperation("生成新订单")
//    @PreAuthorize("@el.check('order:copy')")
    public ResponseEntity<Object> copy(@ApiParam(value = "订单ID") @RequestParam("id") Long id) {
        orderService.copy(id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("update")
    @Log("修改订单")
    @ApiOperation("修改订单")
//    @PreAuthorize("@el.check('order:edit')")
    public ResponseEntity<Object> updateOrder(@Validated @RequestBody Order resources) {
        orderService.update(resources);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("delete")
    @Log("删除订单")
    @ApiOperation("删除订单")
//    @PreAuthorize("@el.check('order:del')")
    public ResponseEntity<Object> deleteOrder(@ApiParam(value = "传ID数组[]") @RequestBody List<Long> ids) {
        orderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("完成订单")
    @ApiOperation("完成订单")
    @PostMapping("finish")
//    @PreAuthorize("@el.check('order:finish')")
    public ResponseEntity<Object> finishOrder(@ApiParam(value = "工单ID") @RequestParam("id") Long id) {
        orderService.finish(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("upload")
    @ApiOperation("上传附件(附件全地址为API地址+返回字符串)")
    public ResponseEntity<Object> upload(
            @ApiParam(value = "附件数组[]") @RequestParam("file")MultipartFile [] file
    ) {
        List<String> url = orderService.upload(file);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }
}