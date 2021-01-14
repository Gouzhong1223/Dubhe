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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.dubhe.annotation.DataPermissionMethod;
import org.dubhe.base.MagicNumConstant;
import org.dubhe.constant.NumberConstant;
import org.dubhe.constant.SymbolConstant;
import org.dubhe.data.constant.*;
import org.dubhe.data.dao.FileMapper;
import org.dubhe.data.domain.bo.TaskSplitBO;
import org.dubhe.data.domain.dto.DatasetVersionFileDTO;
import org.dubhe.data.domain.dto.FileCreateDTO;
import org.dubhe.data.domain.entity.Dataset;
import org.dubhe.data.domain.entity.DatasetVersionFile;
import org.dubhe.data.domain.entity.File;
import org.dubhe.data.domain.entity.Task;
import org.dubhe.data.domain.vo.*;
import org.dubhe.data.machine.constant.DataStateMachineConstant;
import org.dubhe.data.machine.constant.FileStateCodeConstant;
import org.dubhe.data.machine.enums.FileStateEnum;
import org.dubhe.data.machine.utils.StateMachineUtil;
import org.dubhe.data.service.*;
import org.dubhe.data.service.store.IStoreService;
import org.dubhe.data.service.store.MinioStoreServiceImpl;
import org.dubhe.data.util.FileUtil;
import org.dubhe.data.util.TaskUtils;
import org.dubhe.dto.StateChangeDTO;
import org.dubhe.enums.DatasetTypeEnum;
import org.dubhe.enums.LogEnum;
import org.dubhe.exception.BusinessException;
import org.dubhe.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.dubhe.data.constant.Constant.ABSTRACT_NAME_PREFIX;


