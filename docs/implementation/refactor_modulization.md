# ComposeVisibilitySample マルチモジュール化リファクタリング プロンプト

## 📋 プロジェクト概要

既存の単一モジュール構成のComposeVisibilitySampleアプリを、以下のマルチモジュール構成にリファクタリングします。

**対象リポジトリ:** https://github.com/Dai1678/ComposeVisibilitySample

## 🎯 目標モジュール構成

```
ComposeVisibilitySample/
│
├── build-logic/                          # Convention Plugins
│   └── convention/
│       ├── build.gradle.kts
│       └── src/main/kotlin/
│           ├── ComposeMultiplatformConventionPlugin.kt
│           ├── KotlinMultiplatformConventionPlugin.kt
│           ├── FeatureConventionPlugin.kt
│           ├── DataConventionPlugin.kt
│           └── DomainConventionPlugin.kt
│
├── composeApp/                           # アプリエントリポイント
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/
│       │   └── kotlin/dev/dai/compose/visibility/sample/
│       │       ├── App.kt
│       │       └── di/
│       │           └── AppModule.kt
│       ├── androidMain/
│       │   └── kotlin/dev/dai/compose/visibility/sample/
│       │       └── MainActivity.kt
│       └── iosMain/
│           └── kotlin/dev/dai/compose/visibility/sample/
│               └── MainViewController.kt
│
├── core/
│   ├── common/                           # 共通ユーティリティ
│   │   ├── build.gradle.kts
│   │   └── src/
│   │       └── commonMain/kotlin/dev/dai/compose/visibility/sample/core/common/
│   │           ├── result/
│   │           │   └── Result.kt
│   │           └── util/
│   │               └── Extensions.kt
│   │
│   ├── data/
│   │   └── image/                        # Data層統合モジュール
│   │       ├── build.gradle.kts
│   │       └── src/
│   │           ├── commonMain/kotlin/dev/dai/compose/visibility/sample/core/data/image/
│   │           │   ├── di/
│   │           │   │   ├── DataSourceModule.kt
│   │           │   │   └── RepositoryModule.kt
│   │           │   ├── remote/
│   │           │   │   ├── PicsumRemoteDataSource.kt
│   │           │   │   ├── RemoteDataSource.kt
│   │           │   │   └── model/
│   │           │   │       └── ImageResponse.kt
│   │           │   ├── repository/
│   │           │   │   └── ImageRepositoryImpl.kt
│   │           │   └── mapper/
│   │           │       └── ImageMapper.kt
│   │           └── commonTest/kotlin/dev/dai/compose/visibility/sample/core/data/image/
│   │               ├── repository/
│   │               │   └── ImageRepositoryImplTest.kt
│   │               ├── remote/
│   │               │   └── PicsumRemoteDataSourceTest.kt
│   │               └── fake/
│   │                   └── FakeRemoteDataSource.kt
│   │
│   └── domain/
│       └── image/                        # Domain層モジュール
│           ├── build.gradle.kts
│           └── src/
│               ├── commonMain/kotlin/dev/dai/compose/visibility/sample/core/domain/image/
│               │   ├── model/
│               │   │   ├── ImageItem.kt
│               │   │   ├── VisibilityLog.kt
│               │   │   └── MicrosecondTimestamp.kt
│               │   ├── repository/
│               │   │   └── ImageRepository.kt
│               │   └── usecase/
│               │       ├── GetImagesUseCase.kt
│               │       └── LogImageVisibilityUseCase.kt
│               └── commonTest/kotlin/dev/dai/compose/visibility/sample/core/domain/image/
│                   ├── model/
│                   │   ├── MicrosecondTimestampTest.kt
│                   │   └── VisibilityLogTest.kt
│                   └── usecase/
│                       ├── GetImagesUseCaseTest.kt
│                       └── LogImageVisibilityUseCaseTest.kt
│
├── feature/
│   └── imagelist/                        # UI層モジュール
│       ├── build.gradle.kts
│       └── src/
│           ├── commonMain/kotlin/dev/dai/compose/visibility/sample/feature/imagelist/
│           │   ├── di/
│           │   │   └── FeatureModule.kt
│           │   ├── screen/
│           │   │   └── ImageListScreen.kt
│           │   ├── component/
│           │   │   ├── ImageCard.kt
│           │   │   ├── ErrorView.kt
│           │   │   └── LoadingView.kt
│           │   └── viewmodel/
│           │       └── ImageListViewModel.kt
│           └── androidUnitTest/kotlin/dev/dai/compose/visibility/sample/feature/imagelist/
│               ├── viewmodel/
│               │   └── ImageListViewModelTest.kt
│               ├── util/
│               │   └── MainDispatcherRule.kt
│               └── fake/
│                   └── FakeImageRepository.kt
│
├── iosApp/
│
├── settings.gradle.kts
└── gradle.properties
```

