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

package org.dubhe.data.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.dubhe.base.MagicNumConstant;
import org.dubhe.data.constant.Constant;
import org.dubhe.data.constant.ErrorEnum;
import org.dubhe.data.domain.bo.DatasetFileBO;
import org.dubhe.data.domain.bo.EnhanceTaskSplitBO;
import org.dubhe.data.domain.dto.DatasetEnhanceFinishDTO;
import org.dubhe.data.domain.dto.DatasetEnhanceRequestDTO;
import org.dubhe.data.domain.dto.FileCreateDTO;
import org.dubhe.data.domain.entity.DatasetVersionFile;
import org.dubhe.data.domain.entity.File;
import org.dubhe.data.domain.entity.Task;
import org.dubhe.data.service.DatasetEnhanceService;
import org.dubhe.data.service.DatasetVersionFileService;
import org.dubhe.data.service.FileService;
import org.dubhe.data.service.TaskService;
import org.dubhe.data.util.FileUtil;
import org.dubhe.data.util.TaskUtils;
import org.dubhe.enums.LogEnum;
import org.dubhe.exception.BusinessException;
import org.dubhe.utils.LogUtil;
import org.dubhe.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

/**
 * @description 数据集增强
 * @date 2020-06-28
 */
@Service
public class DatasetEnhanceServiceImpl implements DatasetEnhanceService {

    static PriorityBlockingQueue<EnhanceTaskSplitBO> queue;
    private ConcurrentHashMap<String, EnhanceTaskSplitBO> enhancing;
    @Autowired
    private DatasetVersionFileService datasetVersionFileService;
    @Autowired
    private FileService fileService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TaskUtils taskUtils;
    @Autowired
    @Lazy
    private TaskService taskService;
    @Value("${data.annotation.task.splitSize:16}")
    private Integer taskSplitSize;
    @Value("${k8s.nfs-root-path}")
    private String nfs;

    @Autowired
    private FileUtil fileUtil;

    /**
     * 提交任务
     *
     * @param datasetVersionFiles      数据版本文件关系列表
     * @param task                     任务
     * @param datasetEnhanceRequestDTO 数据增强请求条件
     */
    @Override
    public void commitEnhanceTask(List<DatasetVersionFile> datasetVersionFiles, Task task, DatasetEnhanceRequestDTO datasetEnhanceRequestDTO) {
        Set<File> fileSet = fileService.get(datasetVersionFiles.stream().
                map(DatasetVersionFile::getFileId).collect(Collectors.toList()), task.getDatasetId());
        Map<Long, DatasetVersionFile> datasetVersionFilesMap = new HashMap<>(datasetVersionFiles.size());
        datasetVersionFiles.stream().forEach(datasetVersionFile -> {
            datasetVersionFilesMap.put(datasetVersionFile.getFileId(), datasetVersionFile);
        });
        List<EnhanceTaskSplitBO> tasks = new ArrayList<>();
        for (int i = MagicNumConstant.ZERO; i < datasetEnhanceRequestDTO.getTypes().size(); i++) {
            List<List<File>> files = CollectionUtil.split(fileSet, taskSplitSize);
            for (List<File> list : files) {
                EnhanceTaskSplitBO enhanceTaskSplitBO = new EnhanceTaskSplitBO(
                        task.getId(),
                        list,
                        datasetVersionFiles.get(MagicNumConstant.ZERO).getDatasetId(),
                        datasetVersionFiles.get(MagicNumConstant.ZERO).getVersionName(),
                        datasetVersionFilesMap,
                        datasetEnhanceRequestDTO.getTypes().get(i),
                        fileUtil);
                String uuid = IdUtil.simpleUUID();
                try {
                    Boolean imgProcessUnprocessed = taskUtils.zAdd("imgProcess_unprocessed", uuid, 10L);
                    if (imgProcessUnprocessed) {
                        redisUtils.set("imgProcess:" + uuid, enhanceTaskSplitBO);
                    }
                } catch (Exception e) {
                    LogUtil.error(LogEnum.BIZ_DATASET, "enhancingTask add fail. task:{} exception:{}", enhanceTaskSplitBO, e);
                }
            }
        }
    }