/**
 * @description 文件信息 服务实现类
 * @date 2020-04-10
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    /**
     * 单个标注任务数量
     */
    @Value("${data.annotation.task.splitSize:16}")
    private Integer taskSplitSize;

    /**
     * 默认标注页面文件列表分页大小
     */
    @Value("${data.file.pageSize:20}")
    private Integer defaultFilePageSize;

    /**
     * 路径名前缀
     */
    @Value("${k8s.nfs-root-path:/nfs/}")
    private String prefixPath;

    /**
     * minIO公钥
     */
    @Value("${minio.accessKey}")
    private String accessKey;

    /**
     * minIO私钥
     */
    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 加密字符串
     */
    @Value("${minio.url}")
    private String url;

    /**
     * 文件转换
     */
    @Autowired
    private FileConvert fileConvert;

    /**
     * 文件工具类
     */
    @Autowired
    private FileUtil fileUtil;

    /**
     * 任务类
     */
    @Autowired
    @Lazy
    private TaskService taskService;

    /**
     * 文件存储服务实现类
     */
    @Resource(type = MinioStoreServiceImpl.class)
    private IStoreService storeService;

    /**
     * 数据集服务实现类
     */
    @Resource
    @Lazy
    private DatasetService datasetService;

    /**
     * 数据集版本文件服务实现类
     */
    @Resource
    @Lazy
    private DatasetVersionFileService datasetVersionFileService;

    @Autowired
    private TaskUtils taskUtils;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 数据集版本文件服务实现类
     */
    @Resource
    @Lazy
    private FileMapper fileMapper;

    @Resource
    private LabelService labelService;

    private static final String SAMPLE_FINISHED_QUEUE_NAME = "videoSample_finished";

    private static final String SAMPLE_FAILED_QUEUE_NAME = "videoSample_failed";

    private static final String START_SAMPLE_QUEUE = "videoSample_processing";

    private static final String SAMPLE_PENDING_QUEUE = "videoSample_unprocessed";

    private static final String DETAIL_NAME = "videoSample_pictures:";

    @Autowired
    private GeneratorKeyUtil generatorKeyUtil;


    /**
     * 文件详情
     *
     * @param fileId 文件ID
     * @return FileVO 文件信息
     */
    @Override
    @DataPermissionMethod(dataType = DatasetTypeEnum.PUBLIC)
    public FileVO get(Long fileId, Long datasetId) {
        File file = fileMapper.selectFile(fileId, datasetId);
        Dataset dataset = datasetService.getOneById(datasetId);
        DatasetVersionFile datasetVersionFile = datasetVersionFileService.getDatasetVersionFile(datasetId, dataset.getCurrentVersionName(), fileId);
        if (file == null) {
            return null;
        }
        return fileConvert.toDto(file,
                getAnnotation(file.getDatasetId(), file.getName(), datasetVersionFile.getVersionName(), datasetVersionFile.getChanged() == NumberConstant.NUMBER_0));
    }

    /**
     * 获取标注信息
     *
     * @param datasetId 数据集ID
     * @param fileName  文件名
     * @return String
     */
    public String getAnnotation(Long datasetId, String fileName, String versionName, boolean change) {
        String path = fileUtil.getReadAnnotationAbsPath(datasetId, fileName, versionName, change);
        return storeService.read(path);
    }

    /**
     * 判断视频数据集是否已存在视频
     *
     * @param datasetId 数据集ID
     */
    @Override
    public void isExistVideo(Long datasetId) {
        QueryWrapper<File> fileQueryWrapper = new QueryWrapper<>();
        fileQueryWrapper.lambda().eq(File::getDatasetId, datasetId);
        if (getBaseMapper().selectCount(fileQueryWrapper) > MagicNumConstant.ZERO) {
            throw new BusinessException(ErrorEnum.VIDEO_EXIST);
        }
    }

    /**
     * 删除文件
     *
     * @param datasetId 数据集ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long datasetId) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(File::getDatasetId, datasetId);
        remove(queryWrapper);
    }

    /**
     * 数据集标注进度
     *
     * @param datasets 数据集
     * @return Map<Long, ProgressVO> 数据集标注进度map
     */
    @Override
    public Map<Long, ProgressVO> listStatistics(List<Dataset> datasets) {
        if (CollectionUtils.isEmpty(datasets)) {
            return Collections.emptyMap();
        }
        Map<Long, ProgressVO> res = new HashMap<>(datasets.size());

        // 封装数据集版本数据
        datasets.forEach(dataset -> {
            Map<Integer, Integer> fileStatus = datasetVersionFileService.getDatasetVersionFileCount(dataset.getId(), dataset.getCurrentVersionName());
            ProgressVO progressVO = ProgressVO.builder().build();
            if (fileStatus != null) {
                for (Map.Entry<Integer, Integer> entry : fileStatus.entrySet()) {
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(entry.getValue()));
                    if (entry.getKey().equals(FileStateCodeConstant.NOT_ANNOTATION_FILE_STATE) || entry.getKey().equals(FileStateCodeConstant.MANUAL_ANNOTATION_FILE_STATE)) {
                        progressVO.setUnfinished(progressVO.getUnfinished() + jsonObject.getInteger("count"));
                    } else if (entry.getKey().equals(FileStateCodeConstant.AUTO_TAG_COMPLETE_FILE_STATE)) {
                        progressVO.setAutoFinished(progressVO.getAutoFinished() + jsonObject.getInteger("count"));
                    } else if (entry.getKey().equals(FileStateCodeConstant.ANNOTATION_COMPLETE_FILE_STATE)) {
                        progressVO.setFinished(progressVO.getFinished() + jsonObject.getInteger("count"));
                    } else if (entry.getKey().equals(FileStateCodeConstant.TARGET_COMPLETE_FILE_STATE)) {
                        progressVO.setFinishAutoTrack(progressVO.getFinishAutoTrack() + jsonObject.getInteger("count"));
                    } else if (entry.getKey().equals(FileStateCodeConstant.ANNOTATION_NOT_DISTINGUISH_FILE_STATE)) {
                        progressVO.setAnnotationNotDistinguishFile(progressVO.getAnnotationNotDistinguishFile() + jsonObject.getInteger("count"));
                    }
                }
            }
            res.put(dataset.getId(), progressVO);
        });
        return res;
    }

    /**
     * 判断是否存在手动标注中的数据集
     *
     * @param id 数据集ID
     * @return boolean 判断是否存在手动标注中的数据集
     */
    @Override
    public boolean hasManualAnnotating(Long id) {
        QueryWrapper<File> fileQueryWrapper = new QueryWrapper<>();
        //状态等于标注中,排除视频文件
        fileQueryWrapper.lambda().eq(File::getDatasetId, id).eq(File::getStatus, FileStateCodeConstant.MANUAL_ANNOTATION_FILE_STATE).ne(File::getFileType, DatatypeEnum.VIDEO.getValue());
        return getBaseMapper().selectCount(fileQueryWrapper) > MagicNumConstant.ZERO;
    }

    /**
     * 将整体任务分割
     *
     * @param files 文件集合
     * @param task  任务
     * @return List<TaskSplitBO> 任务集合
     */
    @Override
    public List<TaskSplitBO> split(Collection<File> files, Task task) {
        if (CollectionUtils.isEmpty(files)) {
            return new LinkedList<>();
        }
        LogUtil.info(LogEnum.BIZ_DATASET, "split file. file size:{}", files.size());
        Map<Long, List<File>> groupedFiles = files.stream().collect(Collectors.groupingBy(File::getDatasetId));
        List<TaskSplitBO> ts = groupedFiles.values().stream()
                .flatMap(fs -> CollectionUtil.split(fs, taskSplitSize).stream())
                .map(fs -> TaskSplitBO.from(fs, task)).filter(Objects::nonNull).collect(Collectors.toList());
        LogUtil.info(LogEnum.BIZ_DATASET, "split result. split size:{}", ts.size());
        return ts;
    }

    /**
     * 执行文件更新
     *
     * @param ids            文件ID
     * @param fileStatusEnum 文件状态
     * @return int 更新结果
     */
    public int doUpdate(Collection<Long> ids, FileStateEnum fileStatusEnum) {
        if (CollectionUtils.isEmpty(ids)) {
            return MagicNumConstant.ZERO;
        }
        File newObj = File.builder().status(fileStatusEnum.getCode()).build();
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(File::getId, ids);
        return baseMapper.update(newObj, queryWrapper);
    }

    /**
     * 更新文件状态
     *
     * @param files          文件集合
     * @param fileStatusEnum 文件状态
     * @return int 更新结果
     */
    @Override
    public int update(Collection<File> files, FileStateEnum fileStatusEnum) {
        Collection<Long> ids = toIds(files);
        if (CollectionUtils.isEmpty(files)) {
            return MagicNumConstant.ZERO;
        }
        int count = doUpdate(ids, fileStatusEnum);
        if (count == MagicNumConstant.ZERO) {
            throw new BusinessException(ErrorEnum.DATA_ABSENT_OR_NO_AUTH);
        }
        return count;
    }

    /**
     * 文件完成自动标注
     *
     * @param files file文件
     * @return boolean 自动标注结果
     */
    @Override
    public boolean finishAnnotation(Dataset dataset, Set<Long> files) {
        int count = datasetVersionFileService.updateAnnotationStatus(dataset.getId(), dataset.getCurrentVersionName(),
                files, FileStateCodeConstant.NOT_ANNOTATION_FILE_STATE, FileStateCodeConstant.AUTO_TAG_COMPLETE_FILE_STATE);
        return count > MagicNumConstant.ZERO;
    }

    /**
     * 通过文件获取ID
     *
     * @param files file文件
     * @return Collection<Long> 文件ID
     */
    private Collection<Long> toIds(Collection<File> files) {
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptySet();
        }
        return files.stream().map(File::getId).collect(Collectors.toSet());
    }

    /**
     * 更新文件
     *
     * @param fileId         文件ID
     * @param fileStatusEnum 文件状态
     * @return int 更行结果
     */
    public int update(Long fileId, FileStateEnum fileStatusEnum) {
        File newObj = File.builder()
                .id(fileId)
                .status(fileStatusEnum.getCode())
                .build();
        return baseMapper.updateById(newObj);
    }

    /**
     * 更新文件
     *
     * @param fileId         文件ID
     * @param fileStatusEnum 文件状态
     * @param originStatus   文件状态
     * @return boolean 更行结果
     */
    public boolean update(Long fileId, FileStateEnum fileStatusEnum, FileStateEnum originStatus) {
        if (getById(fileId) == null) {
            return true;
        }
        UpdateWrapper<File> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(File::getId, fileId).eq(File::getStatus, originStatus.getCode())
                .set(File::getId, fileId).set(File::getStatus, fileStatusEnum.getCode());
        return update(updateWrapper);
    }

    /**
     * 保存文件
     *
     * @param fileId 文件ID
     * @param files  file文件
     * @return List<Long> 保存的文件id集合
     */
    @Override
    public List<File> saveFiles(Long fileId, List<FileCreateDTO> files) {
        Map<String, String> fail = new HashMap<>(files.size());
        List<File> newFiles = new ArrayList<>();
        Long datasetUserId = datasetService.getOneById(fileId).getCreateUserId();
        Long userId = null;
        if (JwtUtils.getCurrentUserDto() != null) {
            userId = JwtUtils.getCurrentUserDto().getId();
        }
        files.stream().map(file -> FileCreateDTO.toFile(file, fileId, datasetUserId)).forEach(f -> {
            try {
                newFiles.add(f);
            } catch (DuplicateKeyException e) {
                fail.put(f.getName(), "the file already exists");
            }
        });
        if (!CollectionUtils.isEmpty(fail)) {
            throw new BusinessException(ErrorEnum.FILE_EXIST, JSON.toJSONString(fail), null);
        }
        Queue<Long> dataFileIds = generatorKeyUtil.getSequenceByBusinessCode(Constant.DATA_FILE, newFiles.size());
        for (File f : newFiles) {
            f.setId(dataFileIds.poll());
        }
        baseMapper.saveList(newFiles, userId, datasetUserId);
        return newFiles;
    }

    /**
     * 保存视频文件
     *
     * @param fileId 视频文件ID
     * @param files  file文件
     * @param type   文件类型
     * @param pid    文件父ID
     * @param userId 用户ID
     */
    @Override
    public void saveVideoFiles(Long fileId, List<FileCreateDTO> files, int type, Long pid, Long userId) {
        List<File> list = new ArrayList<>();
        Long createUserId = datasetService.getOneById(fileId).getCreateUserId();
        files.forEach(fileCreateDTO -> {
            File file = FileCreateDTO.toFile(fileCreateDTO, fileId, type, pid);
            list.add(file);
        });
        Queue<Long> dataFileIds = generatorKeyUtil.getSequenceByBusinessCode(Constant.DATA_FILE, list.size());
        for (File f : list) {
            f.setId(dataFileIds.poll());
        }
        baseMapper.saveList(list, userId, createUserId);
    }

    /**
     * 创建查询
     *
     * @param datasetId 数据集ID
     * @param status    状态
     * @return QueryWrapper<File> 查询条件
     */
    public QueryWrapper<File> buildQuery(Long datasetId, Set<Integer> status) {
        FileQueryCriteriaVO criteria = FileQueryCriteriaVO.builder()
                .datasetId(datasetId).order("id ASC").build();
        return WrapperHelp.getWrapper(criteria);
    }

    /**
     * 获取offset
     *
     * @param datasetId 数据集ID
     * @param fileId    文件ID
     * @param type      数据集类型
     * @return Integer 获取到offset
     */
    @Override
    public Integer getOffset(Long fileId, Long datasetId, Integer type) {
        Integer offset = datasetVersionFileService.getOffset(fileId, datasetId, type);
        return offset == MagicNumConstant.ZERO ? null : offset - MagicNumConstant.ONE;
    }


    /**
     * 文件查询，物体检测标注页面使用
     *
     * @param datasetId 数据集ID
     * @param offset    Offset
     * @param limit     页容量
     * @param page      分页条件
     * @param type      数据集类型
     * @return Page<File> 文件查询分页列表
     */
    @Override
    @DataPermissionMethod(dataType = DatasetTypeEnum.PUBLIC)
    public Page<File> listByLimit(Long datasetId, Long offset, Integer limit, Integer page, Integer type) {
        if (page == null) {
            page = MagicNumConstant.ONE;
        }
        if (offset == null) {
            offset = getDefaultOffset();
        }
        if (limit == null) {
            limit = defaultFilePageSize;
        }
        //查询数据集
        Dataset dataset = datasetService.getOneById(datasetId);
        //查询当前数据集下所有的文件(中间表)
        List<DatasetVersionFileDTO> datasetVersionFiles = datasetVersionFileService
                .getListByDatasetIdAndAnnotationStatus(dataset.getId(), dataset.getCurrentVersionName(), type, offset,
                        limit, "id", null, null);
        if (datasetVersionFiles == null || datasetVersionFiles.isEmpty()) {
            Page<File> filePage = new Page<>();
            filePage.setCurrent(page);
            filePage.setSize(limit);
            filePage.setTotal(NumberConstant.NUMBER_0);
            return filePage;
        }
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", datasetVersionFiles
                .stream()
                .map(DatasetVersionFileDTO::getFileId)
                .collect(Collectors.toSet())).eq("dataset_id", dataset.getId());
        List<File> files = baseMapper.selectList(queryWrapper);
        //将所有文件的状态放入
        files.forEach(v -> {
            datasetVersionFiles.forEach(d -> {
                if (v.getId().equals(d.getFileId())) {
                    v.setStatus(d.getAnnotationStatus());
                }
            });
        });
        //文件重排序（按照版本文件排序）
        List<File> fileArrayList = new ArrayList<>();
        datasetVersionFiles.forEach(v -> {
            files.forEach(f -> {
                if (v.getFileId().equals(f.getId())) {
                    fileArrayList.add(f);
                }
            });
        });
        Page<File> pages = new Page<>();
        pages.setTotal(
                datasetVersionFileService.selectCount(
                        new LambdaQueryWrapper<DatasetVersionFile>() {{
                            eq(DatasetVersionFile::getDatasetId, datasetId);
                            in(DatasetVersionFile::getStatus, DataStatusEnum.ADD.getValue(), DataStatusEnum.NORMAL.getValue());
                            if (type != null) {
                                in(DatasetVersionFile::getAnnotationStatus, FileTypeEnum.getStatus(type));
                            }
                            if (StringUtils.isBlank(dataset.getCurrentVersionName())) {
                                isNull(DatasetVersionFile::getVersionName);
                            } else {
                                eq(DatasetVersionFile::getVersionName, dataset.getCurrentVersionName());
                            }
                        }}
                )
        );
        pages.setRecords(fileArrayList);
        pages.setSize(limit);
        pages.setCurrent(page);
        return pages;
    }

    /**
     * 文件查询
     *
     * @param datasetId         数据集ID
     * @param page              分页条件
     * @param queryCriteria     查询条件
     * @return Map<String, Object> 文件查询列表
     */
    @Override
    @DataPermissionMethod(dataType = DatasetTypeEnum.PUBLIC)
    public Map<String, Object> listPage(Long datasetId, Page page, FileQueryCriteriaVO queryCriteria) {
        Dataset dataset = datasetService.getOneById(queryCriteria.getDatasetId());
        List<DatasetVersionFileDTO> datasetVersionFiles = commDatasetVersionFiles(datasetId, dataset.getCurrentVersionName(), page, queryCriteria);
        if (datasetVersionFiles == null || datasetVersionFiles.isEmpty()) {
            return buildPage(page);
        }
        List<File> files = getFileList(datasetVersionFiles, datasetId);
        //将所有文件的状态放入
        files.forEach(v -> {
            datasetVersionFiles.forEach(d -> {
                if (v.getId().equals(d.getFileId())) {
                    v.setStatus(d.getAnnotationStatus());
                }
            });
        });
        //文件重排序（按照版本文件排序）
        List<File> fileArrayList = new ArrayList<>();
        datasetVersionFiles.forEach(v -> {
            files.forEach(f -> {
                if (v.getFileId().equals(f.getId())) {
                    fileArrayList.add(f);
                }
            });
        });
        Map<Long, File> fileListMap = files.stream().collect(Collectors.toMap(File::getId, obj -> obj));
        List<FileVO> vos = datasetVersionFiles.stream().map(versionFile -> {
            FileVO fileVO = FileVO.builder().build();
            if (!Objects.isNull(fileListMap.get(versionFile.getFileId()))) {
                File file = fileListMap.get(versionFile.getFileId());
                BeanUtil.copyProperties(file, fileVO);
                fileVO.setLabelId(versionFile.getLabelId());
                fileVO.setPrediction(versionFile.getPrediction());
                fileVO.setAnnotation(getAnnotation(datasetId, file.getName(), versionFile.getVersionName(), versionFile.getChanged() == NumberConstant.NUMBER_0));
            }
            return fileVO;
        }).collect(Collectors.toList());
        Page<File> pages = buildPages(page, files, dataset, queryCriteria);
        return org.dubhe.utils.PageUtil.toPage(pages, vos);
    }

    /**
     * 获取首个文件
     *
     * @param datasetId 数据集id
     * @param type      数据集类型
     * @return Long 首个文件Id
     */
    @Override
    @DataPermissionMethod(dataType = DatasetTypeEnum.PUBLIC)
    public Long getFirst(Long datasetId, Integer type) {
        Dataset dataset = datasetService.getOneById(datasetId);
        DatasetVersionFile datasetVersionFile = datasetVersionFileService
                .getFirstByDatasetIdAndVersionNum(datasetId, dataset.getCurrentVersionName(), FileTypeEnum.getStatus(type));
        return datasetVersionFile == null ? null : datasetVersionFile.getFileId();
    }


    /**
     * 默认offset
     *
     * @return Long 默认offset
     */
    public Long getDefaultOffset() {
        return MagicNumConstant.ZERO_LONG;
    }

    /**
     * 如果ids为空，则返回空
     *
     * @param fileIds 文件id集合
     * @return Set<File> 文件集合
     */
    @Override
    public Set<File> get(List<Long> fileIds, Long datasetId) {
        if (CollectionUtils.isEmpty(fileIds)) {
            return new HashSet<>();
        }
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dataset_id", datasetId);
        queryWrapper.eq("id", fileIds.get(MagicNumConstant.ZERO));
        File fileOne = baseMapper.selectOne(queryWrapper);
        if (fileOne == null) {
            return new HashSet<>();
        }
        QueryWrapper<File> fileQueryWrapper = new QueryWrapper<>();
        fileQueryWrapper.eq("dataset_id", fileOne.getDatasetId());
        fileQueryWrapper.in("id", fileIds);
        return new HashSet(baseMapper.selectList(fileQueryWrapper));
    }

    /**
     * 视频采样任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void videoSample() {
        //处理失败任务，将数据集改为采样失败
        List<Object> failedIdKeys = redisUtils.lGet(SAMPLE_FAILED_QUEUE_NAME, 0, -1);
        failedIdKeys.forEach(failedIdKey -> {
            String failedId = JSON.parseObject(JSON.toJSONString(failedIdKey)).getString("datasetIdKey");
            try {
                videoSampleFailed(failedId);
            } catch (Exception exception) {
                LogUtil.error(LogEnum.BIZ_DATASET, "videoFailedTask exception:{}", exception);
            }
            taskUtils.finishedTask(SAMPLE_FAILED_QUEUE_NAME, JSON.parseObject(failedIdKey.toString()).toJSONString()
                    , DETAIL_NAME, failedId);
            //将任务改成失败
        });
        //处理已经完成的任务
        List<Object> datasetIdKeyObjects = redisUtils.lGet(SAMPLE_FINISHED_QUEUE_NAME, 0, -1);
        datasetIdKeyObjects.forEach(datasetIdKeyObject -> {
            String datasetIdKey = JSON.parseObject(JSON.toJSONString(datasetIdKeyObject)).getString("datasetIdKey");
            Long datasetId = Long.valueOf(StringUtils.substringBefore(String.valueOf(datasetIdKey), ":"));
            List<Object> picNameObjects = redisUtils.lGet(DETAIL_NAME + datasetIdKey, 0, -1);
            QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
            taskQueryWrapper.lambda().eq(Task::getDatasetId, datasetId)
                    .eq(Task::getType, MagicNumConstant.FIVE);
            Task task = taskService.selectOne(taskQueryWrapper);
            Integer segment = Integer.valueOf(StringUtils.substringAfter(String.valueOf(datasetIdKey), ":"));
            if (segment.equals(task.getFinished() + MagicNumConstant.ONE)) {
                try {
                    videSampleFinished(picNameObjects, task);
                } catch (Exception exception) {
                    LogUtil.error(LogEnum.BIZ_DATASET, "videoFinishedTask exception:{}", exception);
                }
                //从已完成队列中删除
                taskUtils.finishedTask(SAMPLE_FINISHED_QUEUE_NAME, JSON.toJSONString(datasetIdKeyObject)
                        , DETAIL_NAME, datasetIdKey);
            }
        });
    }

    /**
     * 采样失败任务处理
     *
     * @Param failedId 采样失败任务ID
     */
    public void videoSampleFailed(String failedId) {
        Long datasetId = Long.valueOf(StringUtils.substringBefore(String.valueOf(failedId), ":"));
        //创建入参请求体
        StateChangeDTO stateChangeDTO = new StateChangeDTO();
        //创建需要执行事件的方法的传入参数
        Object[] objects = new Object[1];
        objects[0] = datasetId.intValue();
        stateChangeDTO.setObjectParam(objects);
        //添加需要执行的状态机类
        stateChangeDTO.setStateMachineType(DataStateMachineConstant.DATA_STATE_MACHINE);
        //采样失败事件
        stateChangeDTO.setEventMethodName(DataStateMachineConstant.DATA_SAMPLING_FAILURE_EVENT);
        StateMachineUtil.stateChange(stateChangeDTO);
    }

    /**
     * 采样完成任务处理
     *
     * @Param picNameObjects 完成后图片名称
     * @Param task           采样任务
     */
    public void videSampleFinished(List<Object> picNameObjects, Task task) {
        List<String> picNames = new ArrayList<>();
        picNameObjects.forEach(picNameObject -> {
            String pictureName = JSON.parseObject(JSON.toJSONString(picNameObject)).getString("pictureName");
            picNames.add(pictureName);
        });
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(File::getDatasetId, task.getDatasetId()).eq(File::getFileType, MagicNumConstant.ONE);
        File file = getBaseMapper().selectOne(queryWrapper);
        saveVideoPic(picNames, file);
        task.setFinished(task.getFinished() + MagicNumConstant.ONE);
        taskService.updateByTaskId(task);
        //单个视频采样完成
        if (task.getTotal().equals(task.getFinished())) {
            file.setStatus(FileStateCodeConstant.AUTO_TAG_COMPLETE_FILE_STATE);
            getBaseMapper().updateFileStatus(file.getDatasetId(), file.getId(), file.getStatus());
            FileQueryCriteriaVO fileQueryCriteria = FileQueryCriteriaVO.builder()
                    .datasetId(file.getDatasetId())
                    .fileType(DatatypeEnum.IMAGE.getValue())
                    .build();
            List<File> files = list(WrapperHelp.getWrapper(fileQueryCriteria));
            List<DatasetVersionFile> list = new ArrayList<>();
            files.forEach(fileOne -> {
                DatasetVersionFile datasetVersionFile = new DatasetVersionFile(file.getDatasetId(), null, fileOne.getId(), fileOne.getName());
                list.add(datasetVersionFile);
            });
            if (MagicNumConstant.ZERO != list.size()) {
                datasetVersionFileService.insertList(list);
            }
            //创建入参请求体
            StateChangeDTO stateChangeDTO = new StateChangeDTO();
            //创建需要执行事件的方法的传入参数
            Object[] objects = new Object[1];
            objects[0] = file.getDatasetId().intValue();
            stateChangeDTO.setObjectParam(objects);
            //添加需要执行的状态机类
            stateChangeDTO.setStateMachineType(DataStateMachineConstant.DATA_STATE_MACHINE);
            //采样事件
            stateChangeDTO.setEventMethodName(DataStateMachineConstant.DATA_SAMPLING_EVENT);
            StateMachineUtil.stateChange(stateChangeDTO);
        }
    }

    /**
     * 保存采样后文件
     *
     * @param picNames 图片文件名字
     * @param file     视频文件
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoPic(List<String> picNames, File file) {
        Collections.reverse(picNames);
        List<FileCreateDTO> fileCreateDTOS = new ArrayList<>();
        picNames.forEach(picName -> {
            picName = StringUtils.substringAfter(picName, prefixPath);
            FileCreateDTO f = FileCreateDTO.builder()
                    .url(picName)
                    .build();
            fileCreateDTOS.add(f);
        });
        saveVideoFiles(file.getDatasetId(), fileCreateDTOS, DatatypeEnum.IMAGE.getValue(), file.getId(), file.getCreateUserId());
    }

    /**
     * 批量更新file
     *
     * @param datasetVersionFiles 文件列表
     * @param init                更新结果
     */
    public void updateStatus(List<DatasetVersionFile> datasetVersionFiles, FileStateEnum init) {
        List<Long> fileIds = datasetVersionFiles
                .stream().map(DatasetVersionFile::getFileId)
                .collect(Collectors.toList());
        UpdateWrapper<File> fileUpdateWrapper = new UpdateWrapper();
        fileUpdateWrapper.in("id", fileIds);
        File file = new File();
        file.setStatus(init.getCode());
        baseMapper.update(file, fileUpdateWrapper);
    }

    /**
     * 对minio 的账户密码进行加密操作
     *
     * @return Map<String, String> 加密后minio账户密码
     */
    @Override
    public Map<String, String> getMinIOInfo() {
        try {
            Map<String, String> keyPair = RsaEncrypt.genKeyPair();
            String publicKey = RsaEncrypt.getPublicKey(keyPair);
            String privateKey = RsaEncrypt.getPrivateKey(keyPair);
            return new HashMap<String, String>(MagicNumConstant.FOUR) {{
                put("url", RsaEncrypt.encrypt(url, publicKey));
                put("accessKey", RsaEncrypt.encrypt(accessKey, publicKey));
                put("secretKey", RsaEncrypt.encrypt(secretKey, publicKey));
                put("privateKey", privateKey);
            }};
        } catch (Exception e) {
            throw new BusinessException(ErrorEnum.DATA_ERROR);
        }
    }

    /**
     * 获取文件对应所有增强文件
     *
     * @param fileId 文件id
     * @return List<File> 文件对应所有增强文件
     */
    @Override
    @DataPermissionMethod(dataType = DatasetTypeEnum.PUBLIC)
    public List<File> getEnhanceFileList(Long fileId, Long datasetId) {
        File file = baseMapper.getOneById(fileId, datasetId);

        if (ObjectUtil.isNull(file)) {
            throw new BusinessException(ErrorEnum.FILE_ABSENT);
        }
        Dataset dataset = datasetService.getOneById(file.getDatasetId());
        if (ObjectUtil.isNull(dataset)) {
            throw new BusinessException(ErrorEnum.DATASET_ABSENT);
        }
        int enhanceFileCount = datasetVersionFileService.getEnhanceFileCount(dataset.getId(), dataset.getCurrentVersionName());
        if (enhanceFileCount > 0) {
            return datasetVersionFileService.getEnhanceFileList(dataset.getId(), dataset.getCurrentVersionName(), fileId);
        }
        return null;
    }

    /**
     * 获取文件详情
     *
     * @param fileId 文件ID
     * @return File 文件详情
     */
    @Override
    public File selectById(Long fileId, Long datasetId) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dataset_id", datasetId);
        queryWrapper.eq("id", fileId);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 条件搜索获取文件详情
     *
     * @param queryWrapper 查询条件
     * @return 文件详情
     */
    @Override
    public File selectOne(QueryWrapper<File> queryWrapper) {
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 获取文件列表
     *
     * @param wrapper 查询条件
     * @return 文件列表
     */
    @Override
    public List<File> listFile(QueryWrapper<File> wrapper) {
        return list(wrapper);
    }

    /**
     * 批量获取文件列表
     *
     * @param datasetId 数据集ID
     * @param offset    偏移量
     * @param batchSize 批大小
     * @return 文件列表
     */
    @Override
    public List<File> listBatchFile(Long datasetId, int offset, int batchSize) {
        DatasetVO datasetVO = datasetService.get(datasetId);
        return baseMapper.selectListOne(datasetId, datasetVO.getCurrentVersionName(), offset, batchSize);
    }

    /**
     * 判断执行中的采样任务是否过期
     */
    @Override
    public void expireSampleTask() {
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = taskUtils.zGetWithScore(START_SAMPLE_QUEUE);
        typedTuples.forEach(value -> {
            String timestampString = new BigDecimal(JSON.toJSONString(value.getScore())).toPlainString();
            long timestamp = Long.parseLong(timestampString);
            String keyId = JSONObject.parseObject(JSON.toJSONString(value.getValue())).getString("datasetIdKey");
            long timestampNow = System.currentTimeMillis() / 1000;
            if (timestampNow - timestamp > MagicNumConstant.TWO_HUNDRED) {
                LogUtil.info(LogEnum.BIZ_DATASET, "restart videoSample task keyId:{}", keyId);
                taskUtils.restartTask(keyId, START_SAMPLE_QUEUE, SAMPLE_PENDING_QUEUE, DETAIL_NAME
                        , JSON.toJSONString(value.getValue()));
            }
        });
    }

    /**
     * 根据版本和数据集ID获取文件url
     *
     * @param datasetId     数据集ID
     * @param versionName   版本名
     * @return List<String> url列表
     */
    @Override
    public List<String> selectUrls(Long datasetId, String versionName) {
        return baseMapper.selectUrls(datasetId, versionName);
    }

    /**
     * 根据version.changed获取文件name列表
     *
     * @Param datasetId     数据集ID
     * @Param changed       版本文件是否改动
     * @Param versionName   版本名称
     * @return List<name>   名称列表
     */
    @Override
    public List<String> selectNames(Long datasetId, Integer changed, String versionName) {
        return baseMapper.selectNames(datasetId, changed, versionName);
    }

    /**
     * 公共获取版本文件列表
     *
     * @param datasetId             数据集ID
     * @param currentVersionName    当前版本文件
     * @param page                  分页
     * @param queryCriteria         查询实体
     * @return LinkedList<DatasetVersionFileDTO> 版本文件列表
     */
    private LinkedList<DatasetVersionFileDTO> commDatasetVersionFiles(Long datasetId, String currentVersionName, Page page, FileQueryCriteriaVO queryCriteria) {
        queryCriteria.setDatasetId(datasetId);
        queryCriteria.setFileType(DatatypeEnum.IMAGE.getValue());

        //根据数据集ID和版本名称以及状态查询出当前数据集的所有文件
        LinkedList<DatasetVersionFileDTO> datasetVersionFiles = datasetVersionFileService
                .getListByDatasetIdAndAnnotationStatus(datasetId,
                        currentVersionName,
                        queryCriteria.getStatus(),
                        (page.getCurrent() - 1) * page.getSize(),
                        (int) page.getSize(),
                        queryCriteria.getSort(),
                        queryCriteria.getOrder(),
                        queryCriteria.getLabelId()
                );
        return datasetVersionFiles;
    }

    /**
     * 构建分页数据
     *
     * @param page 分页参数
     * @return Map<String, Object> 分页实体
     */
    private Map<String, Object> buildPage(Page page) {
        return org.dubhe.utils.PageUtil.toPage(new Page<File>() {{
            setCurrent(page.getCurrent());
            setSize(page.getSize());
            setTotal(NumberConstant.NUMBER_0);
        }}, new ArrayList<FileVO>());
    }

    /**
     * 文本数据集文件查询
     *
     * @param datasetId         数据集id
     * @param page              分页条件
     * @param queryCriteria 查询文件参数
     * @return Map<String, Object> 文件查询列表
     */
    @Override
    public Map<String, Object> txtFilesByPage(Long datasetId, Page page, FileQueryCriteriaVO queryCriteria) {
        //查询数据集
        Dataset dataset = datasetService.getOneById(queryCriteria.getDatasetId());
        LinkedList<DatasetVersionFileDTO> datasetVersionFiles = commDatasetVersionFiles(datasetId, dataset.getCurrentVersionName(), page, queryCriteria);
        if (datasetVersionFiles == null || datasetVersionFiles.isEmpty()) {
            return buildPage(page);
        }
        List<File> files = getFileList(datasetVersionFiles, datasetId);
        Map<Long, File> fileListMap = files.stream().collect(Collectors.toMap(File::getId, obj -> obj));
        List<TxtFileVO> vos = datasetVersionFiles.stream().map(versionFile -> {
            TxtFileVO fileVO = TxtFileVO.builder().build();
            if (!Objects.isNull(fileListMap.get(versionFile.getFileId()))) {
                File file = fileListMap.get(versionFile.getFileId());
                BeanUtil.copyProperties(file, fileVO);
                fileVO.setPrediction(versionFile.getPrediction());
                fileVO.setLabelId(versionFile.getLabelId());
                fileVO.setAbstractName(Constant.ABSTRACT_NAME_PREFIX + file.getName());
                String afterPath = org.dubhe.utils.StringUtils.substringAfterLast(fileVO.getUrl(), SymbolConstant.SLASH);
                String beforePath = org.dubhe.utils.StringUtils.substringBeforeLast(fileVO.getUrl(), SymbolConstant.SLASH);
                String newPath = beforePath + SymbolConstant.SLASH + ABSTRACT_NAME_PREFIX + afterPath;
                fileVO.setAbstractUrl(newPath);
                fileVO.setStatus(versionFile.getAnnotationStatus());
            }
            return fileVO;
        }).collect(Collectors.toList());


        Page<File> pages = buildPages(page, files, dataset, queryCriteria);
        return org.dubhe.utils.PageUtil.toPage(pages, vos);
    }

    /**
     * 文本状态数量统计
     *
     * @param datasetId 数据集ID
     * @return ProgressVO 文本状态数量统计
     */
    @Override
    public ProgressVO getFileCountByStatus(Long datasetId) {
        Dataset dataset = datasetService.getOneById(datasetId);
        Set<Integer> unfinishedCount = FileTypeEnum.getStatus(FileTypeEnum.UNFINISHED_FILE.getValue());
        Set<Integer> finishedCount = FileTypeEnum.getStatus(FileTypeEnum.FINISHED_FILE.getValue());
        return new ProgressVO() {{
            setFinished(datasetVersionFileService.selectCount(new LambdaQueryWrapper<DatasetVersionFile>() {{
                                                                  eq(DatasetVersionFile::getDatasetId, datasetId);
                                                                  if (!org.dubhe.utils.StringUtils.isBlank(dataset.getCurrentVersionName())) {
                                                                      eq(DatasetVersionFile::getVersionName, dataset.getCurrentVersionName());
                                                                  } else {
                                                                      isNull(DatasetVersionFile::getVersionName);
                                                                  }
                                                                  ne(DatasetVersionFile::getStatus, DataStatusEnum.DELETE.getValue());
                                                                  in(DatasetVersionFile::getAnnotationStatus, finishedCount);
                                                              }}
            ));
            setUnfinished(datasetVersionFileService.selectCount(new LambdaQueryWrapper<DatasetVersionFile>() {{
                                                                    eq(DatasetVersionFile::getDatasetId, datasetId);
                                                                    if (!org.dubhe.utils.StringUtils.isBlank(dataset.getCurrentVersionName())) {
                                                                        eq(DatasetVersionFile::getVersionName, dataset.getCurrentVersionName());
                                                                    } else {
                                                                        isNull(DatasetVersionFile::getVersionName);
                                                                    }
                                                                    ne(DatasetVersionFile::getStatus, DataStatusEnum.DELETE.getValue());
                                                                    in(DatasetVersionFile::getAnnotationStatus, unfinishedCount);
                                                                }}
            ));

        }};
    }


    /**
     * 获取文件列表
     *
     * @param datasetVersionFiles   数据集版本文件列表
     * @param datasetId             数据集ID
     * @return List<File> 文件列表
     */
    private List<File> getFileList(List<DatasetVersionFileDTO> datasetVersionFiles, Long datasetId) {
        Set<Long> set = datasetVersionFiles
                .stream()
                .map(DatasetVersionFileDTO::getFileId)
                .collect(Collectors.toSet());
        QueryWrapper queryWrapper = new QueryWrapper<>()
                .in("id", set)
                .eq("dataset_id", datasetId);
        List<File> files = baseMapper.selectList(queryWrapper);

        return files;
    }


    /**
     * 构建文件列表分页
     *
     * @param page          分页条件
     * @param files         文件列表
     * @param dataset       数据集实体
     * @param queryCriteria 查询条件
     * @return ge<File> 分页结果
     */
    private Page<File> buildPages(Page page, List<File> files, Dataset dataset, FileQueryCriteriaVO queryCriteria) {
        Page<File> pages = new Page<>();
        pages.setTotal(
                datasetVersionFileService.selectCount(
                        new LambdaQueryWrapper<DatasetVersionFile>() {{
                            eq(DatasetVersionFile::getDatasetId, dataset.getId());
                            in(DatasetVersionFile::getStatus, DataStatusEnum.ADD.getValue(), DataStatusEnum.NORMAL.getValue());
                            if (queryCriteria.getStatus() != null) {
                                in(DatasetVersionFile::getAnnotationStatus, FileTypeEnum.getStatus(queryCriteria.getStatus()));
                            }
                            if (StringUtils.isBlank(dataset.getCurrentVersionName())) {
                                isNull(DatasetVersionFile::getVersionName);
                            } else {
                                eq(DatasetVersionFile::getVersionName, dataset.getCurrentVersionName());
                            }
                        }}
                )
        );
        pages.setRecords(files);
        pages.setSize(page.getSize());
        pages.setCurrent(page.getCurrent());

        return pages;
    }

}
