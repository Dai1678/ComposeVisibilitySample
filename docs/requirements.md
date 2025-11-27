# ComposeVisibilitySample - 要求仕様書

## 1. プロジェクト概要

### 1.1 目的
Jetpack ComposeのonVisibilityChanged APIの使用方法を解説する記事のサンプルアプリケーションとして、実装例を提供する。

### 1.2 プラットフォーム
- Android
- iOS（Compose Multiplatform経由）

### 1.3 プロジェクトスコープ
シンプルな1画面のみの画像リストアプリケーション。onVisibilityChanged APIの動作を検証・デモンストレーションすることに焦点を当てる。

## 2. 機能要件

### 2.1 画像リスト表示機能
- **FR-001**: Lorem Picsum APIから画像情報を取得する
  - エンドポイント: `https://picsum.photos/v2/list`
  - パラメータ:
    - `page`: ページ番号（固定: 1）
    - `limit`: 1ページあたりの件数（固定: 30）
  - 注記: ページング機能は実装しない（初回取得の30件のみ表示）

- **FR-002**: LazyColumnを使用して画像リストを縦スクロール可能に表示する

- **FR-003**: 各アイテムはCardコンポーネントで表示し、以下の情報を含む
  - 画像（`download_url`から取得）
  - 作者名（`author`フィールド）
  - 注記: 画像サイズ、レイアウト詳細、Card styling等は実装時にデザインファイルに基づいて決定

