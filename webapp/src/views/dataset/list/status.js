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

import { datasetStatusMap } from '../util';

export default {
  name: 'DatasetStatus',
  functional: true,
  render(h, { data, props }) {
    const { withAllDatasetStatusList, filterByDatasetStatus, datasetStatusFilter } = props;
    const iconClass = ['el-icon-arrow-down', 'el-icon--right'];
    const textClass = datasetStatusFilter === 'all' ? null : 'primary';
    const columnProps = {
      ...data,
      scopedSlots: {
        header: () => {
          return (
            <el-dropdown trigger='click' size='medium'>
              <span>
                <span {... { class: textClass } }>状态</span>
                <i {... { class: iconClass } } />
              </span>
              <el-dropdown-menu slot='dropdown'>
                {withAllDatasetStatusList.map(item => {
                  return (
                    <el-dropdown-item
                      key={item.value}
                      nativeOnClick={() => filterByDatasetStatus(item.value)}
                    >
                      {item.label}
                    </el-dropdown-item>
                  );
                })}
              </el-dropdown-menu>
              <el-tooltip effect='dark' content='数据集状态可能会延迟更新，请耐心等待' placement='top' style={{ marginLeft: '10px' }}>
                <i class='el-icon-question'/>
              </el-tooltip>
            </el-dropdown>
          );
        },
        default: ({ row }) => {
          // 导入自定义数据集 状态保持为标注完成（4）
          if (row.import) {
            row.status = 4;
          }
          const status = datasetStatusMap[row.status] || {};
          const colorProps = (!status.type && status.bgColor) && {
            props: {
              color: status.bgColor,
            },
            style: {
              color: status.color,
              borderColor: status.bgColor,
            },
          };
          return (
            <el-tag type={status.type} {...colorProps}>{status.name}</el-tag>
          );
        },
      },
    };

    return h('el-table-column', columnProps);
  },
};
