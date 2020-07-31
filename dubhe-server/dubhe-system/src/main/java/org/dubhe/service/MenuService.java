/**
 * Copyright 2019-2020 Zheng Jie
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
 */
package org.dubhe.service;

import org.dubhe.domain.entity.Menu;
import org.dubhe.domain.dto.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description :菜单服务 Service
 * @Date 2020-06-01
 */
public interface MenuService {

    /**
     * 查询全部数据
     *
     * @param criteria 条件
     * @return /
     */
    List<MenuDTO> queryAll(MenuQueryDTO criteria);

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    MenuDTO findById(long id);

    /**
     * 创建
     *
     * @param resources /
     * @return /
     */
    MenuDTO create(MenuCreateDTO resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(MenuUpdateDTO resources);

    /**
     * 获取待删除的菜单
     *
     * @param menuList /
     * @param menuSet  /
     * @return /
     */
    Set<Menu> getDeleteMenus(List<Menu> menuList, Set<Menu> menuSet);

    /**
     * 获取菜单树
     *
     * @param menus /
     * @return /
     */
    Object getMenuTree(List<Menu> menus);

    /**
     * 根据pid查询
     *
     * @param pid /
     * @return /
     */
    List<Menu> findByPid(long pid);

    /**
     * 构建菜单树
     *
     * @param menuDtos 原始数据
     * @return /
     */
    Map<String, Object> buildTree(List<MenuDTO> menuDtos);

    /**
     * 根据角色查询
     *
     * @param roles /
     * @return /
     */
    List<MenuDTO> findByRoles(List<RoleSmallDTO> roles);

    /**
     * 构建菜单树
     *
     * @param menuDtos /
     * @return /
     */
    Object buildMenus(List<MenuDTO> menuDtos);

    /**
     * 根据ID查询
     *
     * @param id /
     * @return /
     */
    Menu findOne(Long id);

    /**
     * 删除
     *
     * @param menuSet /
     */
    void delete(Set<Menu> menuSet);

    /**
     * 导出
     *
     * @param queryAll 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<MenuDTO> queryAll, HttpServletResponse response) throws IOException;
}