### 2.2 可視性検知機能
- **FR-004**: Jetpack ComposeのModifier.onVisibilityChanged APIを使用して、各Cardの可視状態を検知する
  - API仕様: [androidx.compose.ui.layout.onVisibilityChanged](https://developer.android.com/reference/kotlin/androidx/compose/ui/layout/package-summary#(androidx.compose.ui.Modifier).onVisibilityChanged(kotlin.Long,kotlin.Float,androidx.compose.ui.layout.LayoutBoundsHolder,kotlin.Function1))
  - 可視性閾値: 各itemの50%が表示された状態
  - 可視性持続時間: 1秒以上

- **FR-005**: 可視性検知時のログ送信処理
  - 各itemが上記条件（50%表示、1秒以上）を満たした時点でログを記録
  - ログはLogCatに以下のJSON形式で出力:
    ```json
    {
      "id": "画像ID",
      "position": 1,
      "time": "1762301456.123456"
    }
    ```
  - フィールド説明:
    - `id`: 画像の一意識別子（APIレスポンスの`id`フィールド）
    - `position`: LazyColumnにおけるindex（1始まり）
    - `time`: ログ記録時刻（「秒.マイクロ秒」形式の文字列、例：「1234567890.123456」）
  - 注記: ログ送信APIは実装せず、LogCat出力のみで代替

### 2.3 データ取得機能
- **FR-006**: アプリ起動時に自動的に画像リストを取得する
- **FR-007**: ネットワークエラー時の処理
  - エラー表示: 画面中央に汎用的なエラーメッセージを表示
  - リトライ機能: エラーメッセージの下にリトライボタンを配置
  - オフライン動作: 対応しない（インターネット接続必須）

### 2.4 ページング機能
- **FR-008**: ページング機能は実装しない
  - 初回取得の30件のみを表示
  - スクロール到達時の追加読み込みは行わない

## 3. 非機能要件

### 3.1 パフォーマンス
- **NFR-001**: 画像の遅延読み込み（Lazy Loading）を実装する
- **NFR-002**: 画像キャッシュを適切に管理する（Coil3の機能を活用）
- **NFR-003**: LazyColumnのスクロールパフォーマンスを維持する
- **NFR-004**: onVisibilityChangedコールバック内で重い処理を避ける

### 3.2 ユーザビリティ
- **NFR-005**: 初回データ取得中は画面中央にローディングインジケーター（CircularProgressIndicator）を表示する
- **NFR-006**: 画像読み込み中はプレースホルダーを表示する（実装時にデザイン決定）

### 3.3 互換性
- **NFR-007**: Android最小サポートバージョン: プロジェクトの設定ファイルに記載
- **NFR-008**: iOSサポートバージョン: プロジェクトの設定ファイルに記載

### 3.4 保守性
- **NFR-009**: Androidの推奨アーキテクチャに準拠する
- **NFR-010**: テスタブルなコード構造を維持する
- **NFR-011**: Kotlin Multiplatformの共通コード最大化
- **NFR-012**: テストカバレッジ80%以上を達成する

## 4. API仕様

### 4.1 Lorem Picsum API v2

#### エンドポイント
```
GET https://picsum.photos/v2/list
```

#### リクエストパラメータ
| パラメータ | 型 | 必須 | 使用値 | 説明 |
|-----------|-----|------|--------|------|
| page | Integer | No | 1 | ページ番号（固定） |
| limit | Integer | No | 30 | 1ページあたりの件数（固定） |

#### レスポンス形式
```json
[
  {
    "id": "0",
    "author": "Alejandro Escamilla",
    "width": 5000,
    "height": 3333,
    "url": "https://unsplash.com/...",
    "download_url": "https://picsum.photos/id/0/5000/3333"
  }
]
```

#### レスポンスフィールド
| フィールド | 型 | 説明 |
|-----------|-----|------|
| id | String | 画像の一意識別子 |
| author | String | 撮影者名 |
| width | Integer | 画像の幅（ピクセル） |
| height | Integer | 画像の高さ（ピクセル） |
| url | String | Unsplashの元URL |
| download_url | String | 画像ダウンロードURL |

#### レスポンスヘッダー
- `Link`: ページング情報（next, prev, first, last）
  - 注記: 本アプリではページング機能を実装しないため使用しない

## 5. UI/UX要件

### 5.1 画面構成
- **UI-001**: 単一画面構成（リスト表示のみ）
- **UI-002**: トップバー（実装時にタイトル等を決定）

### 5.2 リストアイテムデザイン
- **UI-003**: Cardコンポーネント使用
- **UI-004**: 画像表示（実装時にサイズ、ContentScale等を決定）
- **UI-005**: 作者名テキスト（実装時にスタイル、配置を決定）

### 5.3 状態表示
- **UI-006**: ローディング状態
  - 画面中央にCircularProgressIndicatorを表示
- **UI-007**: エラー状態
  - 画面中央に汎用的なエラーメッセージを表示
  - エラーメッセージの下にリトライボタンを配置
- **UI-008**: 空状態
  - データが0件の場合、画面中央に「画像がありません」と表示

### 5.4 onVisibilityChanged視覚フィードバック
- **UI-009**: 可視性変化時の視覚フィードバック
  - 本アプリの主目的はログ出力によるデモンストレーション
  - UI上の視覚的フィードバックは不要（LogCat出力のみ）

## 6. 技術的制約

### 6.1 使用技術
- Kotlin Multiplatform
- Jetpack Compose / Compose Multiplatform
- HTTPクライアント: **Ktor Client**
  - Android: OkHttp Engine
  - iOS: Darwin Engine
- ロギング: **Napier**（Kotlin Multiplatform対応）
- 画像読み込みライブラリ: **Coil3**
- DIフレームワーク: **Koin**
- ViewModel: AndroidとiOSで共用（[Multiplatform ViewModel](https://developer.android.com/kotlin/multiplatform/viewmodel)）
- テスト: JUnit (androidUnitTest), Kotlin Test (commonTest), Kotlinx Coroutines Test

### 6.2 外部依存
- Lorem Picsum API（外部サービス）
  - インターネット接続必須
  - オフライン動作は対応しない

## 7. ログ出力仕様

### 7.1 可視性ログ
各画像アイテムが以下の条件を満たした際にLogCatに出力：
- 条件: アイテムの50%以上が1秒以上表示された状態

### 7.2 ログフォーマット
```json
{
  "id": "0",
  "position": 1,
  "time": "1762301456.123456"
}
```

### 7.3 ログフィールド仕様
| フィールド | 型 | 説明 | 例 |
|-----------|-----|------|-----|
| id | String | 画像の一意識別子（APIレスポンスのid） | "0" |
| position | Integer | LazyColumnにおけるindex（1始まり） | 1 |
| time | String | ログ記録時刻（「秒.マイクロ秒」形式）<br>- 秒部分: 10桁<br>- マイクロ秒部分: 6桁（ゼロパディング） | "1762301456.123456" |

### 7.4 ログタグ
- LogCatタグ: `VisibilityLog`

## 8. 実装ガイド

具体的な実装コード例、詳細な実装手順については以下のフォルダにドキュメントを作成して参照しながらAIによる実装を行う。
- [実装ガイドフォルダ](./implementation)

## 9. 今後の拡張性

以下の機能は**本プロジェクトのスコープ外**とし、実装しない：

- ❌ ページング機能の追加（無限スクロール）
- ❌ オフライン対応（ローカルキャッシュ）
- ❌ 画像詳細画面への遷移
- ❌ お気に入り機能
- ❌ 画像検索機能
- ❌ ダークモード対応
- ❌ アクセシビリティ強化

これらの機能は、onVisibilityChanged APIのデモという本アプリの目的に必須ではないため、実装を行わない。

## 10. 開発ワークフロー

### 10.1 Git戦略
- ブランチ戦略: 使用しない
- コミット先: 常にmainブランチに直接コミット
- プッシュタイミング: 一連の機能が完成するまでプッシュしない
- コミットメッセージ: Conventional Commits形式を推奨

### 10.2 コーディング規約
- Kotlin公式コーディング規約に準拠
- KtLintまたはDetektの導入を推奨

---
