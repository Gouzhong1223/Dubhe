<template>
  <div class="chapter">
    <div class="header">
      <el-button type="primary" @click="handleDialog('create')"
        >新增章节</el-button
      >
    </div>
    <el-table
      v-loading="isLoading"
      :data="chapterData"
      style="width: 100%"
      row-key="chapterId"
    >
      <el-table-column label="章节id">
        <template slot-scope="scope">
          <span>{{ scope.row.chapterId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="编号">
        <template slot-scope="scope">
          <span>{{ scope.row.serialNumber }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间">
        <template slot-scope="scope">
          <span
            >{{ scope.row.createTime.split('T')[0] }}
            {{ scope.row.createTime.split('T')[1] }}</span
          >
        </template>
      </el-table-column>
      <el-table-column label="章节名称">
        <template slot-scope="scope">
          <span>{{ scope.row.chapterName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="章节类型">
        <template slot-scope="scope">
          <span v-if="scope.row.chapterType === 0">PDF</span>
          <span v-else-if="scope.row.chapterType === 1">视频</span>
          <span v-else>PPT</span>
        </template>
      </el-table-column>
      <el-table-column label="描述">
        <template slot-scope="scope">
          <span>{{ scope.row.introduction }}</span>
        </template>
      </el-table-column>
      <el-table-column label="上传文件名">
        <template slot-scope="scope">
          <span>{{ scope.row.fileName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            @click="handleDialog('edit', scope.row)"
            >编辑</el-button
          >
          <el-popconfirm
            title="确定删除吗？"
            @onConfirm="handleDelete(scope.row)"
          >
            <el-button slot="reference" size="mini" type="danger"
              >删除</el-button
            >
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :destroy-on-close="true"
      :title="dialogShowType === 'edit' ? '修改章节' : '创建章节'"
      :visible.sync="isVisible"
      width="30%"
    >
      <div class="margin-y-10">章节名称：</div>
      <el-input v-model="chapterName" />
      <div class="margin-y-10">章节介绍:</div>
      <el-input v-model="chapterIntroduce" />
      <div class="margin-y-10">章节编号:</div>
      <el-input-number v-model="serialNumber" />
      <div class="margin-y-10">章节类型:</div>
      <el-select
        v-model="chapterType"
        placeholder="请选择章节类型"
        @change="handleAcceptChange"
      >
        <el-option
          v-for="item in serialOptons"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
      <div class="margin-y-10">文件:</div>
      <el-upload
        v-loading="isUploading"
        :action="uploadAPI"
        :headers="{
          Authorization: token,
        }"
        :multiple="false"
        :limit="1"
        :file-list="fileList"
        :on-change="handleuploadChange"
        :on-remove="handleUploadRemove"
        :on-error="handleUploadError"
        :on-progress="handleUploadProgress"
        :before-upload="handleBeforUpload"
      >
        <el-button size="small" type="primary">点击上传</el-button>
      </el-upload>
      <span slot="footer" class="dialog-footer">
        <el-button @click="isVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleDialogConfirm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import service from '@/api/course'
import { getToken } from '@/utils/auth'

export default {
  data() {
    return {
      uploadAPI: `${process.env.VUE_APP_BASE_API}api/v1/data/file/upload`,
      token: getToken(),
      isUploading: false,
      isVisible: false,
      isLoading: false,
      dialogShowType: '',
      serialOptons: [
        { label: 'PDF', value: 0 },
        { label: '视频', value: 1 },
        { label: 'PPT', value: 2 },
      ],
      uploadAcceptType: '',
      chapterData: [],
      chapterName: '',
      chapterIntroduce: '',
      serialNumber: '',
      chapterType: '',
      fileUrl: '',
      fileId: '',
      currentChapterId: '',
      fileList: [],
    }
  },
  created() {
    this.initData()
  },
  methods: {
    async initData() {
      this.isLoading = true
      const chapter = await service.chapter.get(this.$route.query.courseId)
      this.chapterData = chapter
      this.isLoading = false
    },

    async handleDelete(row) {
      await service.chapter.remove(row.chapterId)
      this.initData()
      this.$message({
        message: '删除成功',
        type: 'success',
      })
    },

    async handleDialog(type, row) {
      this.currentChapterId = ''
      this.chapterType = ''
      this.chapterIntroduce = ''
      this.chapterName = ''
      this.serialNumber = ''
      this.fileId = ''
      this.fileUrl = ''
      this.dialogShowType = type
      this.isVisible = true
      this.fileList = []
      if (row) {
        this.handleAcceptChange(row.chapterType)
        this.currentChapterId = row.chapterId
        this.chapterType = row.chapterType
        this.chapterIntroduce = row.introduction
        this.chapterName = row.chapterName
        this.serialNumber = row.serialNumber
        this.fileId = row.fileId
        this.fileUrl = row.fileUrl
        this.fileList = [
          {
            name: row.fileName,
            url: row.fileUrl,
          },
        ]
      }
    },

    async handleDialogConfirm() {
      if (
        this.chapterName === '' ||
        this.serialNumber === '' ||
        this.chapterType === '' ||
        this.chapterIntroduce === '' ||
        this.fileId === '' ||
        this.fileList.length === 0
      ) {
        this.$message({
          type: 'warning',
          message: '请填写完整信息再提交',
        })
        return
      }
      if (
        !this.uploadAcceptType
          .split(',')
          .some((item) => this.fileList[0].name.endsWith(item))
      ) {
        this.$message({
          type: 'warning',
          message: '不支持该文件类型的上传',
        })
        return
      }
      // 创建
      if (this.dialogShowType === 'create') {
        await service.chapter.create({
          courseId: this.$route.query.courseId,
          courseChapterName: this.chapterName,
          serialNumber: this.serialNumber,
          chapterType: this.chapterType,
          introduction: this.chapterIntroduce,
          fileId: this.fileId,
        })

        this.$message({
          type: 'success',
          message: '创建成功',
        })
      } else {
        await service.chapter.update({
          courseId: this.$route.query.courseId,
          courseChapterId: this.currentChapterId,
          courseChapterName: this.chapterName,
          serialNumber: this.serialNumber,
          chapterType: this.chapterType,
          introduction: this.chapterIntroduce,
          fileId: this.fileId,
        })

        this.$message({
          type: 'success',
          message: '修改成功',
        })
      }

      this.initData()
      this.isVisible = false
    },

    handleuploadChange(file) {
      if (file?.response) {
        const { url, id, name } = file.response.data
        this.fileUrl = url
        this.fileId = id
        this.fileList = [{ name, url }]
        this.isUploading = false
      }
    },

    handleUploadRemove() {
      this.fileList = []
    },

    handleUploadError() {
      this.fileList = []
      this.$message({
        type: 'error',
        message: '上传失败',
      })
      this.isUploading = false
    },

    handleUploadProgress() {
      this.isUploading = true
    },

    handleAcceptChange(value) {
      if (value === 0) {
        this.uploadAcceptType = '.pdf,.PDF'
      } else if (value === 1) {
        this.uploadAcceptType =
          '.mp4,.avi,.MP4,.AVI,.3gp,.3GP,.mpeg,.MPEG,.mov,.MOV'
      } else if (value === 2) {
        this.uploadAcceptType = '.ppt,.PPT,.pptx,.PPTX'
      }
    },

    // eslint-disable-next-line consistent-return
    handleBeforUpload(file) {
      if (!this.uploadAcceptType) {
        this.$message({
          type: 'warning',
          message: '请先选择章节类型',
        })
        return false
      }
      if (
        !this.uploadAcceptType
          .split(',')
          .some((item) => file.name.endsWith(item))
      ) {
        this.$message({
          type: 'warning',
          message: '不支持该文件类型的上传',
        })
        return false
      }
    },
  },
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.chapter {
  .margin-y-10 {
    margin: 10px 0;
  }
  .header {
    padding: 10px 20px 0;
  }

  ::v-deep .avatar-uploader {
    .el-upload {
      width: 100px;
      height: 100px;
      border: 1px solid #ccc;
      border-radius: 10px;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    .avatar {
      width: 100px;
      height: 100px;
      border-radius: 10px;
      img {
        width: 100%;
        height: 100%;
        border: 1px solid transparent;
        border-radius: 10px;
      }
    }
  }
}
</style>
