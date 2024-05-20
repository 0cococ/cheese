**[English Version](README.md)**
# cheese
cheese是一个自动化测试引擎，可以在Android 7.0及以上版本上无需root即可运行自动化脚本。 脚本是用 JavaScript 编写的，并且可以编写插件来扩展功能。

## [文档](https://github.com/0cococ/cheese-docs)

### 注意：本库的原创内容基于 GPL-3.0 开源许可证发布。对于其他第三方文件或内容，请遵循各自仓库的协议许可。

**[查看本项目使用的开源库信息](DEPEND.md)**

> The only people who have anything to fear from free software are those whose products are worth even less.
>
> <p align="right">——David Emery</p>

## 作者的随意想法

熟悉hook、自动化、逆向工程、后端吗？ 欢迎联系我进行技术讨论~

## 联络我

- QQ群：710985269

## 支持的

目前兼容Android 7.0至13.0版本，并将跟进未来的新系统。

## 如何使用

### 步骤 1. 初始化：

参考示例中MainActivity的初始化工作流程。

### 步骤2. 运行Js代码：

```kotlin
val node = node()
node.createNodeRuntime()
node.run('console.log("Hello Cheese!")')
```

## 如何贡献？

- [用户请求，有时间的话可以帮忙修复](https://docs.qq.com/smartsheet/DVUxWaUhuc2l0UGF4?tab=BB08J2&viewId=vUQPXH)

### 应用程序分为多个模块

- app模块：用户操作和UI模块
- 核心模块：该模块是负责整个Cheese调度的核心模块。

如果您想参与开发，只需提交拉取请求（PR）即可。 相关教程请百度搜索或参考 [如何在GitHub上发起你的第一个Pull Request](https://chinese.freecodecamp.org/news/how-to-make-your-first-pull-request-on-github/)。

## PR指南

1. 中英文解释都可以，但一定要详细解释问题。
2. 请遵循原项目的代码风格和设计模式，请勿个性化。
3. 无论大小，都欢迎 PR，如果有任何问题，请随时提交。

### 许可

> ```
> GPL-3.0 许可证
> ```

> 版权所有 (c) 2024 coco