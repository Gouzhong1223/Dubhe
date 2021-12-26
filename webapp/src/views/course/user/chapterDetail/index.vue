<template>
  <div v-if="data.courseChapter" v-loading="isLoading" class="chapter-detail">
    <!-- ppt -->
    <template v-if="data.courseChapter.chapterType === 2"
      ><iframe
        width="100%"
        class="viewer"
        :src="
          `https://view.officeapps.live.com/op/view.aspx?src=${data.fileUrl}`
        "
    /></template>
    <!-- 视频 -->
    <template v-else-if="data.courseChapter.chapterType === 1">
      <div class="viewer">
        <video-player :options="playerOptions" :src="data.fileUrl" />
      </div>
    </template>
    <!-- pdf -->
    <template v-else>
      <embed
        class="viewer"
        :src="data.fileUrl"
        type="application/pdf"
        width="100%"
        height="100%"
      />
    </template>
  </div>
</template>

<script>
// require styles
// eslint-disable-next-line import/no-extraneous-dependencies
import 'video.js/dist/video-js.css'

import { videoPlayer } from 'vue-video-player'
import service from '@/api/course'

export default {
  components: {
    videoPlayer,
  },
  data() {
    return {
      isLoading: false,
      data: {},
      playerOptions: {
        width: '100%',
        height: '100%',
        autoplay: false,
        muted: true,
        language: 'zh-cn',
        playbackRates: [0.75, 1.0, 1.25, 1.5, 1.75, 2.0],
        sources: '',
      },
    }
  },
  created() {
    this.initData()
  },
  methods: {
    async initData() {
      this.isLoading = true
      const data = await service.chapter.detail(
        this.$route.query.courseId,
        this.$route.query.chapterId,
      )
      this.data = data
      this.playerOptions.sources = data.fileUrl
      this.isLoading = false
    },
  },
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.chapter-detail {
  .viewer {
    width: 100%;
    height: 100vh;
    margin-bottom: 30px;
  }

  ::v-deep.video-js {
    width: 100%;
    height: 100vh;
  }

  ::v-deep.vjs-big-play-button {
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
  }
}
</style>
