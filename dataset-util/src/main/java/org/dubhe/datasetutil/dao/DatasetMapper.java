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
package org.dubhe.datasetutil.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dubhe.datasetutil.domain.entity.Dataset;

/**
 * @description 数据集 Mapper接口
 * @date 2020-9-17
 */
public interface DatasetMapper  extends BaseMapper<Dataset> {

    /**
     * 根据数据集ID查询数据集
     *
     * @param datasetId 数据集id
     * @return Dataset  根据数据集ID得到数据集
     */
    @Select("select * from data_dataset where id = #{datasetId} and is_import = 1")
    Dataset findDatasetById(@Param("datasetId") Long datasetId);

    /**
     * 查询数据集标签数量
     *
     * @param datasetId 数据集id
     * @return int 数据集标签数量
     */
    @Select("select count(1) from data_dataset_label where dataset_id = #{datasetId}")
    int findDataLabelById(@Param("datasetId") Long datasetId);

    /**
     * 查询数据集文件数量
     *
     * @param datasetId 数据集id
     * @return int 数据集标签数量
     */
    @Select("select count(1) from data_file where dataset_id = #{datasetId}")
    int findDataFileById(@Param("datasetId") Long datasetId);

    /**
     * 根据数据集ID查询数据集
     *
     * @param datasetId 数据集id
     * @return Dataset  根据数据集ID得到数据集
     */
    @Select("select * from data_dataset where id = #{datasetId}")
    Dataset findDatasetByIdNormal(@Param("datasetId") Long datasetId);


    /**
     * 新增数据集
     *
     * @param insertSql sql语句
     */
    @Insert("${insertSql}")
    void saveBatch(@Param("insertSql") String insertSql);

    /**
     * 删除数据集通过数据集ID
     *
     * @param datasetId 数据集ID
     */
    @Delete("delete  from data_dataset where id = #{datasetId}")
    void deleteDatasetById(@Param("datasetId") long datasetId);
}
