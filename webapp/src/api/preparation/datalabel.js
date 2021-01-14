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

export function getLabels(id) {
  return request({
    url: `api/data/datasets/${id}/labels`,
    method: 'get',
  });
}

export function createLabel(id, label) {
  return request({
    url: `api/data/datasets/${id}/labels`,
    method: 'post',
    data: label,
  });
}

export function editLabel(id, label) {
  return request({
    url: `api/data/datasets/labels/${id}`,
    method: 'put',
    data: label,
  });
}

export function getAutoLabels(labelGroupType) {
  return request({
    url: `api/data/datasets/labels/auto/${labelGroupType}`,
    method: 'get',
  });
}

