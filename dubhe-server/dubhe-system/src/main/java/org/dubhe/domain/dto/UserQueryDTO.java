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

package org.dubhe.domain.dto;

import lombok.Data;
import org.dubhe.annotation.Query;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * @Description 用户查询DTO
 * @Date 2020-06-01
 */

@Data
public class UserQueryDTO implements Serializable {

    private static final long serialVersionUID = -6863842533017963450L;
    @Query
    private Long id;

    @Query(blurry = "email,username,nick_name")
    private String blurry;

    @Query
    private Boolean enabled;

    @Query(propName = "deleted", type = Query.Type.EQ)
    private Boolean deleted = false;

    private Long roleId;


    @Query(propName = "create_time", type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}