## 🔧 リファクタリング手順

### Phase 0: 事前準備

#### ステップ0-1: 既存コードの確認
```prompt
現在のComposeVisibilitySampleプロジェクトのディレクトリ構造とsettings.gradle.ktsを確認してください。
```

#### ステップ0-2: 現在のパッケージ構造の確認
```prompt
composeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/ 配下の
パッケージ構造を確認し、各ファイルの配置を把握してください。
```

---

### Phase 1: build-logic の作成

#### ステップ1-1: build-logic ディレクトリの作成
```prompt
プロジェクトルートに build-logic ディレクトリを作成してください。
以下の構造で初期セットアップを行ってください：

build-logic/
├── settings.gradle.kts
├── gradle.properties
└── convention/
    └── build.gradle.kts

settings.gradle.kts:
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
```

gradle.properties:
```properties
org.gradle.jvmargs=-Xmx2048m
org.gradle.caching=true
org.gradle.configuration-cache=true
```

convention/build.gradle.kts:
```kotlin
plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}
```

#### ステップ1-2: Convention Plugin の作成
```prompt
build-logic/convention/src/main/kotlin/ に以下のConvention Pluginを作成してください：

1. KotlinMultiplatformConventionPlugin.kt
   - Kotlin Multiplatformの共通設定
   - Android, iOS, JVM targetの設定
   - JVM toolchain設定

2. ComposeMultiplatformConventionPlugin.kt
   - Compose Multiplatformの共通設定
   - Compose Compilerの設定

3. DataConventionPlugin.kt
   - Data層モジュール用の設定
   - Ktor Client, kotlinx.serialization依存関係
   - Koin依存関係

4. DomainConventionPlugin.kt
   - Domain層モジュール用の設定
   - Kotlinx Coroutines依存関係

5. FeatureConventionPlugin.kt
   - Feature層モジュール用の設定
   - Compose, ViewModel依存関係
   - Coil依存関係

各プラグインは既存のcomposeApp/build.gradle.ktsの設定を参考にしてください。
```

#### ステップ1-3: ルートsettings.gradle.ktsの更新
```prompt
ルートのsettings.gradle.ktsを更新して、build-logicをincludeBuildしてください：

```kotlin
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
```

---

### Phase 2: core:common モジュールの作成

#### ステップ2-1: core:common ディレクトリ構造の作成
```prompt
core/common/ ディレクトリを作成し、以下の構造を作ってください：

core/common/
├── build.gradle.kts
└── src/
    └── commonMain/kotlin/dev/dai/compose/visibility/sample/core/common/
        └── .gitkeep
```

#### ステップ2-2: core:common のbuild.gradle.kts作成
```prompt
core/common/build.gradle.kts を作成してください。
KotlinMultiplatformConventionPluginを適用し、
基本的な依存関係のみを含めてください。

```kotlin
plugins {
    id("dev.dai.compose.visibility.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // 共通ライブラリ（必要に応じて）
        }
    }
}
```

#### ステップ2-3: settings.gradle.ktsに追加
```prompt
ルートのsettings.gradle.ktsに以下を追加してください：

```kotlin
include(":core:common")
```

#### ステップ2-4: ビルド確認
```prompt
./gradlew :core:common:build を実行して、モジュールが正しく作成されたか確認してください。
```

---

### Phase 3: core:domain:image モジュールの作成と移行

#### ステップ3-1: core:domain:image ディレクトリ構造の作成
```prompt
core/domain/image/ ディレクトリを以下の構造で作成してください：

