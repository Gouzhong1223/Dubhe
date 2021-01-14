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
package org.dubhe.data.machine.state.specific.data;

import org.dubhe.data.dao.DatasetMapper;
import org.dubhe.data.machine.enums.DataStateEnum;
import org.dubhe.data.machine.state.AbstractDataState;
import org.dubhe.data.machine.statemachine.DataStateMachine;
import org.dubhe.enums.LogEnum;
import org.dubhe.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @description 采样中状态类
 * @date 2020-08-27
 */
@Component
public class SamplingState extends AbstractDataState {

    @Autowired
    @Lazy
    private DataStateMachine dataStateMachine;

    @Autowired
    private DatasetMapper datasetMapper;

    /**
     * 数据集 采样中-->调用采集图片程序-->未标注
     *
     * @param primaryKeyId 业务ID
     */
    @Override
    public void samplingEvent(Integer primaryKeyId) {
        LogUtil.debug(LogEnum.STATE_MACHINE, " 【采样中】 执行事件前内存中状态机的状态 :{} ", dataStateMachine.getMemoryDataState());
        LogUtil.debug(LogEnum.STATE_MACHINE, " 接受参数： {} ", primaryKeyId);
        datasetMapper.updateStatus(Long.valueOf(primaryKeyId), DataStateEnum.NOT_ANNOTATION_STATE.getCode());
        dataStateMachine.setMemoryDataState(dataStateMachine.getNotAnnotationState());
        LogUtil.debug(LogEnum.STATE_MACHINE, " 【采样中】 执行事件后内存状态机的切换： {}", dataStateMachine.getMemoryDataState());
    }

    /**
     * 数据集 采样中-->调用采集图片程序-->采样失败
     *
     * @param primaryKeyId 业务ID
     */
    @Override
    public void samplingFailureEvent(Integer primaryKeyId) {
        LogUtil.debug(LogEnum.STATE_MACHINE, " 【采样中】 执行事件前内存中状态机的状态 :{} ", dataStateMachine.getMemoryDataState());
        LogUtil.debug(LogEnum.STATE_MACHINE, " 接受参数： {} ", primaryKeyId);
        datasetMapper.updateStatus(Long.valueOf(primaryKeyId), DataStateEnum.SAMPLED_FAILURE_STATE.getCode());
        dataStateMachine.setMemoryDataState(dataStateMachine.getSampledFailureState());
        LogUtil.debug(LogEnum.STATE_MACHINE, " 【采样中】 执行事件后内存状态机的切换： {}", dataStateMachine.getMemoryDataState());
    }

}