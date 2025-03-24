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
package me.zhengjie.modules.procedure.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.modules.procedure.domain.Procedure;
import me.zhengjie.modules.procedure.service.ProcedureService;
import me.zhengjie.modules.system.domain.DictDetail;
import me.zhengjie.modules.system.service.DictDetailService;
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
@Api(tags = "业务-工序管理")
@RequestMapping("/api/procedure")
public class ProcedureController {

    private final ProcedureService procedureService;
    private final DictDetailService dictDetailService;

//    @ApiOperation("导出数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize("@el.check('procedure:list')")
//    public void exportProcedure(HttpServletResponse response, ProcedureQueryCriteria criteria) throws IOException {
//        procedureService.download(procedureService.queryAll(criteria), response);
//    }

//    @GetMapping
//    @ApiOperation("查询工序")
//    @PreAuthorize("@el.check('procedure:list')")
//    public ResponseEntity<PageResult<Procedure>> queryProcedure(ProcedureQueryCriteria criteria){
//        Page<Object> page = new Page<>(criteria.getPage(), criteria.getSize());
//        return new ResponseEntity<>(procedureService.queryAll(criteria,page),HttpStatus.OK);
//    }

    @AnonymousPostMapping("save")
    @Log("新增工序")
    @ApiOperation("新增工序(小程序端，无需鉴权)")
    public ResponseEntity<Object> createProcedure(@Validated @RequestBody Procedure resources) {
        resources.setCreateUser(SecurityUtils.getCurrentUserId());
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resources.setIsDelete(false);
        procedureService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation("查询工序列表(小程序端，无需鉴权)")
    @AnonymousGetMapping("getProcedureList")
    public ResponseEntity<List<DictDetail>> queryDictDetail() {
        return new ResponseEntity<>(dictDetailService.getDictByName("procedure_status"), HttpStatus.OK);
    }

//    @PutMapping("update")
//    @Log("修改工序")
//    @ApiOperation("修改工序")
//    @PreAuthorize("@el.check('procedure:edit')")
//    public ResponseEntity<Object> updateProcedure(@Validated @RequestBody Procedure resources) {
//        resources.setUpdateUser(SecurityUtils.getCurrentUserId());
//        resources.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//        procedureService.update(resources);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

    @Log("审核工序")
    @ApiOperation("审核工序")
    @PostMapping(value = "check")
    @PreAuthorize("@el.check('order:check')")
    public ResponseEntity<Object> checkProcedure(@ApiParam(value = "工序ID") @RequestParam("id") Long id) {
        Procedure procedure = new Procedure();
        procedure.setId(id);
        procedure.setIsCheck(true);
        procedure.setCheckUser(SecurityUtils.getCurrentUserId());
        procedure.setCheckTime(new Timestamp(System.currentTimeMillis()));
        procedureService.update(procedure);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

//    @DeleteMapping("delete")
//    @Log("删除工序")
//    @ApiOperation("删除工序")
//    @PreAuthorize("@el.check('procedure:del')")
//    public ResponseEntity<Object> deleteProcedure(@ApiParam(value = "传ID数组[]") @RequestBody List<Long> ids) {
//        procedureService.deleteAll(ids);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}