core/domain/image/
├── build.gradle.kts
└── src/
    ├── commonMain/kotlin/dev/dai/compose/visibility/sample/core/domain/image/
    │   ├── model/
    │   ├── repository/
    │   └── usecase/
    └── commonTest/kotlin/dev/dai/compose/visibility/sample/core/domain/image/
        ├── model/
        └── usecase/
```

#### ステップ3-2: core:domain:image のbuild.gradle.kts作成
```prompt
core/domain/image/build.gradle.kts を作成してください：

```kotlin
plugins {
    id("dev.dai.compose.visibility.kotlinMultiplatform")
    id("dev.dai.compose.visibility.domain")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
```

#### ステップ3-3: Domainモデルの移行
```prompt
既存のcomposeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/domain/model/
から以下のファイルを
core/domain/image/src/commonMain/kotlin/dev/dai/compose/visibility/sample/core/domain/image/model/
に移動してください：

- ImageItem.kt
- VisibilityLog.kt
- MicrosecondTimestamp.kt

パッケージ宣言を以下に変更してください：
```kotlin
package dev.dai.compose.visibility.sample.core.domain.image.model
```

#### ステップ3-4: Repository interfaceの移行
```prompt
既存のcomposeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/domain/repository/
から ImageRepository.kt を
core/domain/image/src/commonMain/kotlin/dev/dai/compose/visibility/sample/core/domain/image/repository/
に移動してください。

パッケージ宣言を変更し、import文を更新してください：
```kotlin
package dev.dai.compose.visibility.sample.core.domain.image.repository

import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem
```

#### ステップ3-5: UseCaseの移行
```prompt
既存のcomposeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/domain/usecase/
から以下のファイルを
core/domain/image/src/commonMain/kotlin/dev/dai/compose/visibility/sample/core/domain/image/usecase/
に移動してください：

- GetImagesUseCase.kt
- LogImageVisibilityUseCase.kt

パッケージ宣言とimport文を更新してください。
```

#### ステップ3-6: Domainテストの移行
```prompt
既存のcomposeApp/src/commonTest/ から以下のテストファイルを移行してください：

移行元: composeApp/src/commonTest/kotlin/dev/dai/compose/visibility/sample/domain/
移行先: core/domain/image/src/commonTest/kotlin/dev/dai/compose/visibility/sample/core/domain/image/

移行対象：
- model/MicrosecondTimestampTest.kt
- model/VisibilityLogTest.kt
- usecase/GetImagesUseCaseTest.kt
- usecase/LogImageVisibilityUseCaseTest.kt

パッケージ宣言とimport文を更新してください。
```

#### ステップ3-7: settings.gradle.ktsに追加
```prompt
ルートのsettings.gradle.ktsに以下を追加してください：

```kotlin
include(":core:domain:image")
```

#### ステップ3-8: ビルド確認
```prompt
./gradlew :core:domain:image:build を実行して、ビルドエラーがないか確認してください。
テストも実行してください：
./gradlew :core:domain:image:test
```

---

### Phase 4: core:data:image モジュールの作成と移行

#### ステップ4-1: core:data:image ディレクトリ構造の作成
```prompt
core/data/image/ ディレクトリを以下の構造で作成してください：

core/data/image/
├── build.gradle.kts
└── src/
    ├── commonMain/kotlin/dev/dai/compose/visibility/sample/core/data/image/
    │   ├── di/
    │   ├── remote/
    │   │   └── model/
    │   ├── repository/
    │   └── mapper/
    └── commonTest/kotlin/dev/dai/compose/visibility/sample/core/data/image/
        ├── repository/
        ├── remote/
        └── fake/
```

#### ステップ4-2: core:data:image のbuild.gradle.kts作成
```prompt
core/data/image/build.gradle.kts を作成してください：

```kotlin
plugins {
    id("dev.dai.compose.visibility.kotlinMultiplatform")
    id("dev.dai.compose.visibility.data")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain.image)
            implementation(projects.core.common)
            
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            
            implementation(libs.koin.core)
            implementation(libs.napier)
        }
        
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
```

