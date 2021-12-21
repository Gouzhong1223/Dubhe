<template>
  <div class="category">
    <header class="header">
      <el-button type="primary" class="add" @click="handleDialogShow"
        >添加课程分类</el-button
      >
    </header>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column label="ID">
        <template slot-scope="scope">
          <span style="margin-left: 10px">{{ scope.row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="课程分类名称">
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最新更新时间">
        <template slot-scope="scope">
          <span>{{
            `${scope.row.updateTime.split('T')[0]} ${
              scope.row.updateTime.split('T')[1]
            }`
          }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            @click="handleDialogShow(scope.$index, scope.row)"
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

    <el-dialog title="提示" :visible.sync="dialogVisible" width="30%">
      课程分类名称：<el-input v-model="courseTypeName" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleDialogConfirm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import service from '@/api/course'

export default {
  data() {
    return {
      isUpdate: false,
      updateId: '',
      courseTypeName: '',
      tableData: [],
      dialogVisible: false,
    }
  },
  created() {
    this.initData()
  },
  methods: {
    async initData() {
      const courseType = await service.courseType.get()
      this.tableData = courseType
    },
    async handleDelete(index, row) {
      await service.courseType.remove(row.id)
      this.initData()
      this.$message({
        message: '删除成功',
        type: 'success',
      })
    },

    async handleDialogShow(index, row) {
      this.courseTypeName = ''
      this.dialogVisible = true
      if (row?.id) {
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
      } else {
        await service.courseType.create(this.courseTypeName)
      }
      this.$message({
        type: 'success',
        message: '修改成功',
      })
      this.dialogVisible = false
      this.initData()
      this.courseTypeName = ''
    },
  },
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.category {
  .header {
    padding: 20px;
    .add {
    }
  }
}
</style>
