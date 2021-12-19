import request from '@/utils/request'

function upload(file) {
  return request.post('/course/file/upload', {
    headers: { 'Content-Type': 'multipart/form-data' },
    data: file,
  })
}

/**
 * 课程分类
 * */
function getCourseType() {
  return request.get('/courseType/getAllCourseTypes')
}

function createCourseType(data) {
  return request({
    url: `/courseType/createCourseType`,
    method: 'post',
    data,
  })
}

function removeCourseType(id) {
  return request.delete(`/courseType/deleteCourseType/${id}`)
}

function updateCourseType(id, name) {
  return request({
    url: '/courseType/updateCourseType',
    method: 'put',
    data: {
      courseTypeId: id,
      courseTypeName: name,
    },
  })
}

/**
 * 课程管理
 */
function getCourseList() {
  return request.get('/course/listAllCourses')
}

function updateCourse(data) {
  return request.put('/course/updateCourse', { data })
}

function removeCourse(courseId) {
  return request.delete(`/course/updateCourse/${courseId}`)
}

function createCourse(data) {
  return request.post('/course/createCourse', { data })
}

/**
 * 课程章节管理
 */
// 获取所有课程章节
function getChapterList(courseId) {
  return request.get(`/chapter/listAllCourseChapter/${courseId}`)
}

// 查看(学习)章节详情
function getChapterDetail(chapterId, courseId) {
  return request.get(`/chapter/studyCourseChapter/${chapterId}/${courseId}`)
}

// 更新章节信息
function updateChapter(data) {
  return request.put(`/chapter/updateCourseChapter`, { data })
}

function removeChapter(chapterId) {
  return request.delete(`/chapter/deleteCourseChapter/${chapterId}`)
}

function createChapter(data) {
  return request.post(`/chapter/createCourseChapter`, { data })
}

export default {
  upload,
  courseType: {
    get: getCourseType,
    remove: removeCourseType,
    update: updateCourseType,
    create: createCourseType,
  },
  course: {
    get: getCourseList,
    remove: removeCourse,
    update: updateCourse,
    create: createCourse,
  },
  chapter: {
    get: getChapterList,
    detail: getChapterDetail,
    remove: removeChapter,
    update: updateChapter,
    create: createChapter,
  },
}
