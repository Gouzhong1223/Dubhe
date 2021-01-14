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

export function list(params) {
  return request({
    url: 'api/modelOpt/taskInstance',
    method: 'get',
    params,
  });
}

export function del(data) {
  return request({
    url: `api/modelOpt/taskInstance`,
    method: 'delete',
    data,
  });
}

export function getInstance(params) {
  return request({
    url: `api/modelOpt/taskInstance/detail`,
    method: 'get',
    params,
  });
}

export function cancel(data) {
  return request({
    url: `api/modelOpt/taskInstance/cancel`,
    method: 'put',
    data,
  });
}

export function resubmit(data) {
  return request({
    url: `api/modelOpt/taskInstance/resubmit`,
    method: 'post',
    data,
  });
}