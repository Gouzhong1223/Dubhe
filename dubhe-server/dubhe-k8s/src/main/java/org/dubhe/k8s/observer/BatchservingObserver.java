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

package org.dubhe.k8s.observer;

import org.dubhe.enums.BizEnum;
import org.dubhe.enums.LogEnum;
import org.dubhe.k8s.api.DistributeTrainApi;
import org.dubhe.k8s.constant.K8sLabelConstants;
import org.dubhe.k8s.domain.resource.BizPod;
import org.dubhe.k8s.enums.PodPhaseEnum;
import org.dubhe.k8s.event.callback.PodCallback;
import org.dubhe.utils.LogUtil;
import org.dubhe.utils.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Observable;
import java.util.Observer;

/**
 * @description 观察者，处理批量服务部署pod变化
 * @date 2020-11-04
 */
@Component
public class BatchservingObserver implements Observer {
    @Autowired
    private DistributeTrainApi distributeTrainApi;

    public BatchservingObserver(PodCallback podCallback){
        podCallback.addObserver(this);
    }

    /**
     * Observer update
     * @param observable observable
     * @param arg 参数
     */
    @Override
    public void update(Observable observable, Object arg) {
        if (arg instanceof BizPod){
            BizPod pod = (BizPod)arg;
            boolean batchservingSucceedOrFailed = (PodPhaseEnum.FAILED.getPhase().equals(pod.getPhase()) || PodPhaseEnum.SUCCEEDED.getPhase().equals(pod.getPhase())) && BizEnum.BATCH_SERVING.getBizCode().equals(pod.getBusinessLabel()) && SpringContextHolder.getActiveProfile().equals(pod.getLabel(K8sLabelConstants.PLATFORM_RUNTIME_ENV));
            if (batchservingSucceedOrFailed){
                LogUtil.warn(LogEnum.BIZ_K8S,"delete succeed or failed batchserving resourceName {};phase {};podName {}",pod.getLabel(K8sLabelConstants.BASE_TAG_SOURCE),pod.getPhase(),pod.getName());
                distributeTrainApi.deleteByResourceName(pod.getNamespace(),pod.getLabel(K8sLabelConstants.BASE_TAG_SOURCE));
            }
        }
    }
}
