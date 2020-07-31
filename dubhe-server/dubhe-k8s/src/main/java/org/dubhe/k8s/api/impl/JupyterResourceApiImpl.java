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

package org.dubhe.k8s.api.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.api.model.extensions.IngressBuilder;
import io.fabric8.kubernetes.api.model.extensions.IngressList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.dubhe.base.MagicNumConstant;
import org.dubhe.constant.SymbolConstant;
import org.dubhe.enums.LogEnum;
import org.dubhe.k8s.api.JupyterResourceApi;
import org.dubhe.k8s.api.NodeApi;
import org.dubhe.k8s.api.PersistentVolumeClaimApi;
import org.dubhe.k8s.constant.K8sLabelConstants;
import org.dubhe.k8s.constant.K8sParamConstants;
import org.dubhe.k8s.domain.PtBaseResult;
import org.dubhe.k8s.domain.bo.PtJupyterResourceBO;
import org.dubhe.k8s.domain.bo.PtPersistentVolumeClaimBO;
import org.dubhe.k8s.domain.resource.BizPersistentVolumeClaim;
import org.dubhe.k8s.domain.vo.PtJupyterDeployVO;
import org.dubhe.k8s.enums.ImagePullPolicyEnum;
import org.dubhe.k8s.enums.K8sKindEnum;
import org.dubhe.k8s.enums.K8sResponseEnum;
import org.dubhe.k8s.enums.LackOfResourcesEnum;
import org.dubhe.k8s.utils.K8sUtils;
import org.dubhe.k8s.utils.LabelUtils;
import org.dubhe.k8s.utils.YamlUtils;
import org.dubhe.utils.LogUtil;
import org.dubhe.utils.NfsUtil;
import org.dubhe.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @description JupyterResourceApi 实现类
 * @date 2020-04-17
 */
public class JupyterResourceApiImpl implements JupyterResourceApi {

    private K8sUtils k8sUtils;
    private KubernetesClient client;
    @Autowired
    private PersistentVolumeClaimApi persistentVolumeClaimApi;
    @Autowired
    private NfsUtil nfsUtil;
    @Autowired
    private NodeApi nodeApi;

    private static final String DATASET = "/dataset";
    private static final String WORKSPACE = "/workspace";

    private static final String PVC_DATASET = "pvc-dataset";
    private static final String PVC_WORKSPACE = "pvc-workspace";

    private static final String CONTAINER_NAME = "web";
    private static final Integer CONTAINER_PORT = 8888;
    private static final Integer SVC_PORT = 32680;
    private static final String NOTEBOOK_MAX_UPLOAD_SIZE = "100m";

    public JupyterResourceApiImpl(K8sUtils k8sUtils) {
        this.k8sUtils = k8sUtils;
        this.client = k8sUtils.getClient();
    }

    /**
     * 创建Notebook
     *
     * @param bo 模型管理 Notebook BO
     * @return PtJupyterDeployVO  Notebook 结果类
     */
    @Override
    public PtJupyterDeployVO create(PtJupyterResourceBO bo) {
        try {
            LackOfResourcesEnum lack = nodeApi.isAllocatable(bo.getCpuNum(), bo.getMemNum(), bo.getGpuNum());
            if (!LackOfResourcesEnum.ADEQUATE.equals(lack)) {
                return new PtJupyterDeployVO().error(K8sResponseEnum.LACK_OF_RESOURCES.getCode(), lack.getMessage());
            }
            LogUtil.info(LogEnum.BIZ_K8S, "Param of creating Notebook--create:{}", bo);
            if (null == bo) {
                return new PtJupyterDeployVO().error(K8sResponseEnum.BAD_REQUEST.getCode(), K8sResponseEnum.BAD_REQUEST.getMessage());
            }
            if (!nfsUtil.createDirs(true, bo.getWorkspaceDir(), bo.getDatasetDir())) {
                return new PtJupyterDeployVO().error(K8sResponseEnum.INTERNAL_SERVER_ERROR.getCode(), K8sResponseEnum.INTERNAL_SERVER_ERROR.getMessage());
            }
            PtJupyterDeployVO result = new JupyterDeployer(bo).buildNfsVolumes().deploy();
            LogUtil.info(LogEnum.BIZ_K8S, "Return value of creating Notebook create:{}", result);
            return result;
        } catch (KubernetesClientException e) {
            LogUtil.error(LogEnum.BIZ_K8S, "JupyterResourceApiImpl.create error, param:{} error:", bo, e);
            return new PtJupyterDeployVO().error(String.valueOf(e.getCode()), e.getMessage());
        }
    }

