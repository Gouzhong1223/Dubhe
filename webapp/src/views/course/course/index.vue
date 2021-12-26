<template>
  <div class="course">
    <el-table
      v-loading="isLoading"
      :data="tableData"
      style="width: 100%"
      row-key="courseTypeId"
      default-expand-all
    >
      <el-table-column type="expand" prop="courses">
        <template slot-scope="scope">
          <el-table :data="scope.row.courses" :border="true" row-key="courseId">
            <el-table-column prop="courseId" label="课程id" width="80" />
            <el-table-column label="课程封面" width="120">
              <template slot-scope="scope">
                <img
                  style="width:100px; height:100px;"
                  :src="scope.row.fileUrl"
                />
              </template>
            </el-table-column>
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="introduction" label="课程介绍" />
            <el-table-column
              width="120"
              :formatter="(row) => (row.status === 0 ? '不可见' : '可见')"
              label="用户是否可见"
            />

            <el-table-column label="操作">
              <template slot-scope="subScope">
                <el-button
                  size="mini"
                  type="primary"
                  @click="handleToChapter(scope.row, subScope.row)"
                  >课程章节</el-button
                >
                <el-button
                  size="mini"
                  type="primary"
                  @click="
                    handleCourseDialogShow('edit', scope.row, subScope.row)
                  "
                  >编辑</el-button
                >
                <el-popconfirm
                  title="确定删除吗？"
                  @onConfirm="handleDelete(subScope.$index, subScope.row)"
                >
                  <el-button slot="reference" size="mini" type="danger"
                    >删除</el-button
                  >
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>

      <el-table-column>
        <template slot-scope="scope">
          <el-button
            type="primary"
            class="add"
            @click="handleCourseDialogShow('create', scope.row)"
            >添加课程</el-button
          >
        </template>
      </el-table-column>

      <el-table-column label="课程分类id">
        <template slot-scope="scope">
          <span style="margin-left: 10px">{{ scope.row.courseTypeId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="课程分类名称">
        <template slot-scope="scope">
          <span>{{ scope.row.courseTypeName }}</span>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :destroy-on-close="true"
      :title="dialogShowType === 'edit' ? '修改课程' : '创建课程'"
      :visible.sync="isVisible"
      width="30%"
    >
      <div class="margin-y-10">课程名称：</div>
      <el-input v-model="courseName" />
      <div class="margin-y-10">课程介绍:</div>
      <el-input v-model="courseIntroduce" />
      <template v-if="dialogShowType === 'edit'">
        <div class="margin-y-10">用户是否可见：</div>
        <div>
          <el-radio v-model="showForUserStatus" :label="0">不可见</el-radio>
          <el-radio v-model="showForUserStatus" :label="1">可见</el-radio>
        </div>
      </template>
      <div class="margin-y-10">封面图片:</div>
      <el-upload
        v-loading="isUploading"
        class="avatar-uploader"
        :action="uploadAPI"
        :headers="{
          Authorization: token,
        }"
        :show-file-list="false"
        :on-progress="
          () => {
            isUploading = true
          }
        "
        :on-success="handleUploadSuccess"
      >
        <div v-if="imageUrl" class="avatar">
          <img :src="imageUrl" />
        </div>
        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
      </el-upload>
      <span slot="footer" class="dialog-footer">
        <el-button @click="isVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleCourseConfirm">确 定</el-button>
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
      isLoading: false,
      isUploading: false,
      curCourseTypeId: '',
      imageUrl: '',
      imageId: '',
      courseName: '',
      courseIntroduce: '',
      tableData: [],
      isVisible: false,
      showForUserStatus: null,
      dialogShowType: '',
      curRowData: null,
      curSubRowData: null,
    }
  },
  created() {
    this.initData()
  },
  methods: {
    async initData() {
      this.isLoading = true
      const course = await service.course.get()
      this.tableData = course
      this.isLoading = false
    },

    handleCourseDialogShow(type, row, subRow) {
      this.dialogShowType = type
      this.isVisible = true
      this.curCourseTypeId = row.courseTypeId
      if (type === 'edit') {
        console.log(subRow)
        this.curRowData = row
        this.curSubRowData = subRow
        this.courseName = subRow.courseName
        this.courseIntroduce = subRow.introduction
        this.imageId = subRow.coverImageId
        this.imageUrl = subRow.fileUrl
        this.showForUserStatus = subRow.status
      } else {
        this.courseName = ''
        this.courseIntroduce = ''
        this.imageId = ''
        this.imageUrl = ''
      }
    },

    async handleCourseConfirm() {
      if (
        !this.courseName ||
        !this.curCourseTypeId ||
        !this.courseIntroduce ||
        !this.imageId
      ) {
        this.$message({
          message: '请填写完整信息再提交',
          type: 'warning',
        })
        return
      }
      if (this.dialogShowType === 'create') {
        await service.course.create({
          courseName: this.courseName,
          courseTypeId: this.curCourseTypeId,
          introduction: this.courseIntroduce,
          coverImageId: this.imageId,
        })
        this.$message({
          message: '创建课程成功',
          type: 'success',
        })
      } else {
        // 编辑课程
        await service.course.update({
          courseId: this.curSubRowData.courseId,
          courseName: this.courseName,
          courseTypeId: this.curRowData.courseTypeId,
          introduction: this.courseIntroduce,
          coverImageId: this.imageId,
          status: this.showForUserStatus, // 0-不可见 1-可见
        })

        this.$message({
          message: '编辑课程成功',
          type: 'success',
        })
      }
      this.isVisible = false
      this.initData()
    },

    async handleDelete(index, row) {
      await service.course.remove(row.courseId)
      this.initData()
      this.$message({
        message: '删除成功',
        type: 'success',
      })
    },

    handleUploadSuccess(props) {
      this.imageUrl = props.data.url
      this.imageId = props.data.id
      this.isUploading = false
    },

    handleToChapter(row, subRow) {
      this.$router.push({
        path: '/course/chapter',
        query: {
          courseId: subRow.courseId,
        },
      })
    },
  },
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.course {
  .margin-y-10 {
    margin: 10px 0;
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

  .header {
    padding: 20px;
  }
}
</style>
