<template>
  <div v-loading="isLoading" class="course-study">
    <template v-if="courseData.length > 0">
      <div
        v-for="courseType in courseData"
        :key="courseType.courseTypeId"
        class="course-list"
      >
        <template>
          <h3 class="category-title">
            <i class="el-icon-caret-right" style="color:#4E90F8"></i
            >{{ courseType.courseTypeName }}
          </h3>
          <div
            v-for="course in courseType.courses"
            :key="course.courseId"
            class="course-detail"
          >
            <template>
              <img :src="course.fileUrl" />
              <div class="course-content">
                <div class="title">{{ course.courseName }}</div>

                <el-tooltip
                  effect="dark"
                  :content="course.introduction"
                  placement="top"
                >
                  <p class="introduce">
                    {{ course.introduction }}
                  </p>
                </el-tooltip>
                <div class="schedule">
                  <div>总计:{{ course.totalChapter }}</div>
                  |
                  <div>已完成:{{ course.finishChapter }}章节</div>
                </div>
                <div class="progress">
                  <div>学习进度:</div>
                  <el-progress
                    style="width:200px;"
                    :text-inside="true"
                    :stroke-width="15"
                    :percentage="course.schedule"
                  ></el-progress>
                </div>
                <el-button
                  class="start-learn"
                  type="primary"
                  :icon="
                    course.done === 1
                      ? 'el-icon-circle-check'
                      : 'el-icon-loading'
                  "
                  @click="
                    handleToCourseDetail(
                      courseType.courseTypeId,
                      course.courseId,
                    )
                  "
                  >{{ course.done === 1 ? '学习完成' : '开始学习' }}</el-button
                >
              </div>
            </template>
          </div>
        </template>
      </div>
    </template>
    <div v-else class="course-list">暂无数据</div>
  </div>
</template>

<script>
import service from '@/api/course'

export default {
  data() {
    return {
      isLoading: false,
      courseData: [],
    }
  },
  created() {
    this.initData()
  },
  methods: {
    async initData() {
      this.isLoading = true
      const course = await service.course.get()
      this.courseData = course.filter((item) => item.courses.length > 0)
      this.isLoading = false
    },

    handleToCourseDetail(courseTypeId, courseId) {
      this.$router.push({
        path: '/course/courseDetail',
        query: {
          courseTypeId,
          courseId,
        },
      })
    },
  },
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.course-study {
  height: 100%;
  padding: 20px;
  margin-bottom: 30px;
  .course-list {
    background-color: #fff;
    border-radius: 10px;
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    padding: 20px;
    margin: 30px 0;
    box-shadow: 0 1px 3px 2px rgba(189, 205, 243, 0.32);
    .category-title {
      width: 100%;
    }
    .course-detail {
      min-width: 380px;
      margin: 30px 60px;
      width: 40%;
      height: 200px;
      display: flex;
      flex-direction: row;
      position: relative;

      img {
        width: 200px;
        height: 200px;
        border-radius: 10px;
      }

      .course-content {
        font-size: 14px;
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        width: calc(100% - 200px);
        overflow: hidden;
        padding: 10px 0 10px 10px;
        .title {
          font-size: 18px;
          color: #1c1f21;
        }
        .introduce {
          color: #545c63;
          height: 48px;
          word-break: break-word;
          line-height: 16px;
          text-align: left;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 3;
          overflow: hidden;
          -webkit-box-orient: vertical;
        }

        .schedule {
          display: flex;
          flex-direction: row;
        }

        .progress {
          margin: 10px 0;
          display: flex;
          flex-direction: row;
        }

        .start-learn {
          position: absolute;
          bottom: 0;
          left: 210px;
        }
      }
    }
  }
}
</style>
