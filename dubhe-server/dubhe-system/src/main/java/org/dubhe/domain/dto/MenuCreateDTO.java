/**
 * Copyright 2020 Tianshu AI Platform. All Rights Reserved.
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

package org.dubhe.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @description 菜单新增 dto
 * @date 2020-06-29
 */
@Data
public class MenuCreateDTO implements Serializable {

    private static final long serialVersionUID = 3587665050240667198L;

    @NotBlank
    private String name;

    private Long sort = 999L;

    @Length(max = 255, message = "路由地址长度不能超过255")
    private String path;

    @Length(max = 255, message = "路径长度不能超过255")
    private String component;

    /**
     * 类型，目录、菜单、按钮
     */
    private Integer type;

    /**
     * 权限
     */
    @Length(max = 255, message = "权限长度不能超过255")
    private String permission;

    private String componentName;

    @Length(max = 255, message = "图标长度不能超过255")
    private String icon;

    /**
     * 布局类型
     */
    @Length(max = 255, message = "布局类型不能超过255")
    private String layout;

    private Boolean cache;

    private Boolean hidden;

    /**
     * 上级菜单ID
     */
    private Long pid;


    private Boolean deleted;


    public @interface Update {
    }

}
