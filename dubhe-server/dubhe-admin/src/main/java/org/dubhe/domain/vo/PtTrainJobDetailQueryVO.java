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

package org.dubhe.domain.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @description: 根据jobId查询训练任务详情返回结果
 * @date: 2020-06-12
 */
@Data
@Accessors(chain = true)
public class PtTrainJobDetailQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("jobID")
    private Long id;

    @ApiModelProperty("训练作业ID")
    private Long trainId;

    @ApiModelProperty("训练作业job版本")
    private String trainVersion;

    @ApiModelProperty("训练作业job父版本")
    private String parentTrainVersion;

    @ApiModelProperty("训练作业jobName")
    private String jobName;

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("数据集名称")
    private String dataSourceName;

    @ApiModelProperty("数据集路径")
    private String dataSourcePath;

    @ApiModelProperty("训练时长")
    private String runtime;

    @ApiModelProperty("训练输出位置")
    private String outPath;

    @ApiModelProperty("日志输出路径")
    private String logPath;

    @ApiModelProperty("可视化日志路径")
    private String visualizedLogPath;

    @ApiModelProperty("规格ID")
    private Integer trainJobSpecsId;

    @ApiModelProperty("类型(0为CPU，1为GPU)")
    private Integer resourcesPoolType;

    @ApiModelProperty("节点个数")
    private Integer resourcesPoolNode;

    @ApiModelProperty("训练作业job状态, 0为待处理，1为运行中，2为运行完成，3为失败，4为停止，5为未知，6为删除，7为创建失败")
    private Integer trainStatus;

    @ApiModelProperty("创建时间")
    private Timestamp createTime;

    @ApiModelProperty("算法ID")
    private Long algorithmId;

    @ApiModelProperty("镜像名称")
    private String imageName;

    @ApiModelProperty(value = "镜像Project")
    private String imageNameProject;

    @ApiModelProperty(value = "镜像版本")
    private String imageTag;

    @ApiModelProperty("运行命令,输入长度不能超过128个字符")
    private String runCommand;

    @ApiModelProperty("运行参数(算法来源为我的算法时为调优参数，算法来源为预置算法时为运行参数)")
    private JSONObject runParams;

    @ApiModelProperty("F1值")
    private String paramF1;

    @ApiModelProperty("召回率")
    private String paramCallback;

    @ApiModelProperty("精确率")
    private String paramPrecise;

    @ApiModelProperty("准确率")
    private String paramAccuracy;

    @ApiModelProperty("算法名称")
    private String algorithmName;

    @ApiModelProperty("算法来源(1为我的算法，2为预置算法)")
    private Integer algorithmSource;

    @ApiModelProperty("算法用途")
    private String algorithmUsage;

    @ApiModelProperty("算法精度")
    private String accuracy;

    @ApiModelProperty("P4推理速度（ms）")
    private Integer p4InferenceSpeed;


}
