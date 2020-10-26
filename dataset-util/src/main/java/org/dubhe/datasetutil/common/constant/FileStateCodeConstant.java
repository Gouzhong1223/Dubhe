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
package org.dubhe.datasetutil.common.constant;

/**
 * @description 数据集状态码
 * @date 2020-09-03
 */
public class FileStateCodeConstant {

    private FileStateCodeConstant(){

    }

    /**
     * 未标注
     */
    public static final Integer NOT_ANNOTATION_FILE_STATE = 101;
    /**
     * 手动标注中
     */
    public static final Integer MANUAL_ANNOTATION_FILE_STATE = 102;
    /**
     * 自动标注完成
     */
    public static final Integer AUTO_TAG_COMPLETE_FILE_STATE = 103;
    /**
     * 标注完成
     */
    public static final Integer ANNOTATION_COMPLETE_FILE_STATE = 104;
    /**
     * 标注未识别
     */
    public static final Integer ANNOTATION_NOT_DISTINGUISH_FILE_STATE = 105;
    /**
     * 目标跟踪完成
     */
    public static final Integer TARGET_COMPLETE_FILE_STATE = 201;

}