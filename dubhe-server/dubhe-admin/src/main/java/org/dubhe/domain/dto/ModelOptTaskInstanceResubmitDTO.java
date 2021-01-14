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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description 重新提交任务实例参数
 * @date 2020-05-22
 */
@Data
public class ModelOptTaskInstanceResubmitDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", required = true)
    @NotNull(message = "实例id不能为空")
    private Long id;
}
