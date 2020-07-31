/**
 * Copyright 2020 Zhejiang Lab. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =============================================================
 */

package org.dubhe.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dubhe.domain.PtDevEnvs;
import org.dubhe.domain.dto.PtDevEnvsDTO;
import org.dubhe.domain.dto.PtDevEnvsQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @description  查询数据
 * @date 2020-03-17
 */
public interface PtDevEnvsService {

    /**
     * 查询数据分页
     *
     * @param criteria 条件
     * @param page     分页参数
     * @return Map<String, Object>
     */
    Map<String, Object> queryAll(PtDevEnvsQueryCriteria criteria, Page page);

    /**
     * 查询所有数据不分页
     *
     * @param criteria 条件参数
     * @return List<PtDevEnvsDto>
     */
    List<PtDevEnvsDTO> queryAll(PtDevEnvsQueryCriteria criteria);

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return PtDevEnvsDto
     */
    PtDevEnvsDTO findById(Long id);

    /**
     * 创建
     *
     * @param resources /
     * @return PtDevEnvsDto
     */
    PtDevEnvsDTO create(PtDevEnvs resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(PtDevEnvs resources);

    /**
     * 多选删除
     *
     * @param ids /
     */
    void deleteAll(Long[] ids);

    /**
     * 导出数据
     *
     * @param all      待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<PtDevEnvsDTO> all, HttpServletResponse response) throws IOException;
}
