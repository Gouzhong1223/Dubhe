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

package org.dubhe.serving.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dubhe.serving.domain.entity.ServingModelConfig;

import java.util.Set;

/**
 * @description 模型配置管理
 * @date 2020-08-26
 */
public interface ServingModelConfigService extends IService<ServingModelConfig> {
    /**
     * 根据服务id，获取模型配置id集合
     *
     * @param servingId 在线服务id
     * @return Set<Long> 模型配置id集合
     */
    Set<Long> getIdsByServingId(Long servingId);
}
