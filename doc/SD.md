[TOC]



























## 1. 总体设计

一个基于地理位置的新式漂流瓶应用。

曾经风靡一时的漂流瓶应用，微信、qq、yy语音都推出过漂流瓶功能，但是他们却很少与地理位置画上关系，所以这就是我们产生这个想法的源点。

我们的应用是一个典型的C/S架构的系统，客户端与服务端通过http协议进行交流



## 2 技术选型及理由

### 前端技术选型

**我们前端选择的是安卓客户端，理由如下：**

* 安卓使用人群较多，开发安卓版本推广成本较低
* 安卓对于开发门槛较低，不像IOS版本对于开发机的要求比较严格，发布难度也比较大
* 安卓开发的生态比较好，全球有大量的自由开发者，遇到问题有比较多的解决方案

### 后端技术选型

* nodejs：
  * 便于编写异步高性能服务器
  * 简化对json数据格式的处理
  * 活跃的npm社区，成熟的web开发生态
* mysql：
  * 业界关系型数据库成熟解决方案
* redis：
  * 高速的内存型数据库，用于保存用户的session信息
  * 为每个api必经的session处理中间件提供低时延响应
  * 为多进程架构提供集中的session存储区



## 3 架构设计

![](https://ws1.sinaimg.cn/large/006tKfTcgy1ft2h0j5u91j30gs07ejuk.jpg)



## 4 用例设计

![](https://ws1.sinaimg.cn/large/006tKfTcgy1ft2h112c7ij30gw0c9jtz.jpg)



## 5 领域建模

![](https://ws2.sinaimg.cn/large/006tKfTcgy1ft2h1immg8j309q08mjrf.jpg)





## 6 数据库设计

![](https://ws3.sinaimg.cn/large/006tKfTcgy1ft2h2ehypmj30e60h0wfy.jpg)





## 7 API设计

**遵循RESTful规范进行API设计，使用blueprint编写API文档，利用aglio工具渲染出html，并通过nginx代理此静态文件，使得前端开发人员只需访问https://bottle.resetbypear.com/ 便可方便地浏览到最新的API文档。**

![](https://ws1.sinaimg.cn/large/006tKfTcgy1ft2h35iirij30fv0bbjsy.jpg)

## 8 前端模块划分

### 8.1 验证模块

**该模块实现的功能为用户登录与用户注册，设计界面使用viewPager适配，以实现登录和注册功能的切换的滑动效果；并使用okhttp3+retrofit实现与后台数据库之间的交互**



**使用viewPager适配**

![](https://ws4.sinaimg.cn/large/006tKfTcgy1ft2h48ew4gj30gd0dcn0n.jpg)

**定义Interface**

![](https://ws1.sinaimg.cn/large/006tKfTcgy1ft2h4ybyy9j30gv08xac7.jpg)

**请求时，将数据模型转位JSON并放入RequestBody中，或直接放入URL中：** 

![](https://ws4.sinaimg.cn/large/006tKfTcgy1ft2h5dzfbcj30gv02d0t7.jpg)

**发起一个请求，定义完成和订阅的线程，创建一个订阅者实现请求完成后的逻辑：** 

![](https://ws3.sinaimg.cn/large/006tKfTcgy1ft2h5uzvhkj30gu02ogml.jpg)

**登录注册部分在页面设计上使用了TabLayout和自定义ViewPager的方式实现了页面切换的滑动效果，使用户体验更加友好。**

![](https://ws1.sinaimg.cn/large/006tKfTcgy1ft2h6nb9vpj30fn06awez.jpg)

**登录和注册页面是分开两个xml设计，分别为load.xml和register.xml，然后将相应的xml放入对应的arraylist内以实现配置。配置自定义的ViewPager：** 

![](https://ws1.sinaimg.cn/large/006tKfTcgy1ft2h72ovhxj30gy0clwhe.jpg)

**之后是配置相关按钮的监听器。这是登录按钮的监听器** 

![](https://ws2.sinaimg.cn/large/006tKfTcgy1ft2h7hmhhnj30h0069765.jpg)

**完善了登录功能的健壮性，确保用户能正确输入相关信息并登陆成功。实现了判断是否输入用户名或密码、是否密码错误等的信息提示。登陆成功后，发送相关网络请求，通过retrofit和okhttp的结合，以及observable观察订阅模式，实现对后台发送GET和POST请求，并post session，以及保存cookie，以实现用户登录的持久性。代码如下** 

![](https://ws2.sinaimg.cn/large/006tKfTcgy1ft2h7xik8mj30gu07pdha.jpg)

**注册页面的功能与登录功能类似，实现了相关错误信息输入的判断。代码如下：**﻿ 

![](https://ws4.sinaimg.cn/large/006tKfTcgy1ft2h8ajv2dj30gx0cidjp.jpg)

**其中在最后优化阶段完善的一点是：当注册成功的时候，也会直接跳转到主页面，而不是需要用户再次登录，这点改变了原有的用户体验不良好的效果。** 

![](https://ws3.sinaimg.cn/large/006tKfTcgy1ft2h8m0iqvj30gv0bwdj2.jpg)



### 8.2 地图模块

**此模块负责以下工作：	**

1. 显示地图	
2. 获取附近的漂流瓶并显示在地图上
3. 打开地图上的漂流瓶
4. 使用一定的策略平衡体验和电量之间的矛盾

![bottle-ae/app/src/main/java/com/pear/bottle_ae/MapActivity.java](https://ws3.sinaimg.cn/large/006tKfTcgy1ft2h9pcqmhj30gx0b50vg.jpg)

**根据当前位置获取附近的漂流瓶，并使用高德地图sdk将瓶子标注在地图上**

![**bottle-ae/app/src/main/java/com/pear/bottle_ae/MapActivity.java**](https://ws3.sinaimg.cn/large/006tKfTcgy1ft2haiecc7j30gr0getde.jpg)

**监听用户点击地图上瓶子的动作，并判断能否打开（用户不能打开一个过远的瓶子），若点击的是一个可是打开的瓶子，则向服务器发出请求，表达自己已经打开了这个瓶子，服务端返回该瓶子的相关信息。** 

![](https://ws4.sinaimg.cn/large/006tKfTcgy1ft2hb0zlr4j30fe06bmyv.jpg)

![](https://ws2.sinaimg.cn/large/006tKfTcgy1ft2hbbq9wcj30gw052wfn.jpg)

### 8.3 基础模块构建

**这里的基础模块主要指网络请求模块，因为我们定义使用json进行前后端交互，所以序列化/反序列化是一个经常被复用的模块，故我们构建了一个这样的模块作为基础模块。**

![](https://ws3.sinaimg.cn/large/006tKfTcgy1ft2hctlhpgj30gv09376b.jpg)

(bottle-ae/app/src/main/java/com/pear/bottle_ae/PostBodyHelper.java)



## 9 后端模块划分

### 9.1 后端目录结构

```
├── README.md
├── database
│   └── bottle-ddl.sql
├── deploy
│   └── prod.json
├── docs
│   ├── api.html
│   └── api.md // RESTful API文档
├── package-lock.json
├── package.json // 依赖包
└── src
    ├── config
    │   ├── env // 环境配置
    │   │   ├── development.js // 开发环境配置文件
    │   │   └── production.example.js // 生产环境配置文件
    │   └── index.js
    ├── controllers // 控制器
    │   ├── bottle.js
    │   ├── session.js
    │   └── user.js
    ├── index.js
    ├── models // 数据持久化层
    │   ├── bottle.js
    │   └── user.js
    ├── routers // 路由层
    │   ├── bottle.js
    │   ├── index.js
    │   ├── session.js
    │   └── user.js
    ├── services // service
    │   ├── db // mysql
    │   │   └── index.js
    │   └── redis // cache
    │       ├── ClientManager.js
    │       ├── index.js
    │       ├── koa-redis-store.js
    │       └── session.js
    └── utils // 基础模块
        ├── AppError.js // 异常
        ├── index.js
        └── logger.js // logger

```



### 9.2 Logger

**logger负责请求日志和出错日志的记录**

![](https://ws3.sinaimg.cn/large/006tKfTcgy1ft2hfmg99xj30go0crn06.jpg)

(src/utils/logger.js)

### 9.3 AppError

**AppError模块中定义SoftError和HardError，均继承于基类AppError，各自实现捕获时的处理逻辑。SoftError返回给用户提示信息，HardError在log中打印错误栈信息，而对用户隐藏出错详情。同时，利用koa的中间件机制在合适的位置处理AppError。最后，监听app的error事件，处理相应异常以防进程因出错的用户请求而退出。** 

![](https://ws4.sinaimg.cn/large/006tKfTcgy1ft2hgbh5a9j30gp0djgoo.jpg)

**(src/utils/AppError.js)** 

![](https://ws1.sinaimg.cn/large/006tKfTcgy1ft2hh70900j30h104cdgj.jpg)

(src/utils/index.js)

### 9.4 ClientManager 

**ClientManager是一个工厂类，负责管理redis连接，可以用来使每次查询都使用同个redis连接，避免连接的频繁创建和销毁，减少资源占用**

![](https://ws3.sinaimg.cn/large/006tKfTcgy1ft2hhx80onj30g90f6jte.jpg)

**(src/services/redis/ClientManager.js)**

### 9.5 koa-redis-store

**koa-redis-store是中间件koa-redis的redis适配器**

![](https://ws3.sinaimg.cn/large/006tKfTcgy1ft2hjh227kj30e60g8tap.jpg)

**(src/services/redis/koa-redis-store.js)**



### 9.6 DB模块

**db模块负责mysql连接的创建和查询的执行，捕获mysql相关的错误并包装成HardError抛出。**

![](https://ws2.sinaimg.cn/large/006tKfTcgy1ft2hk74fukj30gt0cdacu.jpg)

**(src/services/db/index.js)** 

### 9.7 用户验证模块

**使用session-cookie机制记录用户的登录状态。全局上，通过blockUnauthorized中间件拦截未登录用户对白名单之外的api的访问** 

![](https://ws3.sinaimg.cn/large/006tKfTcgy1ft2hlhqihcj30gu039jry.jpg)

（src/routers/index.js）

**局部上，每个api会对用户权限进行必要的检查**

![](https://ws1.sinaimg.cn/large/006tKfTcgy1ft2hlz2nxvj30gt02s0ti.jpg)

(src/controllers/bottle.js)



### 9.8 瓶子管理模块

**bottle表记录瓶子的信息；bottle_open表记录瓶子的打开记录。业务逻辑上，同一用户反复打开同一瓶子，bottle_open只会记录第一条记录。对此的实现上，利用关系型数据库主键不能重复的特性，将<bottle_id, opener_id>作为bottle_open的主键，并编写以下代码，捕获主键重复的错误，保持静默** 

![](https://ws4.sinaimg.cn/large/006tKfTcgy1ft2hn67fs6j30gx056ab4.jpg)

**(src/controllers/bottle.js)**



## 10 可拓展性

* 在我们这个系统中，我们构造了大量基础模块，我们构建这些模块时均没有考虑具体的业务，目标是构造出普适的模块，方便系统扩展
* 吸收了MVC的思想，每个模块仅仅负责管理单一的实体，做到了**低耦合，高内聚**

