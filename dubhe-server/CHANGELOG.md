## 0.2.0 (2020-10-26)
### Breaking Changes

1. 支持 OneFlow、TensorFlow、PyTorch 等主流框架的多机多卡模式分布式训练
2. 基于状态机的数据集状态重构，将业务代码和状态变更完全解耦，实现零延时的状态变更响应能力
3. 添加分布式算法调度。引入任务调度，将算法和应用解耦，支持多算法程序节点并行处理任务，并基于 k8s 实现算法节点自动伸缩
4. 将标签和数据集拆分，引入「标签组」统一管理标签，实现标签可复用、可重组
5. 训练时支持将已有模型作为训练入参
6. 训练时支持区分训练数据集与验证数据集
7. 平台后端日志分流，区分用户请求日志、平台日志、定时任务日志；精简日志信息
8. 增加定时任务，异步清理垃圾数据（用户上传文件、训练/模型删除文件、大批量数据集文件）

### Features

1. 数据集图片手动标注优化。支持对标注像素级位置、大小调整，支持常见缩放、拖拽、平移等操作
2. 通用数据权限方案改造。对业务代码零侵入性的权限方案，实现基于用户，角色，资源的权限数据管理
3. 超大数据集操作流程优化。实现超大数据集（ 10w+ 文件）的前端、后端、存储全流程平滑操作
4. 支持本地已有数据集兼容。系统提供标准数据集模板，用户按照规范导入数据集文件，实现数据集全功能兼容
5. 模型开发Notebook 超时（默认 4h ）自动关闭并回收资源
6. 断点续训功能、模型下载功能、模型保存功能支持通过目录树选择模型文件/文件夹
7. 文件上传增加进度条展示
8. 训练创建页，增加运行命令预览功能；训练详情页，增加算法在线编辑跳转功能
9. 训练支持延时启动、定时停止功能
10. 训练日志、运行日志下载功能优化，避免大文件导致的浏览器卡死
11. 镜像管理功能，镜像名称支持自定义；支持镜像的删除、修改等操作；镜像上传后自动清除 docker load 镜像
12. 上传算法功能、训练创建功能、训练终止功能性能优化
13. CPU/GPU 规格类配置支持管理员控制台管理
14. 增加训练失败异常信息反馈