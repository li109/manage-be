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
import me.zhengjie.modules.procedure.domain.Procedure;
import me.zhengjie.modules.procedure.domain.dto.ProcedureQueryCriteria;
import me.zhengjie.modules.procedure.mapper.ProcedureMapper;
import me.zhengjie.modules.procedure.service.ProcedureService;
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

    @Override
    public PageResult<Procedure> queryAll(ProcedureQueryCriteria criteria, Page<Object> page) {
        return PageUtil.toPage(procedureMapper.findAll(criteria, page));
    }

    @Override
    public List<Procedure> queryAll(ProcedureQueryCriteria criteria) {
        return procedureMapper.findAll(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Procedure resources) {
        procedureMapper.insert(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Procedure resources) {
        Procedure procedure = getById(resources.getId());
        procedure.copy(resources);
        procedureMapper.updateById(procedure);
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
            map.put("工艺要求", procedure.getWorkmanship());
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
}