#### ステップ4-3: Remote DataSourceの移行
```prompt
既存のcomposeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/data/remote/
から以下のファイルを移行してください：

移行先: core/data/image/src/commonMain/kotlin/dev/dai/compose/visibility/sample/core/data/image/remote/

移行対象：
- RemoteDataSource.kt
- PicsumRemoteDataSource.kt

model/ ディレクトリ配下：
- ImageResponse.kt

パッケージ宣言を以下に変更：
```kotlin
package dev.dai.compose.visibility.sample.core.data.image.remote
// または
package dev.dai.compose.visibility.sample.core.data.image.remote.model
```

import文も適切に更新してください。
```

#### ステップ4-4: Mapperの作成
```prompt
core/data/image/src/commonMain/kotlin/dev/dai/compose/visibility/sample/core/data/image/mapper/
に ImageMapper.kt を作成してください。

既存のImageResponse内のtoDomainModel()拡張関数を
このMapperファイルに移動してください：

```kotlin
package dev.dai.compose.visibility.sample.core.data.image.mapper

import dev.dai.compose.visibility.sample.core.data.image.remote.model.ImageResponse
import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem

fun ImageResponse.toDomainModel(): ImageItem {
    // 既存の実装
}

fun List<ImageResponse>.toDomainModel(): List<ImageItem> {
    return map { it.toDomainModel() }
}
```

#### ステップ4-5: Repository実装の移行
```prompt
既存のcomposeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/data/repository/
から ImageRepositoryImpl.kt を
core/data/image/src/commonMain/kotlin/dev/dai/compose/visibility/sample/core/data/image/repository/
に移行してください。

パッケージ宣言とimport文を更新してください：
```kotlin
package dev.dai.compose.visibility.sample.core.data.image.repository

import dev.dai.compose.visibility.sample.core.domain.image.repository.ImageRepository
import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem
import dev.dai.compose.visibility.sample.core.data.image.remote.RemoteDataSource
import dev.dai.compose.visibility.sample.core.data.image.mapper.toDomainModel
```

#### ステップ4-6: DIモジュールの作成
```prompt
core/data/image/src/commonMain/kotlin/dev/dai/compose/visibility/sample/core/data/image/di/
に以下のファイルを作成してください：

1. DataSourceModule.kt
```kotlin
package dev.dai.compose.visibility.sample.core.data.image.di

import dev.dai.compose.visibility.sample.core.data.image.remote.PicsumRemoteDataSource
import dev.dai.compose.visibility.sample.core.data.image.remote.RemoteDataSource
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataSourceModule = module {
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(message, tag = "HTTP")
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
    
    single<RemoteDataSource> {
        PicsumRemoteDataSource(httpClient = get())
    }
}
```

2. RepositoryModule.kt
```kotlin
package dev.dai.compose.visibility.sample.core.data.image.di

import dev.dai.compose.visibility.sample.core.data.image.repository.ImageRepositoryImpl
import dev.dai.compose.visibility.sample.core.domain.image.repository.ImageRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ImageRepository> {
        ImageRepositoryImpl(remoteDataSource = get())
    }
}
```

#### ステップ4-7: Dataテストの移行
```prompt
既存のcomposeApp/src/commonTest/kotlin/dev/dai/compose/visibility/sample/data/
から以下のテストファイルを移行してください：

移行先: core/data/image/src/commonTest/kotlin/dev/dai/compose/visibility/sample/core/data/image/

移行対象：
- repository/ImageRepositoryImplTest.kt
- remote/PicsumRemoteDataSourceTest.kt

fake/ ディレクトリ配下：
- FakeRemoteDataSource.kt

パッケージ宣言とimport文を更新してください。
```

#### ステップ4-8: settings.gradle.ktsに追加
```prompt
ルートのsettings.gradle.ktsに以下を追加してください：

```kotlin
include(":core:data:image")
```

#### ステップ4-9: ビルド確認
```prompt
./gradlew :core:data:image:build を実行してビルドエラーを確認してください。
テストも実行してください：
./gradlew :core:data:image:test
```

---

### Phase 5: feature:imagelist モジュールの作成と移行

