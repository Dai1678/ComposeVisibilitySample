# ComposeVisibilitySample

Jetpack Compose の `onVisibilityChanged` API の使用方法を示すサンプルアプリケーションです。Kotlin Multiplatform (Android + iOS) で実装されており、画像リストの可視性検知とログ出力機能を提供します。

## 📱 概要

このアプリケーションは以下の機能を実装しています：

- **画像リスト表示**: [Lorem Picsum API](https://picsum.photos) から取得した画像を LazyColumn で表示
- **可視性検知**: `Modifier.onVisibilityChanged` を使用して、各画像アイテムの50%が1秒以上表示された際にログを記録
- **クロスプラットフォーム**: Android と iOS で同一のビジネスロジックとUIを共有

## 🏗️ アーキテクチャ

本プロジェクトは Android の推奨アーキテクチャに準拠し、以下のレイヤー構成を採用しています：

- **UI Layer**: Composables, ViewModel (StateFlow)
- **Domain Layer**: Use Cases, Domain Models
- **Data Layer**: Repository, RemoteDataSource (Ktor Client)

詳細は [アーキテクチャ設計仕様書](./docs/architecture.md) を参照してください。

## 📋 ドキュメント

- [要求仕様書](./docs/requirements.md) - 機能要件、API仕様、ログ出力仕様
- [アーキテクチャ設計仕様書](./docs/architecture.md) - レイヤー構成、技術スタック、テスト戦略

## 🛠️ 技術スタック

- **言語**: Kotlin
- **UI**: Compose Multiplatform
- **HTTP Client**: Ktor Client
- **画像読み込み**: Coil3
- **DI**: Koin
- **ロギング**: Napier
- **テスト**: JUnit, Kotlin Test, Kotlinx Coroutines Test

## 🚀 セットアップ

### 前提条件

- JDK 11以上
- Android Studio (最新安定版推奨)
- Xcode (iOS開発の場合)

### プロジェクトのクローン

```bash
git clone <repository-url>
cd ComposeVisibilitySample
```

## 📦 ビルドと実行

### Android

#### IDE から実行
Android Studio の Run ボタンから `composeApp` を選択して実行

### iOS

#### IDE から実行
Android Studio の Run ボタンから `iosApp` を選択して実行

#### Xcode から実行
1. `/iosApp` ディレクトリを Xcode で開く
2. ターゲットデバイスを選択
3. Run ボタンをクリック

## 🧪 テスト

### 全テストの実行

```bash
# Unit Tests
./gradlew :composeApp:testDebugUnitTest

# テスト構成
# - commonTest: Domain/Data層
# - androidUnitTest: ViewModel層
```

### テストカバレッジ

- **目標**: 80%以上

## 📁 プロジェクト構成

```
ComposeVisibilitySample/
├── composeApp/           # 共通コード & プラットフォーム固有コード
│   └── src/
│       ├── commonMain/   # 共通ビジネスロジック・UI
│       ├── commonTest/   # 共通テスト
│       ├── androidMain/  # Android固有コード
│       ├── androidUnitTest/ # Android Unit Tests
│       └── iosMain/      # iOS固有コード
├── iosApp/              # iOSアプリエントリーポイント
└── docs/                # ドキュメント
    ├── requirements.md
    └── architecture.md
```

詳細なパッケージ構成は [アーキテクチャ設計仕様書](./docs/architecture.md#5-モジュールパッケージ構成) を参照してください。

## 📊 可視性ログ出力

各画像アイテムが50%以上1秒間表示されると、以下の形式でログが出力されます：

```json
{"id":"0","position":1,"time":"1234567890.123456"}
```

- **id**: 画像ID
- **position**: リスト内のインデックス（1始まり）
- **time**: タイムスタンプ（「秒.マイクロ秒」形式）

ログタグ: `[VisibilityLog]`

## 🔧 開発

### 依存関係の更新

```bash
./gradlew :composeApp:dependencies
```

### コードフォーマット

Kotlin公式コーディング規約に準拠してください。

## 📚 参考資料

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Modifier.onVisibilityChanged](https://developer.android.com/reference/kotlin/androidx/compose/ui/layout/package-summary#(androidx.compose.ui.Modifier).onVisibilityChanged)
- [Ktor Client](https://ktor.io/docs/client.html)
- [Coil3](https://coil-kt.github.io/coil/)
- [Napier](https://github.com/AAkira/Napier)

---

**Note**: このプロジェクトは `onVisibilityChanged` API のデモンストレーションを目的としており、以下の機能は実装していません：
- ページング（無限スクロール）
- オフライン対応
- 画像詳細画面
- お気に入り機能