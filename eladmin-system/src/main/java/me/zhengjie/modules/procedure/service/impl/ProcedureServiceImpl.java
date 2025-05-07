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
package me.zhengjie.modules.procedure.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.order.domain.Order;
import me.zhengjie.modules.order.mapper.OrderMapper;
import me.zhengjie.modules.procedure.domain.Procedure;
import me.zhengjie.modules.procedure.domain.dto.ProcedureQueryCriteria;
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
public class ProcedureServiceImpl extends ServiceImpl<ProcedureMapper, Procedure> implements ProcedureService {

    private final ProcedureMapper procedureMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    @Override
    public PageResult<Procedure> queryAll(ProcedureQueryCriteria criteria, Page<Object> page) {
        return PageUtil.toPage(procedureMapper.findAll(criteria, page));
    }

    @Override
    public List<Procedure> queryAll(ProcedureQueryCriteria criteria) {
        return procedureMapper.findAll(criteria);
    }

    @Override
    public Procedure getById(Long id) {
        Procedure procedure = procedureMapper.selectById(id);
        if (procedure.getIsCheck() != null && procedure.getIsCheck() && procedure.getCheckUser() != null) {
            User user = userMapper.selectById(procedure.getCheckUser());
            if (user != null) {
                procedure.setCheckUserName(user.getNickName());
            }
        }
        if (procedure.getCreateUser() != null) {
            User user = userMapper.selectById(procedure.getCheckUser());
            if (user != null) {
                procedure.setCreateUserName(user.getNickName());
            }
        }
        return procedure;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Procedure resources) {
        // 基础字段赋值
        resources.setCreateUser(SecurityUtils.getCurrentUserId());
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resources.setIsDelete(false);
        procedureMapper.insert(resources);
        // 变更工单主体信息
        if (resources.getOrderId() != null) {
            Order order = orderMapper.selectById(resources.getOrderId());
            if (order != null) {
//                // 获取当前工单完成工序数量
//                Integer count = procedureMapper.selectProcedureCount(order.getId());
//                order.setProgress(count * 10 + "%"); // 进度
                // 获取当前工单排序最靠后的工序名称
                String name = procedureMapper.getLastFinishProcedure(order.getId());
                order.setFinishProcedure(name); //当前工序
                orderMapper.updateById(order);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Procedure resources) {
        Procedure procedure = getById(resources.getId());
        procedure.copy(resources);
        // 基础字段赋值
        procedure.setUpdateUser(SecurityUtils.getCurrentUserId());
        procedure.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        procedure.setCreateUser(SecurityUtils.getCurrentUserId());
        procedure.setCreateTime(new Timestamp(System.currentTimeMillis()));
//        if (resources.getIsCheck() != null && resources.getIsCheck()) {
//            procedure.setIsCheck(true);
//            procedure.setCheckUser(SecurityUtils.getCurrentUserId());
//            procedure.setCheckTime(new Timestamp(System.currentTimeMillis()));
//        }
        procedureMapper.updateById(procedure);
        // 变更工单主体信息
        if (procedure.getOrderId() != null) {
            Order order = orderMapper.selectById(procedure.getOrderId());
            if (order != null) {
//                // 获取当前工单完成工序数量
//                Integer count = procedureMapper.selectProcedureCount(order.getId());
//                order.setProgress(count * 10 + "%"); // 进度
                // 获取当前工单排序最靠后的工序名称
                String name = procedureMapper.getLastFinishProcedure(order.getId());
                order.setFinishProcedure(name); // 当前工序
                // 回写最新工序的生产数量到工单完成数量字段
                Integer count = procedureMapper.getLastFinishCount(order.getId());
                if (count != null) {
                    order.setFinishCount(count.toString()); // 完成数量
                }

                orderMapper.updateById(order);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Long> ids) {
//        procedureMapper.deleteBatchIds(ids);
        List<Procedure> list = procedureMapper.selectBatchIds(ids);
        for (Procedure procedure : list) {
            procedure.setIsDelete(true);
            procedure.setDeleteUser(SecurityUtils.getCurrentUserId());
            procedure.setDeleteTime(new Timestamp(System.currentTimeMillis()));
        }
        updateBatchById(list);
    }

    @Override
    public void download(List<Procedure> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Procedure procedure : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("订单ID", procedure.getOrderId());
//            map.put("工艺要求", procedure.getWorkmanship());
            map.put("生产数量", procedure.getProduceNum());
            map.put("损耗数量", procedure.getLossNum());
            map.put("是否审核：0-未审核；1-已审核", procedure.getIsCheck());
            map.put("审核时间", procedure.getCheckTime());
            map.put("审核人ID", procedure.getCheckUser());
            map.put("创建用户ID（机长签字）", procedure.getCreateUser());
            map.put("创建时间", procedure.getCreateTime());
            map.put("修改用户ID", procedure.getUpdateUser());
            map.put("修改时间", procedure.getUpdateTime());
            map.put("是否删除：0-未删除；1-已删除", procedure.getIsDelete());
            map.put("删除用户ID", procedure.getDeleteUser());
            map.put("删除时间", procedure.getDeleteTime());
            map.put("关联procedure_status数据字典详情value", procedure.getDetailValue());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void check(Long id) {
        Procedure procedure = procedureMapper.selectById(id);
        if (procedure != null) {
            procedure.setIsCheck(true);
            procedure.setCheckUser(SecurityUtils.getCurrentUserId());
            procedure.setCheckTime(new Timestamp(System.currentTimeMillis()));
        }
        procedureMapper.updateById(procedure);
    }

    @Override
    public void deleteNotInIds(Long orderId, List<Long> ids) {
        Long updateUser = SecurityUtils.getCurrentUserId();
        baseMapper.deleteNotInIds(orderId, ids, updateUser);
    }
}