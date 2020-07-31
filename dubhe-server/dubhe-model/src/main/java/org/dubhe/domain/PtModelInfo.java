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

package org.dubhe.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.experimental.Accessors;
import org.dubhe.base.BaseEntity;

import javax.validation.constraints.*;

/**
 * @description 模型管理
 * @date 2020-03-24
 */

@Data
@Accessors(chain = true)
@TableName("pt_model_info")
public class PtModelInfo extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @NotNull(groups = {Update.class})
    private Long id;

    /**
     * 模型名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 框架类型(OneFlow、TensorFlow、Pytroch、Keras、Caffe、Blade)
     */
    @TableField(value = "frame_type")
    private Integer frameType;

    /**
     * 模型格式(SavedModel、Frozen Pb、Keras H5、Caffe Prototxt、ONNX、BladeModel、PMML)
     */
    @TableField(value = "model_format")
    private Integer modelType;

    /**
     * 模型描述
     */
    @TableField(value = "model_description")
    private String modelDescription;

    /**
     * 模型分类(目标检测、目标分类)
     */
    @TableField(value = "model_type")
    private String modelClassName;

    /**
     * 模型地址
     */
    @TableField(value = "url")
    private String modelAddress;

    /**
     * 模型版本
     */
    @TableField(value = "model_version")
    private String versionNum;

    /**
     * 组ID
     */
    @TableField(value = "team_id")
    private Integer teamId;

    /**
     * 模型是否为预置模型（0默认模型，1预置模型）
     */
    @TableField(value = "model_resource")
    private Integer modelResource;

    /**
     * 模型版本总的个数
     */
    @TableField(value = "total_num")
    private Integer totalNum;

    public void copy(PtModelInfo source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