    /**
     * 挂载存储创建 Notebook
     *
     * @param bo 模型管理 Notebook BO
     * @return PtJupyterDeployVO Notebook 结果类
     */
    @Override
    public PtJupyterDeployVO createWithPvc(PtJupyterResourceBO bo) {
        try {
            LackOfResourcesEnum lack = nodeApi.isAllocatable(bo.getCpuNum(), bo.getMemNum(), bo.getGpuNum());
            if (!LackOfResourcesEnum.ADEQUATE.equals(lack)) {
                return new PtJupyterDeployVO().error(K8sResponseEnum.LACK_OF_RESOURCES.getCode(), lack.getMessage());
            }
            LogUtil.info(LogEnum.BIZ_K8S, "Param of creating Notebook--createWithPvc:{}", bo);
            if (null == bo) {
                return new PtJupyterDeployVO().error(K8sResponseEnum.BAD_REQUEST.getCode(), K8sResponseEnum.BAD_REQUEST.getMessage());
            }
            if (!nfsUtil.createDirs(true, bo.getWorkspaceDir(), bo.getDatasetDir())) {
                return new PtJupyterDeployVO().error(K8sResponseEnum.INTERNAL_SERVER_ERROR.getCode(), K8sResponseEnum.INTERNAL_SERVER_ERROR.getMessage());
            }
            BizPersistentVolumeClaim bizPersistentVolumeClaim = (StringUtils.isEmpty(bo.getWorkspaceDir())) ? persistentVolumeClaimApi.createDynamicNfs(new PtPersistentVolumeClaimBO(bo)) : persistentVolumeClaimApi.createWithNfsPv(new PtPersistentVolumeClaimBO(bo));
            if (K8sResponseEnum.SUCCESS.getCode().equals(bizPersistentVolumeClaim.getCode())) {
                bo.setWorkspacePvcName(bizPersistentVolumeClaim.getName());
                PtJupyterDeployVO result = new JupyterDeployer(bo).buildNfsPvcVolumes().deploy();
                LogUtil.info(LogEnum.BIZ_K8S, "Return value of creating Notebook--createWithPvc:{}", result);
                return result;
            } else {
                LogUtil.info(LogEnum.BIZ_K8S, "Notebook--createWithPvc error:", bizPersistentVolumeClaim.getMessage());
                return new PtJupyterDeployVO().error(bizPersistentVolumeClaim.getCode(), bizPersistentVolumeClaim.getMessage());
            }
        } catch (KubernetesClientException e) {
            LogUtil.error(LogEnum.BIZ_K8S, "JupyterResourceApiImpl.createWithPvc error, param:{} error:", bo, e);
            return new PtJupyterDeployVO().error(String.valueOf(e.getCode()), e.getMessage());
        }
    }

