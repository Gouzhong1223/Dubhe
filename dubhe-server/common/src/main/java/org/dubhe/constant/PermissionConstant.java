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

package org.dubhe.constant;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @description: 权限常量
 * @since: 2020-05-25 14:39
 */
@Component
@Data
public class PermissionConstant {

    /**
     * 超级用户
     */
    public static final long ADMIN_USER_ID = 1L;
    public static final long ANONYMOUS_USER = -1L;
    public static final String SELECT = "select";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";

}
