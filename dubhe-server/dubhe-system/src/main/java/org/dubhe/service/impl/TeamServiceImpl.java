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

package org.dubhe.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dubhe.dao.TeamMapper;
import org.dubhe.domain.dto.TeamCreateDTO;
import org.dubhe.domain.dto.TeamDTO;
import org.dubhe.domain.dto.TeamQueryDTO;
import org.dubhe.domain.dto.TeamUpdateDTO;
import org.dubhe.domain.entity.Team;
import org.dubhe.service.TeamService;
import org.dubhe.service.convert.TeamConvert;
import org.dubhe.utils.FileUtil;
import org.dubhe.utils.PageUtil;
import org.dubhe.utils.WrapperHelp;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @Description :团队服务 实现类
 * @Date 2020-06-01
 */
@Service
@CacheConfig(cacheNames = "team")
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamConvert teamConvert;


    /**
     * 获取团队列表
     *
     * @param criteria 查询团队列表条件
     * @return java.util.List<org.dubhe.domain.dto.TeamDTO> 团队列表条件
     */
    @Override
    @Cacheable
    public List<TeamDTO> queryAll(TeamQueryDTO criteria) {
        return teamConvert.toDto(teamMapper.selectList(WrapperHelp.getWrapper(criteria)));
    }


    /**
     * 分页查询团队列表
     *
     * @param criteria 查询请求条件
     * @param page     分页实体
     * @return java.lang.Object 团队列表
     */
    @Override
    @Cacheable
    public Object queryAll(TeamQueryDTO criteria, Page page) {
        IPage<Team> teamList = teamMapper.selectPage(page, WrapperHelp.getWrapper(criteria));
        return PageUtil.toPage(teamList, teamConvert::toDto);
    }


    /**
     * 查询团队列表
     *
     * @param page 分页请求实体
     * @return java.util.List<org.dubhe.domain.dto.TeamDTO> 团队列表
     */
    @Override
    @Cacheable
    public List<TeamDTO> queryAll(Page page) {
        IPage<Team> teamList = teamMapper.selectPage(page, null);
        return teamConvert.toDto(teamList.getRecords());
    }

    /**
     * 根据ID插叙团队信息
     *
     * @param id id
     * @return org.dubhe.domain.dto.TeamDTO 团队返回实例
     */
    @Override
    @Cacheable(key = "#p0")
    public TeamDTO findById(Long id) {
        Team team = teamMapper.selectCollById(id);
        return teamConvert.toDto(team);
    }


    /**
     * 新增团队信息
     *
     * @param resources 团队新增请求实体
     * @return org.dubhe.domain.dto.TeamDTO 团队返回实例
     */
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public TeamDTO create(TeamCreateDTO resources) {
        Team team = Team.builder().build();
        BeanUtils.copyProperties(resources, team);
        teamMapper.insert(team);
        return teamConvert.toDto(team);
    }


    /**
     * 修改团队
     *
     * @param resources 团队修改请求实体
     * @return void
     */
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(TeamUpdateDTO resources) {
        Team team = Team.builder().build();
        BeanUtils.copyProperties(resources, team);
        teamMapper.updateById(team);
    }

    /**
     * 团队删除
     *
     * @param teamDtos 团队删除列表
     * @return void
     */
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<TeamDTO> teamDtos) {
        for (TeamDTO teamDto : teamDtos) {
            teamMapper.deleteById(teamDto.getId());
        }
    }

    /**
     * 团队信息导出
     *
     * @param teamDtos 团队列表
     * @param response
     * @return void
     */
    @Override
    public void download(List<TeamDTO> teamDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TeamDTO teamDTO : teamDtos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("名称", teamDTO.getName());
            map.put("状态", teamDTO.getEnabled() ? "启用" : "停用");
            map.put("创建日期", teamDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


}
