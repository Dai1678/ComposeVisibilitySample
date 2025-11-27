# ComposeVisibilitySample - アーキテクチャ設計仕様書

## 1. アーキテクチャ概要

本プロジェクトは、Androidの推奨アーキテクチャ（Android Architecture Guidelines）に準拠し、Kotlin Multiplatformの特性を活かした設計とします。

### 1.1 アーキテクチャ原則

以下のAndroid推奨アーキテクチャの原則に従います：

1. **関心の分離（Separation of Concerns）**: UIロジック、ビジネスロジック、データアクセスロジックを明確に分離
2. **単一方向データフロー（Unidirectional Data Flow: UDF）**: データは単一方向に流れ、イベントは逆方向に流れる
3. **単一情報源（Single Source of Truth）**: 各データ型に対して単一の情報源を持つ
4. **UIはデータモデルに駆動される**: UIはデータモデルの変更を観察し、それに応じて更新される

### 1.2 全体アーキテクチャ図

```
┌─────────────────────────────────────────────────────────┐
│                       UI Layer                          │
│  ┌─────────────────────────────────────────────────┐   │
│  │         Composable Functions                    │   │
│  │  - ImageListScreen                              │   │
│  │  - ImageCard (with onVisibilityChanged)         │   │
│  │  - ErrorView, LoadingView                       │   │
│  └─────────────────────────────────────────────────┘   │
│                        │ ▲                              │
│                        │ │ State/Events                 │
│                        ▼ │                              │
│  ┌─────────────────────────────────────────────────┐   │
│  │    ViewModel (ImageListViewModel)               │   │
│  │  - UiState management (StateFlow)               │   │
│  │  - Event handling                               │   │
│  │  - Visibility logging                           │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                         │ ▲
                         │ │
                         ▼ │
┌─────────────────────────────────────────────────────────┐
│                    Domain Layer                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │         Use Cases                               │   │
│  │  - GetImagesUseCase                             │   │
│  │  - LogImageVisibilityUseCase                    │   │
│  └─────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────┐   │
│  │         Domain Models                           │   │
│  │  - ImageItem                                    │   │
│  │  - VisibilityLog                                │   │
│  │  - MicrosecondTimestamp                         │   │
│  └─────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────┐   │
│  │         Repository Interfaces                   │   │
│  │  - ImageRepository                              │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                         │ ▲
                         │ │
                         ▼ │
┌─────────────────────────────────────────────────────────┐
│                     Data Layer                          │
│  ┌─────────────────────────────────────────────────┐   │
│  │         Repository Implementation               │   │
│  │  - ImageRepositoryImpl                          │   │
│  └─────────────────────────────────────────────────┘   │
│                        │ ▲                              │
│                        ▼ │                              │
│  ┌─────────────────────────────────────────────────┐   │
│  │         Data Sources                            │   │
│  │  - RemoteDataSource (Ktor Client)               │   │
│  │  - PicsumRemoteDataSource                       │   │
│  └─────────────────────────────────────────────────┘   │
│                        │ ▲                              │
│                        ▼ │                              │
│  ┌─────────────────────────────────────────────────┐   │
│  │         DTOs / Response Models                  │   │
│  │  - ImageResponse                                │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                         │ ▲
                         ▼ │
                 Lorem Picsum API
```

## 2. レイヤー構成

### 2.1 UI Layer

#### 2.1.1 責務
- ユーザーへのデータ表示
- ユーザーインタラクションの処理
- onVisibilityChanged APIの統合と可視性状態の管理

#### 2.1.2 コンポーネント

##### Composable Functions
- **ImageListScreen**: メイン画面コンポーザブル
  - LazyColumnによるリスト表示
  - ローディング状態、エラー状態、空状態の表示
  - PullToRefreshは実装しない

- **ImageCard**: 画像アイテムカード
  - 画像表示（Coil3のAsyncImage使用）
  - 作者名表示
  - **onVisibilityChanged API統合**
  - 可視性状態の検知（50%、1秒以上）

- **ErrorView**: エラー状態表示コンポーネント
  - 画面中央にエラーメッセージ表示
  - リトライボタン配置

- **LoadingView**: ローディング状態表示コンポーネント
  - 画面中央にCircularProgressIndicator表示

##### ViewModel
- **ImageListViewModel**
  - UIStateの管理（StateFlow）
  - Use Caseの呼び出し
  - エラーハンドリング
  - 可視性ログイベントの処理

**UiState定義**:
```kotlin
sealed interface ImageListUiState {
    data object Loading : ImageListUiState
    data class Success(val images: List<ImageItem>) : ImageListUiState
    data class Error(val message: String) : ImageListUiState
}
```

