<template>
  <div v-loading="isLoading" class="course-detail">
    <el-timeline v-if="data.length > 0">
      <el-timeline-item v-for="chapter in data" :key="chapter.fileUrl">
        <el-card>
          <h2 style="display:flex;">
            {{ chapter.chapterName }}

            <el-button
              style="margin-left: auto;"
              size="mini"
              type="primary"
              @click="handleToDetail(chapter.chapterId, chapter.courseId)"
              >{{ chapter.learned === 0 ? '开始学习' : '学习完成' }}</el-button
            >
          </h2>
          <p>{{ chapter.introduction }}</p>
          <p>章节编号: {{ chapter.serialNumber }}</p>
          <p>状态: {{ chapter.learned === 0 ? '未学习' : '已学习' }}</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    <div v-else>暂无数据</div>
  </div>
</template>

<script>
import service from '@/api/course'

export default {
  data() {
    return {
      isLoading: false,
      data: [],
    }
  },
  created() {
    this.initData()
  },
  methods: {
    async initData() {
      this.isLoading = true
      const data = await service.chapter.get(this.$route.query.courseId)
      this.data = data
      this.isLoading = false
    },

    handleToDetail(chapterId, courseId) {
      this.$router.push({
        path: '/course/chapterDetail',
        query: {
          courseId,
          chapterId,
        },
      })
    },
  },
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.course-detail {
  margin: 20px;
  padding: 20px;
  border-radius: 10px;
  background-color: #fff;
  box-shadow: 0 1px 5px 2px rgba(208, 220, 247, 0.73);
}
</style>
