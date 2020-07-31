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

package org.dubhe.data.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dubhe.data.constant.FileStatusEnum;
import org.dubhe.data.domain.bo.FileBO;
import org.dubhe.data.domain.bo.TaskSplitBO;
import org.dubhe.data.domain.dto.FileCreateDTO;
import org.dubhe.data.domain.entity.Dataset;
import org.dubhe.data.domain.entity.File;
import org.dubhe.data.domain.entity.Task;
import org.dubhe.data.domain.vo.FileQueryCriteriaVO;
import org.dubhe.data.domain.vo.FileVO;
import org.dubhe.data.domain.vo.ProgressVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description 文件信息服务
 * @date 2020-04-10
 */
public interface FileService {

    /**
     * 文件详情
     *
     * @param fileId 文件id
     * @return FileVO 文件详情
     */
    FileVO get(Long fileId);

    /**
     * 文件查询
     *
     * @param datasetId         数据集id
     * @param page              分页条件
     * @param fileQueryCriteria 查询文件参数
     * @return Map<String, Object> 文件查询列表
     */
    Map<String, Object> listVO(Long datasetId, Page page, FileQueryCriteriaVO fileQueryCriteria);

    /**
     * 文件查询，物体检测标注页面使用
     *
     * @param datasetId 数据集id
     * @param offset    offset
     * @param limit     limit
     * @param page      分页条件
     * @param type      文件类型
     * @return Page<File> 文件查询列表
     */
    Page<File> listByLimit(Long datasetId, Long offset, Integer limit, Integer page, Integer type);

    /**
     * 获取offset
     *
     * @param datasetId 数据集id
     * @param fileId    文件id
     * @param type      文件类型
     * @return Integer 获取到的offset
     */
    Integer getOffset(Long fileId, Long datasetId, Integer type);

    /**
     * 获取首个文件
     *
     * @param datasetId 数据集id
     * @param type      文件类型
     * @return Long 获取首个文件
     */
    Long getFirst(Long datasetId, Integer type);

    /**
     * 对minio 的账户密码进行加密操作
     *
     * @return Map<String, String> minio账户密码加密map
     */
    Map<String, String> getMinIOInfo() throws Throwable;

    /**
     * 获取文件对应所有增强文件
     *
     * @param fileId 文件id
     * @return List<File> 获取文件对应所有增强文件列表
     */
    List<File> getEnhanceFileList(Long fileId);

    /**
     * 视频采样任务
     */
    void videoSample();

    /**
     * 更新文件状态
     *
     * @param files          文件集合
     * @param fileStatusEnum 文件状态
     * @return int 更新结果是否成功
     */
    int update(Collection<File> files, FileStatusEnum fileStatusEnum);

    /**
     * 根据文件ID获取文件内容
     *
     * @param fileId 文件ID
     * @return
     */
    File selectById(Long fileId);

    /**
     * 根据查询条件获取第一个文件
     *
     * @param queryWrapper
     * @return
     */
    File selectOne(QueryWrapper<File> queryWrapper);

    /**
     * 文件完成自动标注
     *
     * @param files file文件
     * @return boolean 文件完成自动标注是否成功
     */
    boolean finishAnnotation(Dataset dataset, Set<Long> files);

    /**
     * 如果ids为空，则返回空
     *
     * @param fileIds 文件id集合
     * @return Set<File> 获取到的文件
     */
    Set<File> get(List<Long> fileIds);

    /**
     * 保存文件
     *
     * @param fileId 文件id
     * @param files  file文件
     * @return List<Long> 保存文件数量
     */
    List<Long> saveFiles(Long fileId, List<FileCreateDTO> files);

    /**
     * 数据集标注进度
     *
     * @param datasetIds 数据集id
     * @return Map<Long, ProgressVO> 数据集标注进度
     */
    Map<Long, ProgressVO> listStatistics(Collection<Long> datasetIds);

    /**
     * 删除文件
     *
     * @param datasetId 数据集id
     */
    void delete(Long datasetId);

    /**
     * 判断视频数据集是否已存在视频
     *
     * @param datasetId 数据集id
     */
    void isExistVideo(Long datasetId);

    /**
     * 保存视频文件
     *
     * @param fileId fileId
     * @param files  file文件
     * @param type   文件类型
     * @param pid    文件父id
     * @param userId 用户id
     */
    void saveVideoFiles(Long fileId, List<FileCreateDTO> files, int type, Long pid, Long userId);

    /**
     * 取过滤后的文件
     *
     * @param datasetIds 数据集id集合
     * @param status     按状态过滤
     * @param need       需要还是不需要，true为需要status中的状态，false为不要其中的状态
     * @return Set<File> 过滤后的文件
     */
    Set<File> toFiles(List<Long> datasetIds, Dataset dataset, Collection<Integer> status, boolean need);

    /**
     * 判断是否存在手动标注中的文件
     *
     * @param datasetId 数据集id
     * @return boolean 判断是否存在手动标注中的文件
     */
    boolean hasManualAnnotating(Long datasetId);

    /**
     * 将整体任务分割
     *
     * @param files file文件
     * @param task  任务
     * @return List<TaskSplitBO> 分割后的任务
     */
    List<TaskSplitBO> split(Collection<File> files, Task task);

    /**
     * 根据条件获取文件列表
     *
     * @param wrapper 查询条件
     * @return
     */
    List<File> listFile(QueryWrapper<File> wrapper);

}
