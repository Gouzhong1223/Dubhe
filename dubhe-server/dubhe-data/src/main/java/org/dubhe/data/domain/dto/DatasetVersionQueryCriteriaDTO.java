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

package org.dubhe.data.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.dubhe.annotation.Query;
import org.dubhe.base.PageQueryBase;
import org.dubhe.constant.NumberConstant;
import org.dubhe.constant.SymbolConstant;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description 数据集版本查询
 * @date 2020-05-25
 */
@Data
public class DatasetVersionQueryCriteriaDTO extends PageQueryBase implements Serializable{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("数据集ID")
    @Query(propName = "dataset_id", type = Query.Type.EQ)
    @NotNull(message = "数据集ID不能为空")
    @Min(value = NumberConstant.NUMBER_0, message = "数据集ID不能小于0")
    private Long datasetId;

    @Query(propName = "deleted", type = Query.Type.EQ)
    @ApiModelProperty(hidden = true,value = SymbolConstant.ZERO)
    private int deleted;

}
