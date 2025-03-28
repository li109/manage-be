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
package me.zhengjie.modules.order.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.order.domain.Order;
import me.zhengjie.modules.order.domain.dto.OrderQueryCriteria;
import me.zhengjie.modules.order.mapper.OrderMapper;
import me.zhengjie.modules.order.service.OrderService;
import me.zhengjie.modules.procedure.domain.Procedure;
import me.zhengjie.modules.procedure.mapper.ProcedureMapper;
import me.zhengjie.modules.procedure.service.ProcedureService;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.mapper.UserMapper;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xk
 * @description 服务实现
 * @date 2025-03-16
 **/
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final ProcedureMapper procedureMapper;
    private final UserMapper userMapper;
    private final ProcedureService procedureService;

    @Override
    public PageResult<Order> queryAll(OrderQueryCriteria criteria, Page<Object> page) {
        return PageUtil.toPage(orderMapper.findAll(criteria, page));
    }

    @Override
    public List<Order> queryAll(OrderQueryCriteria criteria) {
        return orderMapper.findAll(criteria);
    }

    @Override
    public Order getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order != null) {
            // 获取工序信息
            List<Procedure> list = procedureMapper.selectByOrder(order.getId());
            order.setList(list);
            // 获取创建人（开单员）
            if (order.getCreateUser() != null) {
                User user = userMapper.selectById(order.getCreateUser());
                if (user != null) {
                    order.setCreateUserName(user.getNickName());
                }
            }
        }

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Order resources) {
        resources.setCreateUser(SecurityUtils.getCurrentUserId());
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resources.setIsDelete(false);
        orderMapper.insert(resources);
        // 新增工序
        if (resources.getList() != null && !resources.getList().isEmpty()) {
            for (Procedure procedure : resources.getList()) {
//                procedure.setCreateTime(new Timestamp(System.currentTimeMillis()));
                procedure.setOrderId(resources.getId());
                procedure.setIsCheck(false);
                procedure.setIsDelete(false);
            }
            procedureService.saveBatch(resources.getList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Order resources) {
        resources.setUpdateUser(SecurityUtils.getCurrentUserId());
        resources.setUpdateTime(new Timestamp(System.currentTimeMillis()));

        Order order = getById(resources.getId());
        order.copy(resources);
        orderMapper.updateById(order);
        // 修改工序
        if (resources.getList() != null && !resources.getList().isEmpty()) {
            procedureService.updateBatchById(resources.getList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Long> ids) {
//        orderMapper.deleteBatchIds(ids);
        List<Order> list = listByIds(ids);
        for (Order order : list) {
            order.setIsDelete(true);
            order.setDeleteUser(SecurityUtils.getCurrentUserId());
            order.setDeleteTime(new Timestamp(System.currentTimeMillis()));
        }
        updateBatchById(list);
    }

    @Override
    public void download(List<Order> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Order order : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("生产单号", order.getOrderNum());
            map.put("下单时间", order.getOrderTime());
            map.put("客户名称", order.getCustomerName());
            map.put("产品名称", order.getProductTitle());
            map.put("交货日期", order.getDeliveryDate());
            map.put("成品数量", order.getProductCount());
            map.put("成品尺寸", order.getProductSize());
            map.put("拼版尺寸", order.getMakeUpSize());
            map.put("面纸配置", order.getFacialTissueSet());
            map.put("调纸尺寸", order.getAdjustPaperSize());
            map.put("切纸尺寸", order.getCutPaperSize());
            map.put("印刷颜色", order.getPrintColor());
            map.put("瓦纸配置", order.getTilePaperSet());
            map.put("瓦纸尺寸", order.getTilePaperSize());
            map.put("刀模", order.getKnifeMold());
            map.put("卡格要求", order.getCardRequirements());
            map.put("出货方式", order.getShipmentWay());
            map.put("打包要求", order.getPackRequire());
            map.put("重要备注", order.getRemarks());
            map.put("创建用户ID(开单员)", order.getCreateUser());
            map.put("创建时间", order.getCreateTime());
            map.put("修改用户ID", order.getUpdateUser());
            map.put("修改时间", order.getUpdateTime());
            map.put("是否删除：0-未删除；1-已删除", order.getIsDelete());
            map.put("删除用户ID", order.getDeleteUser());
            map.put("删除时间", order.getDeleteTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}