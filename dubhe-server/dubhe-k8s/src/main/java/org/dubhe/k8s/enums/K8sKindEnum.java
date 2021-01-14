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

package org.dubhe.k8s.enums;

/**
 * @description k8s资源类型枚举
 * @date 2020-07-01
 */
public enum K8sKindEnum {
    /**
     * Job
     */
    JOB("Job"),
    /**
     * Pod
     */
    POD("Pod"),
    /**
     * Deployment
     */
    DEPLOYMENT("Deployment"),
    /**
     * StatefulSet
     */
    STATEFULSET("StatefulSet"),
    /**
     * DistributeTrain
     */
    DISTRIBUTETRAIN("DistributeTrain"),
    /**
     * StorageClass
     */
    STORAGECLASS("StorageClass"),
    /**
     * PersistentVolumeClaim
     */
    PERSISTENTVOLUMECLAIM("PersistentVolumeClaim"),
    /**
     * Namespace
     */
    NAMESPACE("Namespace"),
    /**
     * PodMetrics
     */
    PODMETRICS("PodMetrics"),
    /**
     * 原生混合资源类型
     */
    MixedNativeResource("MixedNativeResource"),
    ;

    private String kind;

    K8sKindEnum(String kind) {
        this.kind = kind;
    }

    public String getKind(){
        return kind;
    }
}
