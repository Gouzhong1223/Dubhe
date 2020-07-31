/** Copyright 2020 Zhejiang Lab. All Rights Reserved.
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
    url: 'api/v1/notebook/notebooks',
    method: 'get',
    params,
  });
}

export function add(data) {
  return request({
    url: 'api/v1/notebook/notebooks',
    method: 'post',
    data,
  });
}

export function del(ids) {
  return request({
    url: 'api/v1/notebook',
    method: 'delete',
    data: ids,
  });
}

export function start(params) {
  return request({
    url: 'api/v1/notebook/start',
    method: 'put',
    params,
  });
}

export function stop(params) {
  return request({
    url: 'api/v1/notebook/stop',
    method: 'put',
    params,
  });
}

export function open(id) {
  return request({
    url: `api/v1/notebook/${id}`,
    method: 'get',
  });
}

export function getStatus() {
  return request({
    url: `api/v1/notebook/status`,
    method: 'get',
  });
}

export function getModels() {
  return request({
    url: `api/v1/notebook/notebook-model`,
    method: 'get',
  });
}

export function myNotebookCount() {
  return request({
    url: `api/v1/notebook/run-number`,
    method: 'get',
  });
}

export function createNotebook(source, data) {
  return request({
    url: `api/v1/notebook/create/${source}`,
    method: 'post',
    data,
  });
}

export function getNotebookAddress(id) {
  return request({
    url: `api/v1/notebook/${id}/get-address`,
    method: 'get',
  });
}

export function detail(data) {
  return request({
    url: 'api/v1/notebook/detail',
    method: 'post',
    data,
  });
}

export default { list, add, del, start, stop, open, detail };
