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

package org.dubhe.data.pool;

import org.dubhe.base.MagicNumConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

/**
 * @description 定时任务线程池
 * @date 2020-05-29
 */
@Configuration
@EnableAsync
public class SchduledThreadPool {

    @Bean
    public Executor executor() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("async-scheduled-pool-");
        taskScheduler.setPoolSize(MagicNumConstant.FIVE);
        return taskScheduler;
    }

}
