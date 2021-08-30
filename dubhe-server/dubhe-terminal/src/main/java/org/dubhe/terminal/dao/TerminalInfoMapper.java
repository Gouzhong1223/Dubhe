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

package org.dubhe.terminal.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.dubhe.terminal.domain.entity.TerminalInfo;

import java.util.List;

/**
 * @description
 * @date 2021-07-15
 */
public interface TerminalInfoMapper extends BaseMapper<TerminalInfo> {
    /**
     * 还原回收数据
     *
     * @param id terminal id
     * @return int 数量
     */
    @Update("update terminal_info set deleted = 1 where terminal_id = #{id}")
    int deleteByTerminalId(@Param("id") Long id);

    @Select("select * from terminal_info where terminal_id = #{id} and deleted = 0")
    List<TerminalInfo> selectByTerminalId(@Param("id") Long id);
}
