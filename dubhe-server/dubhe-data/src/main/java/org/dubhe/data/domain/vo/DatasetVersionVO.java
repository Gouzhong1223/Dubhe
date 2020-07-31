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

package org.dubhe.data.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.dubhe.data.domain.entity.Dataset;
import org.dubhe.data.domain.entity.DatasetVersion;
import org.dubhe.domain.dto.UserSmallDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 数据集版本Vo
 * @date 2020-05-21
 */
@Data
public class DatasetVersionVO implements Serializable {

    @ApiModelProperty("数据集ID")
    private Long datasetId;
    @ApiModelProperty(value = "数据类型:0图片，1视频")
    private Integer dataType;
    @ApiModelProperty(value = "标注类型：2分类,1目标检测,5目标跟踪")
    private Integer annotateType;
    @ApiModelProperty("数据集名称")
    private String name;
    @ApiModelProperty("数据集版本名称")
    private String versionName;
    @ApiModelProperty("版本创建时间")
    private Date createTime;
    @ApiModelProperty("版本说明")
    private String versionNote;
    @ApiModelProperty("是否当前版本")
    private Boolean isCurrent;
    @ApiModelProperty("0:未标注，1:手动标注中，2:自动标注中，3:自动标注完成，4:标注完成")
    private Integer status;
    @ApiModelProperty("文件数量")
    private Integer fileCount;
    @ApiModelProperty("标注进度")
    private ProgressVO progressVO;
    @ApiModelProperty("版本信息存储url")
    private String versionUrl;
    @ApiModelProperty("二进制转换后文件url")
    private String versionOfRecordUrl;
    @ApiModelProperty("创建人")
    private UserSmallDTO createUser;
    @ApiModelProperty("更新人")
    private UserSmallDTO updateUser;
    @ApiModelProperty("转换状态")
    private Integer dataConversion;
    @ApiModelProperty("图片数量")
    private Integer imageCounts;

    public DatasetVersionVO() {
    }

    public DatasetVersionVO(DatasetVersion datasetVersion, Dataset dataset) {
        this.name = dataset.getName();
        this.versionName = datasetVersion.getVersionName();
        this.createTime = datasetVersion.getCreateTime();
        this.versionNote = datasetVersion.getVersionNote();
        this.isCurrent = datasetVersion.getVersionName().equals(dataset.getCurrentVersionName());
        this.status = datasetVersion.getDeleted() ? 1 : 0;
        this.datasetId = dataset.getId();
        this.annotateType = dataset.getAnnotateType();
        this.dataType = dataset.getDataType();
    }

}