#### ステップ5-1: feature:imagelist ディレクトリ構造の作成
```prompt
feature/imagelist/ ディレクトリを以下の構造で作成してください：

feature/imagelist/
├── build.gradle.kts
└── src/
    ├── commonMain/kotlin/dev/dai/compose/visibility/sample/feature/imagelist/
    │   ├── di/
    │   ├── screen/
    │   ├── component/
    │   └── viewmodel/
    └── androidUnitTest/kotlin/dev/dai/compose/visibility/sample/feature/imagelist/
        ├── viewmodel/
        ├── util/
        └── fake/
```

#### ステップ5-2: feature:imagelist のbuild.gradle.kts作成
```prompt
feature/imagelist/build.gradle.kts を作成してください：

```kotlin
plugins {
    id("dev.dai.compose.visibility.kotlinMultiplatform")
    id("dev.dai.compose.visibility.composeMultiplatform")
    id("dev.dai.compose.visibility.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain.image)
            implementation(projects.core.common)
            
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            
            implementation(libs.napier)
        }
        
        androidUnitTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
```

#### ステップ5-3: UI Componentの移行
```prompt
既存のcomposeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/ui/component/
から以下のファイルを
feature/imagelist/src/commonMain/kotlin/dev/dai/compose/visibility/sample/feature/imagelist/component/
に移行してください：

- ImageCard.kt
- ErrorView.kt
- LoadingView.kt

パッケージ宣言を変更：
```kotlin
package dev.dai.compose.visibility.sample.feature.imagelist.component
```

import文を更新：
```kotlin
import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem
import dev.dai.compose.visibility.sample.core.domain.image.model.VisibilityLog
```

#### ステップ5-4: Screenの移行
```prompt
既存のcomposeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/ui/screen/
から ImageListScreen.kt を
feature/imagelist/src/commonMain/kotlin/dev/dai/compose/visibility/sample/feature/imagelist/screen/
に移行してください。

パッケージ宣言とimport文を更新してください。
```

#### ステップ5-5: ViewModelの移行
```prompt
既存のcomposeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/ui/viewmodel/
から ImageListViewModel.kt を
feature/imagelist/src/commonMain/kotlin/dev/dai/compose/visibility/sample/feature/imagelist/viewmodel/
に移行してください。

パッケージ宣言を変更：
```kotlin
package dev.dai.compose.visibility.sample.feature.imagelist.viewmodel

import dev.dai.compose.visibility.sample.core.domain.image.usecase.GetImagesUseCase
import dev.dai.compose.visibility.sample.core.domain.image.usecase.LogImageVisibilityUseCase
import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem
```

#### ステップ5-6: DIモジュールの作成
```prompt
feature/imagelist/src/commonMain/kotlin/dev/dai/compose/visibility/sample/feature/imagelist/di/
に FeatureModule.kt を作成してください：

```kotlin
package dev.dai.compose.visibility.sample.feature.imagelist.di

import dev.dai.compose.visibility.sample.core.domain.image.usecase.GetImagesUseCase
import dev.dai.compose.visibility.sample.core.domain.image.usecase.LogImageVisibilityUseCase
import dev.dai.compose.visibility.sample.feature.imagelist.viewmodel.ImageListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val imageListFeatureModule = module {
    factory { GetImagesUseCase(imageRepository = get()) }
    factory { LogImageVisibilityUseCase() }
    
    viewModel {
        ImageListViewModel(
            getImagesUseCase = get(),
            logImageVisibilityUseCase = get()
        )
    }
}
```

#### ステップ5-7: ViewModelテストの移行
```prompt
既存のcomposeApp/src/androidUnitTest/kotlin/dev/dai/compose/visibility/sample/ui/viewmodel/
から以下のファイルを
feature/imagelist/src/androidUnitTest/kotlin/dev/dai/compose/visibility/sample/feature/imagelist/
に移行してください：

- viewmodel/ImageListViewModelTest.kt
- util/MainDispatcherRule.kt

fake/ ディレクトリ配下：
- FakeImageRepository.kt

パッケージ宣言を変更：
```kotlin
package dev.dai.compose.visibility.sample.feature.imagelist.viewmodel
// または
package dev.dai.compose.visibility.sample.feature.imagelist.util
// または
package dev.dai.compose.visibility.sample.feature.imagelist.fake
```

import文を更新：
```kotlin
import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem
import dev.dai.compose.visibility.sample.core.domain.image.repository.ImageRepository
import dev.dai.compose.visibility.sample.core.domain.image.usecase.GetImagesUseCase
import dev.dai.compose.visibility.sample.core.domain.image.usecase.LogImageVisibilityUseCase
```

#### ステップ5-8: settings.gradle.ktsに追加
```prompt
ルートのsettings.gradle.ktsに以下を追加してください：

```kotlin
include(":feature:imagelist")
```

#### ステップ5-9: ビルド確認
```prompt
./gradlew :feature:imagelist:build を実行してビルドエラーを確認してください。
テストも実行してください：
./gradlew :feature:imagelist:testDebugUnitTest
```

---

### Phase 6: composeApp モジュールの更新

#### ステップ6-1: composeApp/build.gradle.ktsの更新
```prompt
composeApp/build.gradle.kts を更新して、新しいモジュールへの依存を追加してください：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            // 新しいモジュールへの依存
            implementation(projects.core.common)
            implementation(projects.core.domain.image)
            implementation(projects.core.data.image)
            implementation(projects.feature.imageList)
            
            // 既存の依存関係は維持
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            
            implementation(libs.napier)
        }
        
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}
```

#### ステップ6-2: App.ktの更新
```prompt
composeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/App.kt
を更新して、新しいモジュール構成を反映してください：

```kotlin
package dev.dai.compose.visibility.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import dev.dai.compose.visibility.sample.di.appModule
import dev.dai.compose.visibility.sample.feature.imagelist.screen.ImageListScreen
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        MaterialTheme {
            ImageListScreen()
        }
    }
}
```

#### ステップ6-3: AppModuleの作成
```prompt
composeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/di/AppModule.kt
を作成して、全モジュールのDI定義を統合してください：

