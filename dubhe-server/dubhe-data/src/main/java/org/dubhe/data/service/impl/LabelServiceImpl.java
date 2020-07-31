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

package org.dubhe.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dubhe.base.MagicNumConstant;
import org.dubhe.data.constant.Constant;
import org.dubhe.data.constant.DatasetLabelEnum;
import org.dubhe.data.constant.ErrorEnum;
import org.dubhe.data.dao.LabelMapper;
import org.dubhe.data.domain.entity.DatasetLabel;
import org.dubhe.data.domain.entity.Label;
import org.dubhe.data.service.DatasetLabelService;
import org.dubhe.data.service.LabelService;
import org.dubhe.enums.LogEnum;
import org.dubhe.exception.BusinessException;
import org.dubhe.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description 数据集标签 服务实现类
 * @date 2020-04-01
 */
@Service
public class LabelServiceImpl extends ServiceImpl<LabelMapper, Label> implements LabelService {

    @Autowired
    private DatasetLabelService datasetLabelService;

    /**
     * 支持自动标注标签
     *
     * @return List<Label> 自动标注标签
     */
    @Override
    public List<Label> listSupportAuto() {
        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.lambda().le(Label::getId, Constant.RESERVED_LABEL_ID);
        labelQueryWrapper.eq("type", DatasetLabelEnum.AUTO.getType());
        return getBaseMapper().selectList(labelQueryWrapper);
    }

    /**
     * 标签查询
     *
     * @param datasetId 数据集id
     * @return List<Label> 标签列表
     */
    @Override
    public List<Label> list(Long datasetId) {
        return getBaseMapper().batchListByIds(datasetId);
    }

    /**
     * 删除数据集标签
     *
     * @param id 数据集id
     * @return int 执行次数
     */
    @Override
    public int delDataset(Long id) {
        List<DatasetLabel> datasetLabels = datasetLabelService.list(id);
        Set<Long> labels = datasetLabels.stream().map(DatasetLabel::getLabelId).collect(Collectors.toSet());
        datasetLabelService.del(id);
        return del(labels);
    }

    /**
     * 删除数据集标签
     *
     * @param labelIds 标签id
     */
    public int del(Collection<Long> labelIds) {
        if (CollectionUtils.isEmpty(labelIds)) {
            return MagicNumConstant.ZERO;
        }
        Set<Long> filtered = labelIds.stream().filter(i -> i > Constant.RESERVED_LABEL_ID).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(filtered)) {
            return MagicNumConstant.ZERO;
        }
        return getBaseMapper().deleteBatchIds(filtered);
    }

    /**
     * 保存标签
     *
     * @param label     标签
     * @param datasetId 数据集id
     */
    @Override
    public Long save(Label label, Long datasetId) {
        save(Arrays.asList(label), datasetId);
        return label.getId();
    }

    /**
     * 根据标签名获取标签id
     *
     * @param name 标签名
     * @return Long 标签id
     */
    public Long getAutoByName(String name) {
        if (name == null) {
            return null;
        }
        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.lambda().eq(Label::getName, name).le(Label::getId, Constant.RESERVED_LABEL_ID);
        List<Label> labels = getBaseMapper().selectList(labelQueryWrapper);
        if (CollectionUtils.isEmpty(labels)) {
            return null;
        }
        return labels.get(MagicNumConstant.ZERO).getId();
    }

    /**
     * 保存标签
     *
     * @param labels    标签
     * @param datasetId 数据集id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(List<Label> labels, Long datasetId) {
        if (CollectionUtils.isEmpty(labels)) {
            return;
        }
        List<DatasetLabel> rels = new LinkedList<>();
        List<Label> newLabels = new LinkedList<>();
        for (Label label : labels) {
            Long id = label.getId();
            if (id == null) {
                id = getAutoByName(label.getName());
                label.setId(id);
            }
            if (id != null && exist(id)) {
                rels.add(DatasetLabel.builder()
                        .datasetId(datasetId)
                        .labelId(id)
                        .build());
                continue;
            }
            if (StringUtils.isEmpty(label.getName())) {
                continue;
            }
            newLabels.add(label);
        }

        for (Label label : newLabels) {
            saveCustom(label, datasetId);
            rels.add(DatasetLabel.builder()
                    .datasetId(datasetId)
                    .labelId(label.getId())
                    .build());
        }

        if (CollectionUtils.isEmpty(rels)) {
            return;
        }
        rels = datasetLabelService.filterExist(rels);
        datasetLabelService.saveList(rels);
    }

    /**
     * 存在标签
     *
     * @param name      标签名
     * @param datasetId 数据集id
     * @return boolean 是否存在结果
     */
    public boolean exist(String name, Long datasetId) {
        List<Long> ids = datasetLabelService.list(datasetId).stream()
                .map(DatasetLabel::getLabelId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }

        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.lambda().eq(Label::getName, name).in(Label::getId, ids);

        return getBaseMapper().selectCount(labelQueryWrapper) > MagicNumConstant.ZERO;
    }

    /**
     * 存在标签
     *
     * @param labelId 标签id
     * @return boolean
     */
    public boolean exist(Long labelId) {
        boolean exist = getById(labelId) != null;
        if (!exist) {
            LogUtil.warn(LogEnum.BIZ_DATASET, "label does not exit. labelId:{}", labelId);
        }
        return exist;
    }

    /**
     * 添加自定义标签，不保存关联关系
     *
     * @param label     需提前过滤系统已有标签
     * @param datasetId 数据集id
     */
    public void saveCustom(Label label, Long datasetId) {
        if (label == null) {
            return;
        }

        if (exist(label.getName(), datasetId)) {
            throw new BusinessException(ErrorEnum.LABEL_NAME_EXIST, label.getName(), null);
        }
        label.setCreateUserId(Constant.DEFAULT_USER);
        label.setUpdateUserId(Constant.DEFAULT_USER);
        save(label);
    }

    /**
     * 获取指定类型下所有标签
     *
     * @param type 标签类型
     * @return List<Label> 指定类型下所有标签
     */
    @Override
    public List<Label> listByType(Integer type) {
        return baseMapper.selectListByType(type);
    }

    /**
     * 获取数据集下所有标签类型
     *
     * @param datasetId 数据集id
     * @return List<Integer> 数据集下所有标签类型
     */
    @Override
    public List<Integer> getDatasetLabelTypes(Long datasetId) {
        return baseMapper.getDatasetLabelTypes(datasetId);
    }

}
