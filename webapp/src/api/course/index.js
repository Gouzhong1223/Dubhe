import request from '@/utils/request'

const baseURL = 'http://139.224.44.245:8001/api/v1'

function upload(file) {
  return request.post('data/course/file/upload', {
    baseURL,
    headers: { 'Content-Type': 'multipart/form-data' },
    data: file,
  })
}

/**
 * 课程分类
 * */
function getCourseType() {
  return request.get('data/courseType/getAllCourseTypes', { baseURL })
}

function createCourseType(courseTypeName) {
  const form = new FormData()
  form.append('courseTypeName', courseTypeName)
  return request({
    url: `data/courseType/createCourseType`,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
    },
    method: 'post',
    data: form,
    baseURL,
  })
}

function removeCourseType(id) {
  return request.delete(`data/courseType/deleteCourseType/${id}`, { baseURL })
}

function updateCourseType(id, name) {
  return request({
    url: 'data/courseType/updateCourseType',
    method: 'put',
    data: {
      courseTypeId: id,
      courseTypeName: name,
    },
    baseURL,
  })
}

/**
 * 课程管理
 */
function getCourseList() {
  return request.get('data/course/listAllCourses', { baseURL })
}

function updateCourse(data) {
  return request.put('data/course/updateCourse', { data, baseURL })
}

function removeCourse(courseId) {
  return request.delete(`data/course/updateCourse/${courseId}`, { baseURL })
}

function createCourse(data) {
  return request({
    url: 'data/course/createCourse',
    method: 'post',
    data,
    baseURL,
  })
}

/**
 * 课程章节管理
 */
// 获取所有课程章节
function getChapterList(courseId) {
  return request.get(`data/chapter/listAllCourseChapter/${courseId}`, {
    baseURL,
  })
}

// 查看(学习)章节详情
function getChapterDetail(chapterId, courseId) {
  return request.get(
    `data/chapter/studyCourseChapter/${chapterId}/${courseId}`,
    {
      baseURL,
    },
  )
}

// 更新章节信息
function updateChapter(data) {
  return request.put(`data/chapter/updateCourseChapter`, { data, baseURL })
}

function removeChapter(chapterId) {
  return request.delete(`data/chapter/deleteCourseChapter/${chapterId}`, {
    baseURL,
  })
}

function createChapter(data) {
  return request.post(`data/chapter/createCourseChapter`, { data, baseURL })
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
