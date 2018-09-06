## 项目缘由
最近研究做爬虫，顺手想做一个小项目，发到码农翻身的星球，就有了这个项目

## 项目描述：
抓取goal.com比赛信息,最后生成一个结构化的可查询的数据库（mysql或者openfootball）。类似 http://www.goal.com/en-us/match/chievo-v-juventus/commentary-result/2a5cko82ghpj0p5odxxxvj4ve

目前进度：完成爬虫部分的技术验证，准备爬比赛数据。后续需要人继续爬其他数据，以及处理爬虫生成的数据（例如合并球员数据）。

最终目标：爬虫完成之后，把数据公开出去。

## 技术选型
爬虫用的是Java的一个框架，webmagic。原因：Star数挺高，文档很丰富，作者似乎对HttpClient研究颇深。

- 坑一：maven仓库的版本对https支持有问题，需要自己从github上自己mvn install
- 坑二：文档示例，因为Github首页改版，调不通
- 坑三：自己封装的xsoup（可能是为了无缝切换内部引擎，自己封装了一套API）不好用，例如没有获取attr的方法

## 运行方法
- 本地Install https://github.com/code4craft/webmagic
- 可能需要安装Redis
- 打包方法：`mvn package spring-boot:repackage`
