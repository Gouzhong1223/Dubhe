<p align="center">
  <a href="http://tianshu.org.cn">
    <img width="200" src="http://tianshu.org.cn/template/default/assets/img/logo4.png">
  </a>
</p>

<h1 align="center">天枢一站式AI模型开发平台</h1>

**天枢人工智能开源开放平台**（简称：**天枢平台**）是天枢平台由之江实验室牵头，联合北京一流科技、中国信通院和浙江大学共同自研的人工智能开源平台。整个平台由一站式AI模型开发平台、高性能深度学习框架和模型炼知框架三大子系统组成。

其中， **一站式AI模型开发平台面**（简称：**一站式开发平台**）面向AI模型生产的生命周期，提供了包括数据处理、模型开发、模型训练和模型管理等功能，方便用户一站式构建AI算法。

## 平台优势

* **一站式开发**：为用户提供一站式深度学习开发功能，通过智能数据处理、便利的模型开发和模型训练，打通深度学习全链路；

* **集成先进算法**：除了囊括常规AI算法外，之江天枢还集成了多领域的独家算法，提供业界领先性能。

* **灵活易用**：除了一站式深度学习开发平台，亦提供可视化和动静结合编码方式，调试灵活，小白亦可快速上手。

* **性能优越**：集成自主研发的分布式训练平台，提供高性能的分布式计算体验，节省训练成本和训练时间。

## 页面预览
![概览](/docs/images/dubhe_dashboard.png "概览")


## 功能列表
<table>
    <tbody>
        <tr>
            <td rowspan="12">一站式开发平台</td>
        </tr>
        <tr>
            <td>数据管理</td>
            <td>数据集管理</td>
        </tr>
        <tr>
            <td rowspan="2">模型开发</td>
            <td>Notebook</td>
        </tr>
        <tr>
            <td>算法管理</td>
        </tr>
        <tr>
            <td rowspan="2">训练管理</td>
            <td>镜像管理</td>
        </tr>
        <tr>
            <td>训练任务</td>
        </tr>
        <tr>
            <td>模型管理</td>
            <td>模型列表</td>
        </tr>
        <tr>
            <td rowspan="5">控制台</td>
            <td>用户管理</td>
        </tr>
        <tr>
            <td>角色管理</td>
        </tr>
        <tr>
            <td>菜单管理</td>
        </tr>
        <tr>
            <td>字典管理</td>
        </tr>
        <tr>
            <td>集群状态</td>
        </tr>
    </tbody>
</table>

## 技术架构
![技术架构](./docs/images/tech-arc.jpg "技术架构")

## 技术栈
- 后端: [Spring Boot](https://spring.io/projects/spring-boot)
- 前端: [Vue.js](https://vuejs.org/), [Element](https://element.eleme.cn/)
- 数据处理 [Yolo](https://pjreddie.com/darknet/yolo/) ...
- 可视化: [Django](https://www.djangoproject.com/) ...
- 中间件: [MySQL](https://www.mysql.com/), [MyBatis-Plus](https://mp.baomidou.com/), [Redis](https://redis.io/)
- 基础设施: [Docker](https://www.docker.com/), [Kubernetes](https://kubernetes.io/)

## 目录结构

```
├── dubhe_data_process    数据处理服务
├── dubhe-server          后端服务 
├── dubhe-visual-server   可视化服务 
├── webapp                前端服务 
```

## 反馈问题

- [在线社区](http://www.aiiaos.cn/index.php?s=/forum/index/forum/id/45.html)
- 钉钉交流群

<a href="./docs/images/dingtalk.jpg"><img src="http://cdn.qjycloud.com/dingtalk.jpg" width="320" /></a> 


## 许可证书
本项目的发布受[Apache 2.0 license](./LICENSE)许可认证。
