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

export function batchFinishAnnotation(data, datasetId) {
  return request({
    url: `api/data/datasets/files/${datasetId}/annotations`,
    method: 'post',
    data,
  });
}

export function delAnnotation(id) {
  const delData = { datasetId: id };
  return request({
    url: 'api/data/datasets/files/annotations',
    method: 'delete',
    data: delData,
  });
}

export function track(id) {
  return request({
    url: `api/data/datasets/files/annotations/auto/track/${id}`,
    method: 'get',
  });
}

export function autoAnnotate(ids) {
  const data = { datasetIds: ids };
  return request({
    url: 'api/data/datasets/files/annotations/auto',
    method: 'post',
    data,
  });
}

export function annotateStatus(id) {
  return request({
    url: `api/data/datasets/${id}`,
    method: 'get',
  });
}

// 发布版本
export function publish(data = {}) {
  return request({
    url: 'api/data/datasets/versions',
    method: 'post',
    data,
  });
}