- ViewModelはAndroidとiOSで共用
- 参考: [Multiplatform ViewModel](https://developer.android.com/kotlin/multiplatform/viewmodel)

#### 2.1.3 State Management
- **StateFlow**: UI状態の保持と通知（ImageListUiState）
- **Compose State**: ローカルUI状態（可視性タイマーなど）

##### 可視性状態管理
各ImageCard内で以下を管理：
- 可視状態（Boolean）
- 可視開始時刻（タイムスタンプ）
- 1秒以上可視の判定とログ送信トリガー

### 2.2 Domain Layer

#### 2.2.1 責務
- ビジネスロジックのカプセル化
- データ変換（DTOからDomainモデルへ）
- プラットフォーム非依存の再利用可能なロジック

#### 2.2.2 コンポーネント

##### Domain Models
**ImageItem**: 画像情報のドメインモデル
- 画像ID、作者名、サイズ、ダウンロードURL等を保持

**VisibilityLog**: 可視性ログのドメインモデル
- 画像ID、位置、タイムスタンプ（String型：「秒.マイクロ秒」形式）を保持
- JSON形式への変換メソッドを提供
- 出力例：`{"id":"0","position":1,"time":"1234567890.123456"}`

**MicrosecondTimestamp**: マイクロ秒単位のタイムスタンプを表すvalue class
- マイクロ秒単位のタイムスタンプ値を保持
- 秒部分とマイクロ秒部分に分割するプロパティを提供
- 「秒.マイクロ秒」形式でフォーマットするメソッドを提供

##### Use Cases
Use Caseレイヤーは必須として実装：

**GetImagesUseCase**: 画像リスト取得のビジネスロジック
- Repositoryからデータ取得
- エラーハンドリング
- Result型でラップして返却

**LogImageVisibilityUseCase**: 可視性ログ出力のビジネスロジック
- MicrosecondTimestampの生成（現在時刻をマイクロ秒単位で取得）
- VisibilityLogオブジェクトの生成
- JSON形式への変換（「秒.マイクロ秒」形式のタイムスタンプ）
- LogCat/コンソール出力（タグ: `[VisibilityLog]`）
- 出力例: `[VisibilityLog] {"id":"0","position":1,"time":"1234567890.123456"}`

##### Repository Interfaces
**ImageRepository**: 画像データ取得のインターフェース
- `getImages(page: Int, limit: Int): Result<List<ImageItem>>` メソッドを定義

### 2.3 Data Layer

#### 2.3.1 責務
- データソースへのアクセス
- ネットワークリクエストの実行（Ktor Client）
- データマッピング（Response DTO → Domain Model）
- エラーハンドリング

#### 2.3.2 コンポーネント

##### Repository Implementation
**ImageRepositoryImpl**: ImageRepositoryの実装クラス
- RemoteDataSourceの呼び出し
- エラーハンドリング（try-catch）
- DTOからDomainモデルへの変換
- Result型でラップして返却

##### Data Sources
**RemoteDataSource**: リモートデータ取得のインターフェース
- `fetchImages(page: Int, limit: Int): List<ImageResponse>` メソッドを定義

**PicsumRemoteDataSource**: RemoteDataSourceの実装クラス
- Ktor Clientを使用したAPI呼び出し
- kotlinx.serializationによるレスポンスのパース
- エンドポイント: `https://picsum.photos/v2/list`

##### DTOs
**ImageResponse**: APIレスポンスのData Transfer Object
- @Serializableアノテーション付与
- APIのフィールド名とマッピング（`@SerialName`使用）
- `toDomainModel()`拡張関数でImageItemに変換

## 3. データフロー

### 3.1 データ取得フロー

```
User Action (Screen Launch / Retry Button Click)
    ↓
ViewModel.loadImages()
    ↓
GetImagesUseCase.invoke()
    ↓
Repository.getImages()
    ↓
RemoteDataSource.fetchImages()
    ↓
Ktor Client → API Request (GET /v2/list?page=1&limit=30)
    ↓
API Response (JSON)
    ↓
Parse to DTO (ImageResponse) [kotlinx.serialization]
    ↓
Map to Domain Model (ImageItem)
    ↓
Repository returns Result<List<ImageItem>>
    ↓
ViewModel updates UiState (Success/Error)
    ↓
Composable observes StateFlow
    ↓
UI recomposes with new data
```

### 3.2 onVisibilityChanged イベントフロー

```
LazyColumn renders ImageCard (with index)
    ↓
onVisibilityChanged callback triggered
    ↓
Visibility state captured (threshold: 50%)
    ↓
Check: visiblePercentage >= 50% ?
    ├─ No: Do nothing
    └─ Yes: Start timer (Compose State)
        ↓
        Wait 1 second (LaunchedEffect)
        ↓
        Check: Still visible >= 50% ?
        ├─ No: Cancel timer
        └─ Yes: Trigger visibility log
            ↓
            ViewModel.onImageVisible(imageId, position)
            ↓
            LogImageVisibilityUseCase.invoke(imageId, position)
            ↓
            Generate VisibilityLog (id, position, time)
            ↓
            Convert to JSON
            ↓
            Output to LogCat with tag "VisibilityLog"
```

## 4. 技術スタック

### 4.1 共通（commonMain）

#### コアフレームワーク
- **Kotlin**: 2.0.0+
- **Compose Multiplatform**: 1.6.0+
- **Kotlinx Coroutines**: 非同期処理
- **kotlinx.serialization**: JSON シリアライゼーション

#### ネットワーキング
- **HTTPクライアント**: Ktor Client 3.0+
  - ktor-client-core
  - ktor-client-content-negotiation
  - ktor-serialization-kotlinx-json
  - ktor-client-logging

#### ロギング
- **ログライブラリ**: Napier
  - Kotlin Multiplatform対応のロギングライブラリ
  - Ktor Clientのログ出力に使用

#### 画像読み込み
- **画像ライブラリ**: Coil3
  - coil-compose
  - coil-network-ktor

#### 状態管理
- **ViewModel**: androidx.lifecycle.viewmodel (Multiplatform対応)
  - 参考: [Multiplatform ViewModel](https://developer.android.com/kotlin/multiplatform/viewmodel)
- **StateFlow/SharedFlow**: Kotlinx Coroutines

#### 依存性注入
- **DIフレームワーク**: Koin
  - koin-core
  - koin-compose

### 4.2 Android固有（androidMain）
- **Jetpack Compose**: Android UI
- **AndroidX Libraries**: ViewModel, Lifecycle
- **Ktor Client Engine**: OkHttp（公式推奨）
- **Kotlinx Coroutines Android**: Android用Dispatcher

### 4.3 iOS固有（iosMain）
- **Compose Multiplatform for iOS**: UI
- **ViewModel**: 共通ViewModelを使用
- **Ktor Client Engine**: Darwin

### 4.4 テスティング
- **JUnit**: ユニットテスト（androidUnitTest用）
- **Kotlin Test**: 共通テスト（commonTest用）
- **Kotlinx Coroutines Test**: コルーチンテスト用ユーティリティ
- **MainDispatcherRule**: JUnit Ruleによるテスト用Dispatcher設定（androidUnitTest）
- **MockK**: モックライブラリ（プラットフォーム固有クラスのみ）
- **Fake Pattern**: 優先的に使用（Repository、DataSourceのFake実装）
- **UIテスト**: 実装しない
- **テストカバレッジ目標**: 80%以上
- **テスト実績**: 39テスト（2025年1月時点）

## 5. モジュール/パッケージ構成

### 5.1 ディレクトリ構造（レイヤー別パッケージ構成）

```
composeApp/src/
├── commonMain/kotlin/dev/dai/compose/visibility/sample/
│   ├── ui/
│   │   ├── screen/
│   │   ├── component/
│   │   └── viewmodel/
│   ├── domain/
│   │   ├── model/
│   │   ├── repository/
│   │   └── usecase/
│   ├── data/
│   │   ├── repository/
│   │   ├── remote/
│   │   └── model/
│   ├── di/
│   └── App.kt
├── commonTest/kotlin/dev/dai/compose/visibility/sample/
│   ├── domain/
│   │   ├── model/
│   │   └── usecase/
│   ├── data/
│   │   ├── repository/
│   │   └── remote/
│   └── fake/
├── androidUnitTest/kotlin/dev/dai/compose/visibility/sample/
│   ├── ui/
│   │   └── viewmodel/
│   └── util/
│       └── MainDispatcherRule.kt
├── androidMain/kotlin/dev/dai/compose/visibility/sample/
│   ├── MainActivity.kt
│   └── di/
│       └── HttpClientFactory.android.kt
└── iosMain/kotlin/dev/dai/compose/visibility/sample/
    ├── MainViewController.kt
    └── di/
        └── HttpClientFactory.ios.kt
```

### 5.2 パッケージ説明

- **ui/**: UI Layer - Composables, ViewModels
  - **screen/**: 画面レベルのコンポーザブル
  - **component/**: 再利用可能なUIコンポーネント
  - **viewmodel/**: ViewModelクラス
- **domain/**: Domain Layer - Models, Repository Interfaces, Use Cases
  - **model/**: ドメインモデル
  - **repository/**: Repositoryインターフェース
  - **usecase/**: ビジネスロジック（Use Cases）
- **data/**: Data Layer - Repository Implementations, Data Sources, DTOs
  - **repository/**: Repository実装
  - **remote/**: リモートデータソース
  - **model/**: DTOs（Data Transfer Objects）
- **di/**: Dependency Injection configuration (Koin)
- **fake/**: テスト用Fake実装（commonTest配下）

## 6. onVisibilityChanged API統合設計

### 6.1 API仕様
- **API**: `Modifier.onVisibilityChanged`
- **ドキュメント**: [androidx.compose.ui.layout.onVisibilityChanged](https://developer.android.com/reference/kotlin/androidx/compose/ui/layout/package-summary#(androidx.compose.ui.Modifier).onVisibilityChanged(kotlin.Long,kotlin.Float,androidx.compose.ui.layout.LayoutBoundsHolder,kotlin.Function1))

### 6.2 統合方針
**ImageCard Composable**内でonVisibilityChanged modifierを使用し、以下を実現：

**可視性判定**:
- 閾値: 50%（`threshold = 0.5f`）
- 持続時間: 1秒以上
- LaunchedEffectを使用したタイマー実装

**ログ送信条件**:
- 閾値と持続時間の両方を満たした場合
- 重複防止: 1度ログ送信したら再送信しない

## 7. 依存性注入（DI）設計

### 7.1 Koin 構成方針

**モジュール定義**:
- Data Layer: HttpClient、RemoteDataSource、Repository
- Domain Layer: Use Cases
- UI Layer: ViewModel

**Koin初期化**:
- Appコンポーザブル内でKoinApplicationを使用
- 全モジュールを登録

**スコープ**:
- HttpClient、DataSource、Repository: Singleton
- Use Cases: Factory（毎回新しいインスタンス）
- ViewModel: viewModelスコープ

## 8. エラーハンドリング戦略

### 8.1 エラータイプ
- **ネットワークエラー**: 接続失敗、タイムアウト
- **HTTPエラー**: 4xx, 5xx
- **パースエラー**: JSONデコードエラー
- **不明なエラー**: その他例外

### 8.2 エラーハンドリング方針

**Data Layer**:
- try-catchでラップ
- Result型でSuccess/Failureを返却

**ViewModel**:
- Result型のonSuccess/onFailureで分岐
- UiStateをError状態に更新
- エラーメッセージを適切に設定

**UI Layer**:
- Error状態時にErrorViewを表示
- リトライボタンで再試行可能

## 9. テスト戦略

### 9.1 テスト範囲

**Unit Tests（80%カバレッジ目標）**:
- ViewModel Tests (androidUnitTest): UI状態遷移、ビジネスロジック（9テスト）
- Use Case Tests (commonTest): ビジネスロジック（8テスト + 4テスト）
- Repository Tests (commonTest): データ変換、エラーハンドリング（4テスト）
- Domain Model Tests (commonTest): MicrosecondTimestamp、VisibilityLog（6テスト + 4テスト）
- RemoteDataSource Tests: API呼び出し、レスポンスパース

**Integration Tests**:
- Repository + RemoteDataSource: データ取得フロー全体

**UI Tests**:
- 実装しない

**テスト実績**:
- 合計39テスト（2025年1月時点）
- カバレッジ: 80%以上達成

### 9.2 テストダブル戦略
- **基本方針**: Fake Patternを優先的に使用
- **MockK使用条件**: プラットフォーム固有のクラス（HttpClientなど）のみ

**Fake実装**:
- FakeImageRepository: ViewModel テスト用
- FakeRemoteDataSource: Repository テスト用

### 9.3 テストカバレッジ
- **目標**: 80%以上
- **測定**: JaCoCo（Android）、Kover（KMP）
- **対象**: commonMain、androidMain、iosMain

## 10. パフォーマンス最適化

### 10.1 画像読み込み最適化
- Coil3の自動リサイズ機能を活用
- メモリキャッシュ、ディスクキャッシュの活用（Coil3デフォルト設定）
- プレースホルダー、エラー画像の設定

### 10.2 LazyColumn最適化
- `key`パラメータの適切な設定（imageItem.idを使用）
- 不要な再コンポジションの回避
- `contentType`の活用

### 10.3 コルーチン最適化
- 適切なDispatcherの使用（Dispatchers.IO, Dispatchers.Main）
- viewModelScopeによる自動キャンセル処理

## 11. セキュリティ考慮事項

- **HTTPS通信**: Lorem Picsum APIはHTTPS対応
- **入力検証**: APIレスポンスの適切なバリデーション（kotlinx.serialization）
- **例外処理**: 不正なデータによるクラッシュ防止（try-catch）
- **ProGuard/R8**: デフォルト設定を使用（追加の難読化設定不要）

## 12. 今後の拡張性

以下の拡張は**本プロジェクトのスコープ外**とし、実装しない：

- ❌ ページング機能の追加（無限スクロール）
- ❌ オフライン対応（ローカルキャッシュ、Room Database）
- ❌ 画像詳細画面への遷移（ナビゲーション）
- ❌ お気に入り機能（ローカルストレージ）
- ❌ 検索機能（APIクエリ）

アーキテクチャは上記機能の将来的な追加を考慮した設計とするが、実装は行わない。

## 13. 開発ガイドライン

### 13.1 コーディング規約
- Kotlin公式コーディング規約に準拠
- KtLintまたはDetektの導入を推奨

### 13.2 Git戦略
- **ブランチ戦略**: 使用しない
- **コミット先**: mainブランチに直接コミット
- **プッシュタイミング**: 一連の機能が完成するまでプッシュしない
- **コミットメッセージ**: Conventional Commits形式を推奨

### 13.3 ドキュメント
- README.md: プロジェクト概要、セットアップ手順
- 各レイヤーのKDocコメント
- 複雑なロジックには詳細コメント

## 14. 実装ガイド

具体的な実装コード例、詳細な実装手順については以下のフォルダにドキュメントを作成して参照しながらAIによる実装を行う。
- [実装ガイドフォルダ](./implementation)

## 15. 実装状況（2025年1月時点）

### 15.1 完成済み機能
- ✅ UI Layer: ImageListScreen, ImageCard, ViewModel
- ✅ Domain Layer: Use Cases（GetImagesUseCase, LogImageVisibilityUseCase）
- ✅ Domain Models: ImageItem, VisibilityLog, MicrosecondTimestamp
- ✅ Data Layer: Repository, RemoteDataSource
- ✅ DI設定: Koin
- ✅ onVisibilityChanged API統合
- ✅ エラーハンドリング
- ✅ Napierロギング統合
- ✅ テスト実装（39テスト、80%以上カバレッジ）

### 15.2 技術的詳細

**Ktor Client設定**:
- Android: OkHttp Engine（公式推奨）
- iOS: Darwin Engine（TLS/HTTPS対応）
- ログレベル: LogLevel.ALL
- Napier統合済み

**タイムスタンプ実装**:
- MicrosecondTimestamp value classによる型安全な実装
- 「秒.マイクロ秒」形式のフォーマット（例: "1234567890.123456"）
- ゼロパディング対応（マイクロ秒部分は6桁固定）

**テスト構成**:
- commonTest: Domain/Data層のテスト（Kotlin Test使用）
- androidUnitTest: ViewModel層のテスト（JUnit + MainDispatcherRule使用）
- Fake Pattern採用（FakeImageRepository, FakeRemoteDataSource）

## 16. 参考資料

- [Android Architecture Guidelines](https://developer.android.com/topic/architecture/recommendations)
- [Guide to app architecture](https://developer.android.com/topic/architecture)
- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Multiplatform ViewModel](https://developer.android.com/kotlin/multiplatform/viewmodel)
- [Modifier.onVisibilityChanged](https://developer.android.com/reference/kotlin/androidx/compose/ui/layout/package-summary#(androidx.compose.ui.Modifier).onVisibilityChanged(kotlin.Long,kotlin.Float,androidx.compose.ui.layout.LayoutBoundsHolder,kotlin.Function1))
- [Ktor Client](https://ktor.io/docs/client.html)
- [Ktor Client Multiplatform](https://ktor.io/docs/client-create-multiplatform-application.html)
- [Ktor Client Logging with Napier](https://github.com/ktorio/ktor-documentation/blob/main/codeSnippets/snippets/client-logging-napier/)
- [Napier](https://github.com/AAkira/Napier)
- [Coil3](https://coil-kt.github.io/coil/)
- [Koin](https://insert-koin.io/)
- [Android Coroutines Testing](https://developer.android.com/kotlin/coroutines/test)
- [Lorem Picsum API](https://picsum.photos)

---
