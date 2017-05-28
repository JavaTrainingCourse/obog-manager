# Java研修 Go研修 OB/OG会 会員管理システム

OB/OG会の参加登録を受け付けるシステムです。

## 本番環境

https://java-obog.aomikan.org/

([VULTR VPS](http://www.vultr.com/?ref=7053029) 東京リージョン + [Microsoft Azure](https://azure.microsoft.com/ja-jp/) 西日本リージョンの冗長構成で運用中)

## 使っているもの

### フレームワーク

* Spring Boot 1.5
* Vaadin Framework 8.0

### データベース

* MySQL 5.6 (本番)
* HSQLDB (開発)

## 開発環境構築

用意するもの:

* Java SE Development Kit 8
* IntelliJ IDEA Ultimate (要購入)

Lombok Plugin の導入:

1. Settings -> Plugins -> Browse repositories... -> 🔍 "Lombok Plugin" -> Install
2. Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors -> ☑ Enable annotation processing
3. IntelliJ 再起動

## ライセンス

OBOGManager のソースコードは [Apache License 2.0](LICENSE) です。
