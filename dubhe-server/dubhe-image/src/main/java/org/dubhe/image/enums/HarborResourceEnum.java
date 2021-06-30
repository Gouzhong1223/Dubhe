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
package org.dubhe.image.enums;

/**
 * @description harbor镜像来源枚举
 * @date 2020-07-16
 */
public enum HarborResourceEnum {

    NOTEBOOK(0, "notebook预置"),
    TRAIN(1, "train预置"),
    TRAIN_SYNC(2, "train同步镜像");

    /**
     * 编码
     */
    private Integer code;

    /**
     * 描述
     */
    private String description;

    HarborResourceEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
