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
package me.zhengjie.modules.procedure.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import me.zhengjie.modules.procedure.domain.Procedure;
import me.zhengjie.modules.procedure.domain.dto.ProcedureQueryCriteria;
import me.zhengjie.utils.PageResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author xk
 * @description 服务接口
 * @date 2025-03-16
 **/
public interface ProcedureService extends IService<Procedure> {

    /**
     * 查询数据分页
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return PageResult
     */
    PageResult<Procedure> queryAll(ProcedureQueryCriteria criteria, Page<Object> page);

    /**
     * 查询所有数据不分页
     *
     * @param criteria 条件参数
     * @return List<ProcedureDto>
     */
    List<Procedure> queryAll(ProcedureQueryCriteria criteria);

    /**
     * 根据主键查询单条数据
     *
     * @param id 条件参数
     * @return Procedure
     */
    Procedure getById(Long id);

    /**
     * 创建
     *
     * @param resources /
     */
    void create(Procedure resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(Procedure resources);

    /**
     * 多选删除
     *
     * @param ids /
     */
    void deleteAll(List<Long> ids);

    /**
     * 导出数据
     *
     * @param all      待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<Procedure> all, HttpServletResponse response) throws IOException;

    void check(Long id);

    void deleteNotInIds(Long orderId,List<Long> ids);
}