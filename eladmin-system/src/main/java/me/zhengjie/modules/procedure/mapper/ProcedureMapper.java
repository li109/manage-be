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
package me.zhengjie.modules.procedure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.zhengjie.modules.procedure.domain.Procedure;
import me.zhengjie.modules.procedure.domain.dto.ProcedureQueryCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xk
 * @date 2025-03-16
 **/
@Mapper
public interface ProcedureMapper extends BaseMapper<Procedure> {

    IPage<Procedure> findAll(@Param("criteria") ProcedureQueryCriteria criteria, Page<Object> page);

    List<Procedure> findAll(@Param("criteria") ProcedureQueryCriteria criteria);

    List<Procedure> selectByOrder(@Param("orderId") Long orderId);

    Integer selectProcedureCount(@Param("orderId") Long id);

    String getLastFinishProcedure(@Param("orderId") Long id);

    Integer getLastFinishCount(Long id);
}