```kotlin
package dev.dai.compose.visibility.sample.di

import dev.dai.compose.visibility.sample.core.data.image.di.dataSourceModule
import dev.dai.compose.visibility.sample.core.data.image.di.repositoryModule
import dev.dai.compose.visibility.sample.feature.imagelist.di.imageListFeatureModule
import org.koin.dsl.module

val appModule = module {
    includes(
        dataSourceModule,
        repositoryModule,
        imageListFeatureModule
    )
}
```

#### ステップ6-4: 既存ファイルの削除
```prompt
composeApp/src/commonMain/kotlin/dev/dai/compose/visibility/sample/ 配下の
以下のディレクトリを削除してください（新しいモジュールに移行済み）：

- data/
- domain/
- ui/

composeApp/src/commonTest/ も削除してください（各モジュールにテストを移行済み）。
composeApp/src/androidUnitTest/ も削除してください（feature:imagelistに移行済み）。
```

#### ステップ6-5: MainActivity.ktの確認
```prompt
composeApp/src/androidMain/kotlin/dev/dai/compose/visibility/sample/MainActivity.kt
が正しく動作するか確認してください。
import文を更新する必要があるか確認してください。
```

#### ステップ6-6: MainViewController.ktの確認
```prompt
composeApp/src/iosMain/kotlin/dev/dai/compose/visibility/sample/MainViewController.kt
が正しく動作するか確認してください。
import文を更新する必要があるか確認してください。
```

---

### Phase 7: settings.gradle.ktsの最終更新

#### ステップ7-1: 全モジュールの登録確認
```prompt
ルートのsettings.gradle.ktsを以下の内容に更新してください：

```kotlin
rootProject.name = "ComposeVisibilitySample"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

// Core modules
include(":core:common")
include(":core:data:image")
include(":core:domain:image")

// Feature modules
include(":feature:imagelist")

// App module
include(":composeApp")
```

---

### Phase 8: ビルドと動作確認

#### ステップ8-1: 全体ビルド
```prompt
プロジェクト全体をビルドしてエラーがないか確認してください：

```bash
./gradlew clean build
```

エラーが発生した場合は、エラーメッセージを確認して修正してください。
```

#### ステップ8-2: テスト実行
```prompt
全モジュールのテストを実行してください：

```bash
# 全テスト実行
./gradlew test

# モジュール別テスト
./gradlew :core:domain:image:test
./gradlew :core:data:image:test
./gradlew :feature:imagelist:testDebugUnitTest
```

