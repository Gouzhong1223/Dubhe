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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dubhe.annotation.Query;
import org.dubhe.base.PageQueryBase;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @description 模型优化任务查询条件
 * @date 2020-05-22
 */
@Data
@Accessors(chain = true)
public class ModelOptTaskQueryDTO extends PageQueryBase implements Serializable {
    @ApiModelProperty("任务id")
    @Query(propName = "id")
    private Long id;

    @Query(type = Query.Type.LIKE)
    @ApiModelProperty("任务名称或id")
    private String name;

    @ApiModelProperty("更新时间")
    @Query(type = Query.Type.BETWEEN, propName = "update_time")
    private List<Timestamp> updateTime;
}
