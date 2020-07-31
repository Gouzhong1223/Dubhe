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

package org.dubhe.k8s.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description Node Metrics Result
 * @date 2020-05-22
 */
@Data
@Accessors(chain = true)
public class PtNodeMetricsVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 节点名称
     **/
    private String nodeName;
    /**
     * 时间
     **/
    private String timestamp;
    /**
     * cpu用量
     **/
    private String cpuUsageAmount;
    /**
     * cpu用量 单位 1核=1000m,1m=1000000n
     **/
    private String cpuUsageFormat;
    /**
     * 内存用量
     **/
    private String memoryUsageAmount;
    /**
     * 内存用量单位
     **/
    private String memoryUsageFormat;

    public PtNodeMetricsVO(String nodeName, String timestamp, String cpuUsageAmount, String cpuUsageFormat, String memoryUsageAmount, String memoryUsageFormat) {
        this.nodeName = nodeName;
        this.timestamp = timestamp;
        this.cpuUsageAmount = cpuUsageAmount;
        this.cpuUsageFormat = cpuUsageFormat;
        this.memoryUsageAmount = memoryUsageAmount;
        this.memoryUsageFormat = memoryUsageFormat;
    }
}
