"""
/**
* Copyright 2020 Zhejiang Lab. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* =============================================================
*/
"""
# coding:utf-8

finishTaskLua = """
local queues,values=KEYS,ARGV
redis.call('zrem', queues[1], values[1])
redis.call('lpush',queues[2],values[2])
redis.call('del',KEYS[3])
return
"""