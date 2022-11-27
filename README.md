# Java研修 Go研修 OB/OG会 会員管理システム

[![Build Status](https://travis-ci.org/JavaTrainingCourse/obog-manager.svg?branch=master)](https://travis-ci.org/JavaTrainingCourse/obog-manager)

OB/OG会の参加登録を受け付けるシステムです。

## 環境

[java-obog.azurewebsites.net](https://java-obog.azurewebsites.net/) (Azure App Service) で稼働しています。

※ 2022/11/28 Heroku から移行しました

## 使っているもの

### フレームワーク

* Spring Boot 1.5
* Vaadin Framework 8.3

### データベース

* PostgreSQL (ElephantSQL)
* HSQLDB (開発)

## 開発環境構築

用意するもの:

* Java SE Development Kit 11
* IntelliJ IDEA
* Azure CLI

Azure App Service デプロイ方法:

```
./gradlew build
az webapp deploy --resource-group java-obog_group --name java-obog --type jar --src-path build/libs/obog-manager.jar
```

## ライセンス

OBOGManager のソースコードは [Apache License 2.0](LICENSE) です。
