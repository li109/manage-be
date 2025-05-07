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

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.zhengjie.config.properties.FileProperties;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.order.domain.Order;
import me.zhengjie.modules.order.domain.dto.OrderQueryCriteria;
import me.zhengjie.modules.order.mapper.OrderMapper;
import me.zhengjie.modules.order.service.OrderService;
import me.zhengjie.modules.procedure.domain.Procedure;
import me.zhengjie.modules.procedure.mapper.ProcedureMapper;
import me.zhengjie.modules.procedure.service.ProcedureService;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.mapper.UserMapper;
import me.zhengjie.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private final FileProperties properties;

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
        resources.setIsFinish(false);
        // 生成顺序工单号：年月日+3位顺序数字
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        String dayStr = format1.format(calendar.getTime());
        String nowDay = format2.format(calendar.getTime());
        // 获取最新的工单号
        String orderNum = orderMapper.getMaxOrderNum(nowDay);
        // 如果没有历史工单，默认从1开始计算
        if (!StringUtils.isNotBlank(orderNum)) {
            orderNum = dayStr + "001";
        } else {
            int num = Integer.parseInt(orderNum.substring(8)) + 1;
            StringBuilder zero = new StringBuilder();
            for (int i = 0; i < 3 - String.valueOf(num).length(); i++) {
                zero.append("0");
            }
            orderNum = dayStr + zero.toString() + num;
        }
        resources.setOrderNum(orderNum);
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
        // 更新订单
        resources.setUpdateUser(SecurityUtils.getCurrentUserId());
        resources.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        Order order = getById(resources.getId());
        order.copy(resources);
        if (resources.getDeliveryDate()== null){
            order.setDeliveryDate(null);
        }
        orderMapper.updateById(order);
        // 修改工序（删除多余数据、更新回传数据、新增新数据）
        List<Procedure> list = resources.getList();
        List<Procedure> updateList = new ArrayList<>();
        List<Procedure> insertList = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (Procedure item : list) {
            item.setOrderId(order.getId());
            if (item.getId() == null) {
                item.setIsDelete(false);
                item.setIsCheck(false);
                insertList.add(item);
            } else {
                ids.add(item.getId());
                item.setUpdateUser(SecurityUtils.getCurrentUserId());
                item.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                updateList.add(item);
            }
        }
        procedureService.deleteNotInIds(order.getId(), ids);
        procedureService.saveBatch(insertList);
        procedureService.updateBatchById(updateList);

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
//            map.put("下单时间", order.getOrderTime());
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

    @Override
    public List<String> upload(MultipartFile[] multipartFile) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile item : multipartFile) {
            // 文件大小验证
            FileUtil.checkSize(properties.getMaxSize(), item.getSize());
            // 验证文件上传的格式
            String image = "gif jpg png jpeg bmp heif heic";
            String fileType = FileUtil.getExtensionName(item.getOriginalFilename());
            if (fileType != null && !image.contains(fileType)) {
                throw new BadRequestException("文件格式错误！, 仅支持图片类型： " + image + " 格式");
            }
            File file = FileUtil.upload(item, properties.getPath().getAvatar());
            if (ObjectUtil.isNull(file)) {
                throw new BadRequestException("上传失败");
            }
            urls.add("/avatar/" + file.getName());
        }
        return urls;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finish(Long id) {
        Order order = orderMapper.selectById(id);
        if (order != null) {
            order.setIsFinish(true);
            order.setUpdateUser(SecurityUtils.getCurrentUserId());
            order.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            // 获取最新工序的生产数量
            Integer count = procedureMapper.getLastFinishCount(id);
            if (count != null) {
                order.setFinishCount(count.toString());
            }
            orderMapper.updateById(order);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copy(Long id) {
        // 复制订单信息
        Order order = baseMapper.selectById(id);
        order.setId(null);
        order.setIsFinish(false);
        order.setFinishProcedure(null);
        order.setFinishCount(null);
        order.setCreateUser(SecurityUtils.getCurrentUserId());
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        order.setUpdateUser(null);
        order.setUpdateTime(null);
        order.setIsDelete(false);
        order.setDeleteUser(null);
        order.setDeleteTime(null);
        // 生成顺序工单号：年月日+3位顺序数字
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        String dayStr = format1.format(calendar.getTime());
        String nowDay = format2.format(calendar.getTime());
        // 获取最新的工单号
        String orderNum = orderMapper.getMaxOrderNum(nowDay);
        // 如果没有历史工单，默认从1开始计算
        if (!StringUtils.isNotBlank(orderNum)) {
            orderNum = dayStr + "001";
        } else {
            int num = Integer.parseInt(orderNum.substring(8)) + 1;
            StringBuilder zero = new StringBuilder();
            for (int i = 0; i < 3 - String.valueOf(num).length(); i++) {
                zero.append("0");
            }
            orderNum = dayStr + zero.toString() + num;
        }
        order.setOrderNum(orderNum);
        orderMapper.insert(order);
        // 复制工序信息
        List<Procedure> list = procedureMapper.selectSourceByOrder(id);
        for (Procedure item : list) {
            item.setId(null);
            item.setOrderId(order.getId());
            item.setProduceNum(null);
            item.setProduceNumStr(null);
            item.setLossNum(null);
            item.setRemarks(null);
            item.setIsCheck(false);
            item.setCheckTime(null);
            item.setCheckUser(null);
            item.setCreateUser(null);
            item.setCreateTime(null);
            item.setUpdateTime(null);
            item.setUpdateUser(null);
            item.setIsDelete(false);
            item.setDeleteUser(null);
            item.setDeleteTime(null);
        }
        procedureService.saveBatch(list);
    }
}