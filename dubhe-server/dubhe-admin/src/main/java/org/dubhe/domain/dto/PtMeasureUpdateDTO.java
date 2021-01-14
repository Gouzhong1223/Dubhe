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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dubhe.utils.TrainUtil;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description 修改度量信息入参DTO
 * @date 2020-11-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PtMeasureUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "参数id,参数id不能小于1", required = true)
    @NotNull(message = "度量id不能为空")
    @Min(value = TrainUtil.NUMBER_ONE, message = "度量参数id不能小于1")
    private Long id;

    @ApiModelProperty(value = "度量名称", required = true)
    @NotBlank(message = "度量名称不能为空")
    private String name;

    @ApiModelProperty("度量描述(选填)")
    private String description;

    @ApiModelProperty(value = "度量文件路径", required = true)
    @NotBlank(message = "度量文件路径不能为空")
    private String url;


}