    /**
     * 删除 Notebook
     *
     * @param namespace 命名空间
     * @param resourceName 资源名称
     * @return PtBaseResult 基础结果类
     */
    @Override
    public PtBaseResult delete(String namespace, String resourceName) {
        try {
            Boolean res = client.extensions().ingresses().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(resourceName)).delete()
                    && client.services().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(resourceName)).delete()
                    && client.apps().statefulSets().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(resourceName)).delete()
                    && client.secrets().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(resourceName)).delete();
            if (res) {
                return new PtBaseResult();
            } else {
                return K8sResponseEnum.REPEAT.toPtBaseResult();
            }
        } catch (KubernetesClientException e) {
            LogUtil.error(LogEnum.BIZ_K8S, "JupyterResourceApiImpl.delete error:", e);
            return new PtBaseResult(String.valueOf(e.getCode()), e.getMessage());
        }
    }

    /**
     * 查询命名空间下所有Notebook
     *
     * @param namespace 命名空间
     * @return List<PtJupyterDeployVO> Notebook 结果类集合
     */
    @Override
    public List<PtJupyterDeployVO> list(String namespace) {
        StatefulSetList list = client.apps().statefulSets().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName()).list();
        if (CollectionUtil.isEmpty(list.getItems())) {
            return Collections.EMPTY_LIST;
        }
        return list.getItems().stream().map(ss -> {
            Map<String, String> labels = ss.getMetadata().getLabels();
            String resourceName = labels.get(K8sLabelConstants.BASE_TAG_SOURCE);
            return get(namespace, resourceName);
        }).collect(toList());
    }

    /**
     * 查询Notebook
     *
     * @param namespace 命名空间
     * @param resourceName 资源名称
     * @return PtJupyterDeployVO Notebook 结果类
     */
    @Override
    public PtJupyterDeployVO get(String namespace, String resourceName) {
        try {
            IngressList ingressList = client.extensions().ingresses().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(resourceName)).list();
            Ingress ingress = CollectionUtil.isEmpty(ingressList.getItems()) ? null : ingressList.getItems().get(0);
            ServiceList svcList = client.services().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(resourceName)).list();
            Service svc = CollectionUtil.isEmpty(svcList.getItems()) ? null : svcList.getItems().get(0);
            StatefulSetList ssList = client.apps().statefulSets().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(resourceName)).list();
            StatefulSet statefulSet = CollectionUtil.isEmpty(ssList.getItems()) ? null : ssList.getItems().get(0);
            SecretList secretList = client.secrets().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(resourceName)).list();
            Secret secret = CollectionUtil.isEmpty(secretList.getItems()) ? null : secretList.getItems().get(0);
            return new PtJupyterDeployVO(secret, statefulSet, svc, ingress);
        } catch (KubernetesClientException e) {
            LogUtil.error(LogEnum.BIZ_K8S, "JupyterResourceApiImpl.get error:", e);
            return new PtJupyterDeployVO().error(String.valueOf(e.getCode()), e.getMessage());
        }
    }

    private class JupyterDeployer {
        private static final String SUB_RESOURCE_NAME_TEMPLATE = "{}-{}-{}";

        private String baseName;
        private String statefulSetName;
        private String secretName;
        private String svcName;
        private String ingressName;

        private String namespace;
        private String image;
        private String datasetDir;
        private String datasetMountPath;
        private String workspaceMountPath;
        private String workspaceDir;
        private Boolean useGpu;

        //数据集默认只读
        private boolean datasetReadOnly;
        private String workSpacePvcName;
        private String nfs;
        private String host;

        private Map<String, Quantity> resourcesLimitsMap;
        private Map<String, String> baseLabels;
        private Map<String, String> podLabels;

        private String defaultJupyterPwd;
        private String baseUrl;
        private String secondaryDomain;
        private String businessLabel;

        private List<VolumeMount> volumeMounts;
        private List<Volume> volumes;

        private JupyterDeployer(PtJupyterResourceBO bo) {
            this.baseName = bo.getName();
            this.statefulSetName = StrUtil.format(K8sParamConstants.RESOURCE_NAME_TEMPLATE, baseName, RandomUtil.randomString(MagicNumConstant.FIVE));
            this.namespace = bo.getNamespace();
            this.image = bo.getImage();
            this.datasetDir = bo.getDatasetDir();
            this.datasetMountPath = StringUtils.isEmpty(bo.getDatasetMountPath()) ? DATASET : bo.getDatasetMountPath();
            this.workspaceDir = bo.getWorkspaceDir();
            this.workspaceMountPath = StringUtils.isEmpty(bo.getWorkspaceMountPath()) ? WORKSPACE : bo.getWorkspaceMountPath();
            Optional.ofNullable(bo.getDatasetReadOnly()).ifPresent(v -> datasetReadOnly = v);
            this.workSpacePvcName = bo.getWorkspacePvcName();
            this.useGpu = bo.getUseGpu() == null ? false : bo.getUseGpu();
            if (bo.getUseGpu() != null && bo.getUseGpu() && null == bo.getGpuNum()) {
                bo.setGpuNum(0);
            }

            this.resourcesLimitsMap = Maps.newHashMap();
            Optional.ofNullable(bo.getCpuNum()).ifPresent(v -> resourcesLimitsMap.put(K8sParamConstants.QUANTITY_CPU_KEY, new Quantity(v.toString(), K8sParamConstants.CPU_UNIT)));
            Optional.ofNullable(bo.getGpuNum()).ifPresent(v -> resourcesLimitsMap.put(K8sParamConstants.GPU_RESOURCE_KEY, new Quantity(v.toString())));
            Optional.ofNullable(bo.getMemNum()).ifPresent(v -> resourcesLimitsMap.put(K8sParamConstants.QUANTITY_MEMORY_KEY, new Quantity(v.toString(), K8sParamConstants.MEM_UNIT)));

            this.nfs = k8sUtils.getNfs();
            this.host = k8sUtils.getHost();
            this.businessLabel = bo.getBusinessLabel();
            this.baseLabels = LabelUtils.getBaseLabels(baseName, businessLabel);
            this.podLabels = LabelUtils.getChildLabels(baseName, statefulSetName, K8sKindEnum.STATEFULSET.getKind(), businessLabel);
            //生成附属资源的名称
            generateResourceName();

            this.defaultJupyterPwd = RandomUtil.randomNumbers(MagicNumConstant.SIX);
            this.baseUrl = SymbolConstant.SLASH + RandomUtil.randomString(MagicNumConstant.SIX);
            this.secondaryDomain = RandomUtil.randomString(MagicNumConstant.SIX) + SymbolConstant.DOT;

            this.datasetReadOnly = true;
            this.volumeMounts = new ArrayList();
            this.volumes = new ArrayList();
        }

        /**
         * 部署Notebook
         *
         * @return PtJupyterDeployVO Notebook 结果类
         */
        public PtJupyterDeployVO deploy() {
            //部署secret
            Secret secret = deploySecret(Base64.encode(defaultJupyterPwd), Base64.encode(baseUrl));
            LogUtil.info(LogEnum.BIZ_K8S, YamlUtils.dumpAsYaml(secret));
            //部署statefulset
            StatefulSet statefulSet = deployStatefulSet();

            //部署svc
            Service service = deployService();

            //部署ingress
            Ingress ingress = deployIngress();

            return new PtJupyterDeployVO(secret, statefulSet, service, ingress);
        }

        /**
         * 部署secret
         *
         * @param base64Pwd
         * @param base64BaseUrl
         * @return Secret
         */
        private Secret deploySecret(String base64Pwd, String base64BaseUrl) {
            Secret secret = null;
            SecretList list = client.secrets().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(baseName)).list();
            if (CollectionUtil.isNotEmpty(list.getItems())) {
                secret = list.getItems().get(0);
                secretName = secret.getMetadata().getName();
                LogUtil.info(LogEnum.BIZ_K8S, "Skip creating secret, {} already exists", secretName);
                return secret;
            }
            secret = new SecretBuilder()
                    .withNewMetadata()
                        .withName(secretName)
                        .addToLabels(baseLabels)
                        .withNamespace(namespace)
                    .endMetadata()
                    .addToData(ImmutableMap.of(K8sParamConstants.SECRET_PWD_KEY, base64Pwd, K8sParamConstants.SECRET_URL_KEY, base64BaseUrl))
                    .build();
            LogUtil.info(LogEnum.BIZ_K8S, "Ready to deploy {}", secretName);
            secret = client.secrets().create(secret);
            LogUtil.info(LogEnum.BIZ_K8S, "{} deployed successfully", secretName);
            return secret;
        }

        /**
         * 构建VolumeMount
         */
        private void buildDatasetNfsVolume() {
            //挂载点
            if (StrUtil.isNotBlank(datasetDir)) {
                volumeMounts.add(new VolumeMountBuilder()
                        .withName(PVC_DATASET)
                        .withMountPath(datasetMountPath)
                        .withReadOnly(datasetReadOnly)
                        .build());
            }
            //存储
            if (StrUtil.isNotBlank(datasetDir)) {
                volumes.add(new VolumeBuilder()
                        .withName(PVC_DATASET)
                        .withNewNfs()
                            .withPath(datasetDir)
                            .withServer(nfs)
                        .endNfs()
                        .build());
            }
        }

        /**
         * 构建VolumeMount
         */
        private void buildWorkspaceNfsVolume() {
            if (StrUtil.isNotBlank(workspaceDir)) {
                volumeMounts.add(new VolumeMountBuilder()
                        .withName(PVC_WORKSPACE)
                        .withMountPath(workspaceMountPath)
                        .build());
            }
            if (StrUtil.isNotBlank(workspaceDir)) {
                volumes.add(new VolumeBuilder()
                        .withName(PVC_WORKSPACE)
                        .withNewNfs()
                            .withPath(workspaceDir)
                            .withServer(nfs)
                        .endNfs()
                        .build());
            }
        }

        /**
         * 构建VolumeMount
         */
        private void buildWorkspaceNfsPvcVolume() {
            //挂载点
            if (StrUtil.isNotBlank(workSpacePvcName)) {
                volumeMounts.add(new VolumeMountBuilder()
                        .withName(PVC_WORKSPACE)
                        .withMountPath(workspaceMountPath)
                        .build());
            }
            if (StrUtil.isNotBlank(workSpacePvcName)) {
                volumes.add(new VolumeBuilder()
                        .withName(PVC_WORKSPACE)
                        .withNewPersistentVolumeClaim(workSpacePvcName, false)
                        .build());
            }
        }

        /**
         * 挂载存储
         *
         * @return JupyterDeployer Notebook 部署类
         */
        private JupyterDeployer buildNfsVolumes() {
            buildDatasetNfsVolume();
            buildWorkspaceNfsVolume();
            return this;
        }

        /**
         * 按照存储资源声明挂载存储
         *
         * @return JupyterDeployer Notebook 部署类
         */
        private JupyterDeployer buildNfsPvcVolumes() {
            buildDatasetNfsVolume();
            buildWorkspaceNfsPvcVolume();
            return this;
        }

        /**
         * 部署statefulset
         *
         * @return StatefulSet
         */
        private StatefulSet deployStatefulSet() {
            StatefulSet statefulSet = null;
            StatefulSetList list = client.apps().statefulSets().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(baseName)).list();
            if (CollectionUtil.isNotEmpty(list.getItems())) {
                statefulSet = list.getItems().get(0);
                statefulSetName = statefulSet.getMetadata().getName();
                LogUtil.info(LogEnum.BIZ_K8S, "Skip creating statefulSet, {} already exists", statefulSetName);
                return statefulSet;
            }
            LabelSelector labelSelector = new LabelSelector();
            labelSelector.setMatchLabels(ImmutableMap.of(K8sLabelConstants.BASE_TAG_P_NAME, statefulSetName));
            //容器
            Container container = new Container();
            container.setName(statefulSetName);
            container.setImage(image);
            container.setImagePullPolicy(ImagePullPolicyEnum.IFNOTPRESENT.getPolicy());
            //端口映射
            container.setPorts(Arrays.asList(new ContainerPortBuilder()
                    .withContainerPort(CONTAINER_PORT)
                    .withName(CONTAINER_NAME).build()));
            container.setVolumeMounts(volumeMounts);

            //环境变量
            List<EnvVar> env = new ArrayList();
            env.add(new EnvVarBuilder().withName(K8sParamConstants.ENV_PWD_KEY)
                    .withNewValueFrom()
                        .withNewSecretKeyRef()
                            .withName(secretName)
                            .withKey(K8sParamConstants.SECRET_PWD_KEY)
                        .endSecretKeyRef()
                    .endValueFrom().build());
            env.add(new EnvVarBuilder().withName(K8sParamConstants.ENV_URL_KEY)
                    .withNewValueFrom()
                        .withNewSecretKeyRef()
                            .withName(secretName)
                            .withKey(K8sParamConstants.SECRET_URL_KEY)
                        .endSecretKeyRef()
                    .endValueFrom().build());

            container.setResources(new ResourceRequirementsBuilder()
                    .addToLimits(resourcesLimitsMap)
                    .build());
            Map<String, String> gpuLabel = new HashMap<>(2);
            if (useGpu) {
                gpuLabel.put(K8sLabelConstants.NODE_GPU_LABEL_KEY, K8sLabelConstants.NODE_GPU_LABEL_VALUE);
            }

            statefulSet = new StatefulSetBuilder()
                    .withNewMetadata()
                        .withName(statefulSetName)
                        .addToLabels(baseLabels)
                        .withNamespace(namespace)
                    .endMetadata()
                    .withNewSpec()
                        .withSelector(labelSelector)
                        .withServiceName(statefulSetName)
                        .withReplicas(1)
                        .withNewTemplate()
                            .withNewMetadata()
                                .withName(statefulSetName)
                                .addToLabels(podLabels)
                                .withNamespace(namespace)
                            .endMetadata()
                            .withNewSpec()
                                .addToNodeSelector(gpuLabel)
                                .withTerminationGracePeriodSeconds(MagicNumConstant.SIXTY_LONG)
                                .addToContainers(container)
                                .addToVolumes(volumes.toArray(new Volume[0]))
                            .endSpec()
                        .endTemplate()
                    .endSpec()
                    .build();
            LogUtil.info(LogEnum.BIZ_K8S, "Ready to deploy {}, yaml信息为{}", statefulSetName, YamlUtils.dumpAsYaml(statefulSet));
            statefulSet = client.apps().statefulSets().create(statefulSet);
            LogUtil.info(LogEnum.BIZ_K8S, "{} deployed successfully", statefulSetName);
            return statefulSet;
        }

        /**
         * 部署service
         *
         * @return Service
         */
        private Service deployService() {
            Service svc = null;
            ServiceList list = client.services().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(baseName)).list();
            if (CollectionUtil.isNotEmpty(list.getItems())) {
                svc = list.getItems().get(0);
                svcName = svc.getMetadata().getName();
                LogUtil.info(LogEnum.BIZ_K8S, "Skip creating service, {} already exists", svcName);
                return svc;
            }
            svc = new ServiceBuilder()
                    .withNewMetadata()
                        .withName(svcName)
                        .addToLabels(baseLabels)
                        .withNamespace(namespace)
                    .endMetadata()
                    .withNewSpec()
                        .addNewPort()
                            .withPort(SVC_PORT)
                            .withTargetPort(new IntOrString(CONTAINER_PORT))
                            .withName(CONTAINER_NAME)
                        .endPort()
                        .withClusterIP("None")
                        .withSelector(podLabels)
                    .endSpec()
                    .build();
            LogUtil.info(LogEnum.BIZ_K8S, "Ready to deploy {}, yaml信息为{}", svcName, YamlUtils.dumpAsYaml(svc));
            svc = client.services().create(svc);
            LogUtil.info(LogEnum.BIZ_K8S, "{} deployed successfully", svcName);
            return svc;
        }

        /**
         * 部署ingress
         *
         * @return Ingress
         */
        private Ingress deployIngress() {
            Ingress ingress = null;
            IngressList list = client.extensions().ingresses().inNamespace(namespace).withLabels(LabelUtils.withEnvResourceName(baseName)).list();
            if (CollectionUtil.isNotEmpty(list.getItems())) {
                ingress = list.getItems().get(0);
                ingressName = ingress.getMetadata().getName();
                LogUtil.info(LogEnum.BIZ_K8S, "Skip creating ingress, {} already exists", ingressName);
                return ingress;
            }
            ingress = new IngressBuilder()
                    .withNewMetadata()
                        .withName(ingressName)
                        .addToLabels(baseLabels)
                        .withNamespace(namespace)
                        .addToAnnotations(K8sParamConstants.INGRESS_PROXY_BODY_SIZE_KEY, NOTEBOOK_MAX_UPLOAD_SIZE)
                    .endMetadata()
                    .withNewSpec()
                        .addNewRule()
                            .withHost(secondaryDomain + host)
                            .withNewHttp()
                                .withPaths()
                                .addNewPath()
                                    .withNewPath(SymbolConstant.SLASH)
                                    .withNewBackend()
                                        .withServiceName(svcName)
                                        .withServicePort(new IntOrString(SVC_PORT))
                                    .endBackend()
                                .endPath()
                            .endHttp()
                        .endRule()
                    .endSpec()
                    .build();
            LogUtil.info(LogEnum.BIZ_K8S, "Ready to deploy {}, yaml信息为{}", ingressName, YamlUtils.dumpAsYaml(ingress));
            ingress = client.extensions().ingresses().create(ingress);
            LogUtil.info(LogEnum.BIZ_K8S, "{} deployed successfully", ingressName);
            return ingress;
        }

        /**
         * 生成资源名
         */
        private void generateResourceName() {
            this.secretName = StrUtil.format(SUB_RESOURCE_NAME_TEMPLATE, baseName, SymbolConstant.TOKEN, RandomUtil.randomString(MagicNumConstant.FIVE));
            this.ingressName = StrUtil.format(SUB_RESOURCE_NAME_TEMPLATE, baseName, K8sParamConstants.INGRESS_SUFFIX, RandomUtil.randomString(MagicNumConstant.FIVE));
            this.svcName = StrUtil.format(SUB_RESOURCE_NAME_TEMPLATE, baseName, K8sParamConstants.SVC_SUFFIX, RandomUtil.randomString(MagicNumConstant.FIVE));
        }
    }
}
