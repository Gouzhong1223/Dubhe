/** Copyright 2020 Tianshu AI Platform. All Rights Reserved.
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

import request from '@/utils/request';
import { API_MODULE_NAME } from '@/config';

export function list(params) {
  return request({
    url: `/${API_MODULE_NAME.ADMIN}/users`,
    method: 'get',
    params,
  });
}

export function add(data) {
  return request({
    url: `/${API_MODULE_NAME.ADMIN}/users`,
    method: 'post',
    data,
  });
}

export function del(ids) {
  return request({
    url: `/${API_MODULE_NAME.ADMIN}/users`,
    method: 'delete',
    data: { ids },
  });
}

export function edit(data) {
  return request({
    url: `/${API_MODULE_NAME.ADMIN}/users`,
    method: 'put',
    data,
  });
}

// 根据用户名模糊搜索用户列表
export function findByNickName() {
  return request({
    url: `/${API_MODULE_NAME.ADMIN}/users/findByNickName`,
    method: 'get',
  });
}

// 获取用户配置信息
export function getUserConfig(userId) {
  return request({
    url: `/${API_MODULE_NAME.ADMIN}/users/getUserConfig`,
    method: 'get',
    params: { userId },
  });
}

// 更改用户配置信息
export function submitUserConfig(data) {
  return request({
    url: `/${API_MODULE_NAME.ADMIN}/users/setUserConfig`,
    method: 'put',
    data,
  });
}

export default { list, add, edit, del };
