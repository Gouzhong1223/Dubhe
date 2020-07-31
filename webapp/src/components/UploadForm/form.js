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

import axios from 'axios';
import { Message } from 'element-ui';
import './style.scss';

// eslint-disable-next-line import/no-extraneous-dependencies
const path = require('path');

let msgInstance;

const defaultAccept = d => `${d} MB`;

export default {
  name: 'UploadForm',
  props: {
    action: String,
    accept: {
      type: String,
      default: '.jpg,.png,.bmp,.jpeg',
    },
    acceptSize: {
      type: Number, // 如果传入 0 代表不限制
      default: 5, // MB
    },
    acceptSizeFormat: { // 格式化文本
      type: Function,
      default: defaultAccept,
    },
    limit: {
      type: Number,
      default: 1000,
    },
    showFileCount: {
      type: Boolean,
      default: true,
    },
    wordShow: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      uploading: false,
      lenOfFileList: 0,
      source: axios.CancelToken.source(),
    };
  },
  computed: {
    acceptFormatStr() {
      const formats = this.accept.split(',');
      return formats.join('/');
    },
  },
  methods: {
    onMessageClose() {
      // 清理 message 实例
      msgInstance = null;
    },
    reset() {
      this.$refs.uploader.clearFiles();
      this.lenOfFileList = 0;
    },
    fileChange(file, fileList) {
      // 根据后缀名进行格式匹配
      const acceptTypes = this.accept.split(',');
      const extname = path.extname(file.raw.name);
      const mimeType = acceptTypes.includes(extname.toLowerCase());

      if (!mimeType) {
        fileList.splice(fileList.indexOf(file), 1);
        if (!msgInstance) {
          Message.info({
            message: `文件格式不支持`,
            onClose: this.onMessageClose,
          });
        }
        return;
      }
      // accept 支持传入 0 代表不限制大小
      const isOverSize = this.acceptSize !== 0 && (file.size / (1024 * 1024)) > this.acceptSize;
      if (isOverSize) {
        fileList.splice(fileList.indexOf(file), 1);
        if (!msgInstance) {
          msgInstance = Message.info({
            message: `不能添加大于${this.acceptSize}MB的文件`,
            onClose: this.onMessageClose,
          });
        }
        return;
      }

      for (const item of fileList.slice(0, fileList.length - 1)) {
        if (item.name === file.name) {
          fileList.splice(fileList.indexOf(file), 1);
          if (!msgInstance) {
            msgInstance = Message.info({
              message: `不能添加文件名相同的文件`,
              onClose: this.onMessageClose,
            });
          }
          return false;
        }
      }

      this.lenOfFileList = fileList.length;
      // 触发文件变动事件
      this.$emit('fileChange', file, fileList);
    },
    onProgress(res) {
      const {loaded} = res;
      const {total} = res;
      const uploadPercent = Math.floor((loaded / total) * 100) > 1 ? Math.floor((loaded / total) * 100) : 1;
      this.$emit('onUploadPercent', uploadPercent);
    },
    onRemove(file, fileList) {
      this.lenOfFileList = fileList.length;
    },
    cancelUpload() {
      if (this.source) {
        this.source.cancel('取消上传');
      }
    },
    onExceed(files, fileList) {
      if (files.length > this.limit || fileList.length > this.limit) {
        Message.info(`单次上传文件数量不能超过${this.limit}`);
      }
    },
  },
  render(h) {
    // vue jsx 属性传递需要把 on- 放到 props 内
    // 详细参考：https://zhuanlan.zhihu.com/p/37920151
    const uploadProps = {
      props: {
        onChange: this.fileChange,
        onRemove: this.onRemove,
        onExceed: this.onExceed,
        ...this.$attrs,
      },
      ref: 'uploader',
    };

    return (
      <div id='upload-form-style' class='upload-form'>
        <el-upload
          action={this.action}
          accept={this.accept}
          class='upload-field'
          limit={this.limit}
          multiple
          list-type='picture'
          auto-upload={false}
          disabled={this.uploading}
          {...uploadProps}
        >
          <el-button disabled={this.uploading} size='mini' icon='el-icon-upload'>上传文件</el-button>
          <div slot='tip' class='flex f1 flex-between' style='margin-left: 20px;'>
            <div class='upload-tip'>
              <span>文件格式: { this.acceptFormatStr }</span>
              {
                 this.acceptSize > 0 && (
                   <span>, 文件不大于 { this.acceptSizeFormat(this.acceptSize) }</span>
                 )
              }
            </div>
            {
              this.showFileCount && (
                this.wordShow ? <span class='upload-chosen-tip'>已选择{ this.lenOfFileList }张</span> : null
              )
            }
          </div>
        </el-upload>
      </div>
    );
  },
};
