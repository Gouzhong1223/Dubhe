<template>
  <div class="course">
    <el-table
      :data="tableData"
      style="width: 100%"
      row-key="id"
      default-expand-all
    >
      <el-table-column type="expand" prop="courses">
        <template slot-scope="scope">
          <el-table :data="scope.row.courses" :border="true">
            <el-table-column prop="courseId" label="课程id" />
            <el-table-column label="课程封面">
              <template slot-scope="scope">
                <img
                  style="width:80px; height:80px;"
                  :src="scope.row.fileUrl"
                />
              </template>
            </el-table-column>
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="introduction" label="课程介绍" />

            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="primary"
                  @click="editVisible = true"
                  >编辑</el-button
                >
                <el-popconfirm
                  title="确定删除吗？"
                  @onConfirm="handleDelete(scope.$index, scope.row)"
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
          <el-button type="primary" class="add" @click="addCourse(scope.row)"
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

    <el-dialog title="创建课程" :visible.sync="createVisible" width="30%">
      <div class="margin-top-10">课程名称：</div>
      <el-input v-model="courseName" />
      <div class="margin-top-10">课程介绍:</div>
      <el-input v-model="courseIntroduce" />
      <div class="margin-top-10">封面图片:</div>
      <el-upload
        v-loading="isUploading"
        class="avatar-uploader"
        action="http://139.224.44.245:8001/api/v1/data/file/upload"
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
        <el-button @click="createVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleCreateCourse">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="编辑课程" :visible.sync="editVisible" width="30%">
      <span slot="footer" class="dialog-footer">
        <el-button @click="editVisible = false">取 消</el-button>
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
      token: getToken(),
      isUploading: false,
      imageUrl: '',
      imageId: '',
      curCourseTypeId: '',
      courseName: '',
      courseIntroduce: '',
      tableData: [],
      createVisible: false,
      editVisible: false,
    }
  },
  created() {
    this.initData()
  },
  methods: {
    async initData() {
      const course = await service.course.get()
      this.tableData = course
    },

    addCourse(row) {
      this.createVisible = true
      this.curCourseTypeId = row.courseTypeId
    },

    async handleCreateCourse() {
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
      this.createVisible = false
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

    async handleDialogShow(row) {
      this.dialogVisible = true
      if (row?.courseTypeId) {
        this.isUpdate = true
        this.updateId = row.id
        this.courseTypeName = row.name
        return
      }
      this.isUpdate = false
    },

    async handleDialogConfirm() {
      if (!this.courseTypeName) {
        this.$message({
          type: 'warning',
          message: '课程分类名称不能为空',
        })
        return
      }
      if (this.isUpdate) {
        await service.courseType.update(this.updateId, this.courseTypeName)
        this.$message({
          type: 'success',
          message: '修改成功',
        })
        this.dialogVisible = false
        this.initData()
      } else {
        const res = await service.courseType.create(this.courseTypeName)
        console.log(res)
      }
    },
  },
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.course {
  .margin-top-10 {
    margin-top: 10px;
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
    .add {
    }
  }
}
</style>
