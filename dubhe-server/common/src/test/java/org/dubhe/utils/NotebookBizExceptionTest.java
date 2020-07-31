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

package org.dubhe.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.dubhe.exception.NotebookBizException;
import org.junit.Test;

/**
 * @desc
 * @date 2020.06.16
 */
@Slf4j
public class NotebookBizExceptionTest {

    @Test
    public void toStringTest() {
        try {
            throw new NotebookBizException("NotebookBizException toString Test");
        }catch (NotebookBizException e){
            log.error("{}", e);
        }
    }
}
