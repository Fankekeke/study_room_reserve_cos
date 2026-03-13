### 基于SpringBoot + Vue的自习室预约系统.

###### 管理员：
公告信息 、信用积分记录 、房间管理 、房间类型 、装修公司 、消息通知 、支付记录 、房间装修 、租借订单 、房间保洁 、员工管理 、用户管理 、数据统计。

###### 用户：
账户注册登录、密码修改、个人信息 、我的消息 、我的订单 、支付记录 、信用积分 、在线支付 、订单评价 、订单评价。

##### 空间与资源配置
###### 房间/类型管理： 灵活定义自习室规格与功能分区，实时维护各房间的开放状态与配置。

###### 房间装修/公司： 记录空间维护与装修进度，对接专业公司确保学习环境的持续优化。

###### 房间保洁： 建立标准化清洁流程，实时追踪保洁状态，为用户提供卫生整洁的自习空间。

##### 预约租赁与交易体系
###### 租借订单/我的： 提供便捷的选座预约与订单追踪，实现从下单到使用的全流程闭环管理。

###### 在线支付/记录： 整合安全便捷的支付入口，自动生成财务流水，方便用户与管理员核对账目。

###### 订单评价： 收集用户对环境及服务的真实反馈，作为提升运营质量与管理水平的重要依据。

##### 用户信用与激励机制
###### 信用积分/记录： 建立用户行为信用档案，通过积分奖惩机制规范自习行为，减少违约占用。

###### 用户/员工管理： 统一管控会员信息与工作人员权限，支撑平台日常运营与高效的服务保障。

##### 信息交互与智能提醒
###### 公告信息/通知： 实时发布自习室规定与活动资讯，确保重要通知能够精准触达每一位用户。

###### 我的消息/通知： 聚合预约成功、费用变动等系统提醒，通过多端通知保障信息传递的实时性。

##### 运营分析与辅助决策
###### 数据统计： 自动汇总房间周转率、营收趋势及信用分布，通过数据驱动自习室的科学经营。

#### 安装环境

JAVA 环境 

Node.js环境 [https://nodejs.org/en/] 选择14.17

Yarn 打开cmd， 输入npm install -g yarn !!!必须安装完毕nodejs

Mysql 数据库 [https://blog.csdn.net/qq_40303031/article/details/88935262] 一定要把账户和密码记住

redis

Idea 编译器 [https://blog.csdn.net/weixin_44505194/article/details/104452880]

WebStorm OR VScode 编译器 [https://www.jianshu.com/p/d63b5bae9dff]

#### 采用技术及功能

后端：SpringBoot、MybatisPlus、MySQL、Redis、
前端：Vue、Apex、Antd、Axios

平台前端：vue(框架) + vuex(全局缓存) + rue-router(路由) + axios(请求插件) + apex(图表)  + antd-ui(ui组件)

平台后台：springboot(框架) + redis(缓存中间件) + shiro(权限中间件) + mybatisplus(orm) + restful风格接口 + mysql(数据库)

开发环境：windows10 or windows7 ， vscode or webstorm ， idea + lambok


#### 前台启动方式
安装所需文件 yarn install 
运行 yarn run dev

#### 默认后台账户密码
[管理员]
admin
1234qwer

#### 项目截图
暂无

|  |  |
|---------------------|---------------------|
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/6d848aa3-3c8b-4151-b816-f11d1bfdd7ea.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/dc2110bb-fa2d-4203-bf71-3bc45e7f9f67.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/4d2495f1-a4d4-4ce8-94d3-c1d92f20e80a.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/b1985af4-00de-4241-853f-4acbb2e691e7.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/3fef8828-550f-4a85-ba8a-cbaafcbe98c1.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/b2adcbba-7235-4a37-96fa-71301b2fbbde.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/0b8e1dde-c965-437e-826c-4f5ba19ce96b.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/28651cc6-bf47-4792-86b8-de323952dbd0.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/ec08215d-b924-458a-84a4-5071fb6ab3ae.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/21609b91-bcf6-40d9-a396-0ed6d53e95f6.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/eafd5a0a-33f9-4195-a995-fe7f305c7e20.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/771ca0b7-06e4-4c3c-a312-e627c607ab95.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/e4973487-7ca1-43bd-889d-1fda83b9e6e2.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/699c0eaf-07f5-4dc8-a8e1-9bc77aac0d59.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/e18e83d0-aa3a-47bb-afc7-3f8a522fa97c.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/24f2e7f1-4bb5-4c0d-9e97-435dd708af88.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/e6db588b-9332-46ee-a158-e8f39e2e6a1e.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/22aa4e8a-9a4f-4ca1-a652-e659ab58ad7a.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/e3e4b7ba-a764-41d0-8644-8e6a9ea26dff.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/18e7e41f-461e-44f0-bcc1-6ec18f614d2e.png) |

![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/work/936e9baf53eb9a217af4f89c616dc19.png)

#### 演示视频

暂无

#### 获取方式

Email: fan1ke2ke@gmail.com

WeChat: `Storm_Berserker`

`附带部署与讲解服务，因为要恰饭资源非免费，伸手党勿扰，谢谢理解😭`

> 1.项目纯原创，不做二手贩子 2.一次购买终身有效 3.项目讲解持续到答辩结束 4.非常负责的答辩指导 5.**黑奴价格**

> 项目部署调试不好包退！功能逻辑没讲明白包退！

#### 其它资源

[2025年-答辩顺利通过-客户评价🍜](https://berserker287.github.io/2025/06/18/2025%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2024年-答辩顺利通过-客户评价👻](https://berserker287.github.io/2024/06/06/2024%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2023年-答辩顺利通过-客户评价🐢](https://berserker287.github.io/2023/06/14/2023%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2022年-答辩通过率100%-客户评价🐣](https://berserker287.github.io/2022/05/25/%E9%A1%B9%E7%9B%AE%E4%BA%A4%E6%98%93%E8%AE%B0%E5%BD%95/)

[毕业答辩导师提问的高频问题](https://berserker287.github.io/2023/06/13/%E6%AF%95%E4%B8%9A%E7%AD%94%E8%BE%A9%E5%AF%BC%E5%B8%88%E6%8F%90%E9%97%AE%E7%9A%84%E9%AB%98%E9%A2%91%E9%97%AE%E9%A2%98/)

[50个高频答辩问题-技术篇](https://berserker287.github.io/2023/06/13/50%E4%B8%AA%E9%AB%98%E9%A2%91%E7%AD%94%E8%BE%A9%E9%97%AE%E9%A2%98-%E6%8A%80%E6%9C%AF%E7%AF%87/)

[计算机毕设答辩时都会问到哪些问题？](https://www.zhihu.com/question/31020988)

[计算机专业毕业答辩小tips](https://zhuanlan.zhihu.com/p/145911029)

#### 接JAVAWEB毕设，纯原创，价格公道，诚信第一

`网站建设、小程序、H5、APP、各种系统 选题+开题报告+任务书+程序定制+安装调试+项目讲解+论文+答辩PPT`

More info: [悲伤的橘子树](https://berserker287.github.io/)

<p><img align="center" src="https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/%E5%90%88%E4%BD%9C%E7%89%A9%E6%96%99%E6%A0%B7%E5%BC%8F%20(3).png" alt="fankekeke" /></p>