    /**
     * 获取增加完成任务
     *
     * @return
     */
    public boolean getEnhanceFinishedTask() {
        Object failedIdKey = redisUtils.lpop("imgProcess_failed");
        if (ObjectUtil.isNotNull(failedIdKey)) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(failedIdKey));
            String failedId = jsonObject.getString("processKey").replaceAll("\"", "");
            Object object = redisUtils.get("imgProcess:" + failedId);
            String enhanceTaskSplitBOString = JSON.toJSONString(object);
            EnhanceTaskSplitBO enhanceTaskSplitBO = JSON.parseObject(enhanceTaskSplitBOString, EnhanceTaskSplitBO.class);
            Integer fileNum = enhanceTaskSplitBO.getFileDtos().size();
            taskService.finishTaskFile(enhanceTaskSplitBO.getId(), fileNum);
            redisUtils.del("imgProcess:" + failedId);
        }
        Object object = redisUtils.lpop("imgProcess_finished");
        if (ObjectUtil.isNotNull(object)) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(object));
            String processKey = jsonObject.getString("processKey").replaceAll("\"", "");
            Object finishDetail = redisUtils.get("imgProcess:finished:" + processKey);
            DatasetEnhanceFinishDTO datasetEnhanceFinishDTO = JSON.parseObject(JSON.toJSONString(finishDetail), DatasetEnhanceFinishDTO.class);
            LogUtil.info(LogEnum.BIZ_DATASET, "start finish enhance task datasetEnhanceFinishDTO:{}", datasetEnhanceFinishDTO);
            enhanceFinish(datasetEnhanceFinishDTO);
        }
        return ObjectUtil.isNotNull(failedIdKey) || ObjectUtil.isNotNull(object);
    }

    /**
     * 增强任务完成
     *
     * @param datasetEnhanceFinishDTO 增强完成任务详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enhanceFinish(DatasetEnhanceFinishDTO datasetEnhanceFinishDTO) {
        Object object = redisUtils.get("imgProcess:" + datasetEnhanceFinishDTO.getId());
        EnhanceTaskSplitBO enhanceTaskSplitBO = JSON.parseObject(JSON.toJSONString(object), EnhanceTaskSplitBO.class);
        if (ObjectUtil.isNull(enhanceTaskSplitBO)) {
            throw new BusinessException(ErrorEnum.TASK_SPLIT_ABSENT);
        }
        Integer fileNum = enhanceTaskSplitBO.getFileDtos().size();
        LogUtil.info(LogEnum.BIZ_DATASET, "DatasetEnhanceServiceImpl enhance finish file count {}", fileNum);
        //写入图片到data_file表
        List<DatasetFileBO> datasetFileBOList = enhanceTaskSplitBO.getFileDtos();
        List<FileCreateDTO> fileDTOList = Lists.transform(datasetFileBOList, new Function<DatasetFileBO, FileCreateDTO>() {
            @Nullable
            @Override
            public FileCreateDTO apply(@Nullable DatasetFileBO datasetFileBO) {
                return new FileCreateDTO(enhanceTaskSplitBO.createEnhanceFilePath(datasetEnhanceFinishDTO.getSuffix(), datasetFileBO).replaceFirst(nfs, ""),
                        datasetFileBO.getFileId(), datasetFileBO.getAnnotationStatus(), enhanceTaskSplitBO.getType(), enhanceTaskSplitBO.getUserId(),
                        datasetFileBO.getWidth(), datasetFileBO.getHeight());
            }
        });
        Map<Long, Integer> fileStatus = new HashMap<>(fileDTOList.size());
        datasetFileBOList.stream().forEach(datasetFileBO -> {
            fileStatus.put(datasetFileBO.getFileId(), datasetFileBO.getAnnotationStatus());
        });
        List<File> files = fileService.saveFiles(enhanceTaskSplitBO.getDatasetId(), fileDTOList);
        LogUtil.info(LogEnum.BIZ_DATASET, "DatasetEnhanceServiceImpl enhance finish file save success {}", datasetEnhanceFinishDTO.getId());

        List<DatasetVersionFile> datasetVersionFileList = new ArrayList<>();
        files.forEach(file -> {
            datasetVersionFileList.add(new DatasetVersionFile(
                    enhanceTaskSplitBO.getDatasetId(),
                    enhanceTaskSplitBO.getVersionName(),
                    file.getId(),
                    fileStatus.get(file.getPid()),
                    file.getName(),
                    enhanceTaskSplitBO.getVersionName()==null? Constant.UNCHANGED:Constant.UNCHANGED));
        });
        //文件写入关系表
        datasetVersionFileService.insertList(datasetVersionFileList);
        LogUtil.info(LogEnum.BIZ_DATASET, "DatasetEnhanceServiceImpl enhance finish version file save success {}", datasetEnhanceFinishDTO.getId());
        taskService.finishTaskFile(enhanceTaskSplitBO.getId(), fileNum);
        redisUtils.del("imgProcess:finished:" + datasetEnhanceFinishDTO.getId());
        redisUtils.del("imgProcess:" + datasetEnhanceFinishDTO.getId());
    }

}
