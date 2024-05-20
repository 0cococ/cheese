**[中文版](README_CN.md)**
# cheese
cheese is an automation testing engine that can run automation scripts without root on Android 7.0 and above. Scripts are written in JavaScript, and plugins can be written to extend functionality.

## [Docs](https://github.com/0cococ/cheese-docs)

### Note: The original content of this repository is released under the GPL-3.0 open-source license. For other third-party files or content, please adhere to the respective repository's license agreement.

**[Check the open source library information used in this project](DEPEND.md)**

> The only people who have anything to fear from free software are those whose products are worth even less.
>
> <p align="right">——David Emery</p>

## Author's Random Thoughts

Familiar with hooking, automation, reverse engineering, backend? Feel free to reach out to me for technical discussions~

## Contact Me

- QQ Group: 710985269

## Supported

Currently compatible with Android versions 7.0 to 13.0, and will follow up with future new systems.

## How to Use

### Step 1. Initialization:

Refer to the initialization workflow of MainActivity in the example.

### Step 2. Run Js code:

```kotlin
val node = node()
node.createNodeRuntime()
node.run('console.log("Hello Cheese!")')
```

## How to Contribute?

- [User requests, if you have time, you can help fix them](https://docs.qq.com/smartsheet/DVUxWaUhuc2l0UGF4?tab=BB08J2&viewId=vUQPXH)

### The application is divided into multiple modules

- app module: user operations and UI module
- core module: This module is the core module responsible for the entire Cheese scheduling.

If you want to participate in development, just submit a pull request (PR). For related tutorials, please search on Baidu or refer to [How to Make Your First Pull Request on GitHub](https://chinese.freecodecamp.org/news/how-to-make-your-first-pull-request-on-github/).

### PR Guidelines

1. Both Chinese and English explanations are acceptable, but be sure to explain the problem in detail.
2. Please adhere to the original project's code style and design patterns do not personalize.
3. PRs are welcome regardless of size, and feel free to submit them if you have any issues.

### License

> ```
> GPL-3.0 license
> ```

> Copyright (c) 2024 coco