全てのテストがパスすることを確認してください。
```

#### ステップ8-3: Androidアプリの動作確認
```prompt
Android Studioから composeApp を実行して、アプリが正常に動作することを確認してください：

1. Android Studio の Run ボタンから composeApp を選択
2. エミュレーターまたは実機で実行
3. 画像リストが正しく表示されるか確認
4. スクロールして可視性ログが出力されるか確認（Logcat）
```

#### ステップ8-4: iOSアプリの動作確認
```prompt
iOSアプリも同様に動作確認してください：

1. Android Studio の Run ボタンから iosApp を選択
2. iOSシミュレーターで実行
3. 画像リストが正しく表示されるか確認
4. スクロールして可視性ログが出力されるか確認（Console）
```

---

### Phase 9: ドキュメントの更新

#### ステップ9-1: README.mdの更新
```prompt
README.md を更新して、新しいモジュール構成を反映してください。
「📁 プロジェクト構成」セクションを更新し、マルチモジュール構成を記載してください。
```

#### ステップ9-2: architecture.mdの更新
```prompt
docs/architecture.md を更新して、新しいモジュール構成とレイヤー設計を反映してください。
「5. モジュール/パッケージ構成」セクションを更新してください。
```

---

## ✅ 完了チェックリスト

すべてのフェーズが完了したら、以下を確認してください：

- [ ] build-logic が正しく設定されている
- [ ] core:common モジュールが作成されている
- [ ] core:domain:image モジュールが作成され、テストがパスする
- [ ] core:data:image モジュールが作成され、テストがパスする
- [ ] feature:imagelist モジュールが作成され、テストがパスする
- [ ] composeApp が更新され、不要なコードが削除されている
- [ ] settings.gradle.kts が正しく更新されている
- [ ] ./gradlew clean build が成功する
- [ ] 全テストがパスする（./gradlew test）
- [ ] Androidアプリが正常に動作する
- [ ] iOSアプリが正常に動作する
- [ ] README.md が更新されている
- [ ] docs/architecture.md が更新されている

---

## 🚨 トラブルシューティング

### よくある問題と解決方法

#### 問題1: TYPESAFE_PROJECT_ACCESSORS が使えない
```prompt
projects.core.common のような記法でエラーが出る場合：

settings.gradle.kts に以下が含まれているか確認：
```kotlin
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
```

それでも解決しない場合は、通常の文字列記法を使用：
```kotlin
implementation(project(":core:common"))
```

#### 問題2: Convention Plugin が見つからない
```prompt
build-logic/convention/build.gradle.kts の dependencies に
必要なプラグインが含まれているか確認してください：

```kotlin
dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}
```

gradle/libs.versions.toml にこれらの定義があるか確認してください。
```

#### 問題3: パッケージ名が解決できない
```prompt
import文でエラーが出る場合は、以下を確認してください：

1. 移行先のパッケージ宣言が正しいか
2. build.gradle.kts の dependencies に必要なモジュールが含まれているか
3. Gradle Sync を実行したか（Android Studio: File > Sync Project with Gradle Files）
```

#### 問題4: Koin の依存関係解決エラー
```prompt
Koin で "No definition found" エラーが出る場合：

1. AppModule.kt で全てのモジュールを includes しているか確認
2. 各DIモジュール（dataSourceModule, repositoryModule, featureModule）が正しく定義されているか確認
3. App.kt で KoinApplication を正しく初期化しているか確認
```

---

## 📝 補足事項

### Convention Pluginの詳細実装

各Convention Pluginの実装例は既存のcomposeApp/build.gradle.ktsを参考に、
共通設定を抽出して作成してください。

### テストの互換性

既存のテストコードは極力変更せず、import文とパッケージ宣言のみを更新することで
移行できるように設計されています。

### 段階的な移行

このプロンプトは段階的（Phase単位）に実行できるように設計されています。
各Phaseを完了してからビルドとテストを実行し、問題がないことを確認してから
次のPhaseに進むことを推奨します。

---

以上で、ComposeVisibilitySampleのマルチモジュール化リファクタリングが完了します！
