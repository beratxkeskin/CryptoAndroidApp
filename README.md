# 🚀 CryptoPulse - Real-time Crypto Tracking & Portfolio App

<p center>
  <!-- Language Selector -->
  <b>Language / Dil:</b>
  <a href="#-english"> 🇬🇧 English</a> | 
  <a href="#-türkçe"> 🇹🇷 Türkçe</a>
</p>

---

## 🇬🇧 English

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.6.0-4285F4.svg?style=flat&logo=android)](https://developer.android.com/jetpack/compose)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-green.svg)](https://developer.android.com/topic/architecture)
[![Hilt](https://img.shields.io/badge/DI-Dagger%20Hilt-brightgreen.svg)](https://dagger.dev/hilt/)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28.svg?style=flat&logo=firebase)](https://firebase.google.com/)

**CryptoPulse** is a modern, high-performance Android application built with **100% Jetpack Compose**, **Clean Architecture**, and **SOLID Principles**. It provides real-time cryptocurrency price tracking, interactive financial charts, cloud-synced portfolio tracking, and instant push notifications.

### ✨ Key Features

* ⚡ **Hybrid Dual Data Stream (REST + WebSocket):**
  * **CoinGecko REST API:** Used for static metadata, coin icons, descriptions, and historical chart data.
  * **Binance WebSocket Stream (`wss://stream.binance.com`):** Delivers sub-second live price updates with real-time green/red price flash animations.
  * **Flow Throttling:** Uses `Flow.sample(300L)` to optimize UI recompositions and preserve CPU/battery life.

* 💼 **Live Portfolio Tracker:**
  * Real-time portfolio calculation using `CalculatePortfolioUseCase`.
  * Persisted on **Firebase Firestore** (`/users/{uid}/portfolio`).
  * Live valuation multiplying held crypto amounts (e.g. `10 ETH`, `0.25 BTC`) by real-time WebSocket prices.

* ⭐ **Cloud-Synced Watchlist (Favorites):**
  * Real-time synchronization across devices via Firebase Firestore (`/users/{uid}/favorites`).
  * Interactive star toggles on Home, Watchlist, and Detail screens.

* 📊 **Interactive Financial Charts (Vico):**
  * Dual-mode Line Chart and Candlestick (OHLC) Chart powered by Vico v3.
  * Touch markers and interactive price tooltips.

* 🔔 **FCM Push Notifications & Deep Linking:**
  * Foreground and background Firebase Cloud Messaging (FCM) push notifications.
  * Deep link navigation directly opening targeted coin detail screens.

* 👤 **Space-Themed Profile & Customizations:**
  * Profile overview with active portfolio summary, membership duration, and watchlist counter.
  * Interactive name editing dialog (`updateProfile`), password reset email dispatcher, and theme/locale toggle.

* 🌐 **Full Localization (EN / TR):**
  * Multi-language support for English 🇬🇧 and Turkish 🇹🇷 using native Android `stringResource` architecture.

* 📱 **Adaptive UI & Edge-to-Edge System Bar Compatibility:**
  * Built with dynamic `navigationBarsPadding()` ensuring 100% device compatibility across all Android manufacturers (Samsung, Xiaomi, Pixel, etc.) with 3-button or gesture navigation.

### 🏛️ Architecture & Tech Stack

```
                  ┌─────────────────────────────────────────┐
                  │          Presentation Layer             │
                  │   (Jetpack Compose, ViewModels, UI)    │
                  └────────────────────┬────────────────────┘
                                       │
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │              Domain Layer               │
                  │ (UseCases, Entities, Repository Interfaces)│
                  └────────────────────▲────────────────────┘
                                       │
                                       │
                  ┌────────────────────┴────────────────────┐
                  │               Data Layer                │
                  │(Repositories Impl, REST, Socket, Firestore)│
                  └─────────────────────────────────────────┘
```

* **Language:** Kotlin 100%
* **UI Framework:** Jetpack Compose, Material 3, Navigation Compose
* **Architecture:** Clean Architecture + MVVM + Repository Pattern
* **Dependency Injection:** Dagger Hilt
* **Asynchronous & Reactive:** Kotlin Coroutines, `Flow`, `StateFlow`
* **Networking:** Retrofit 2, OkHttp 4, Kotlinx Serialization
* **Real-time WebSockets:** OkHttp WebSocket Client
* **Cloud & Backend:** Firebase Authentication, Cloud Firestore, Cloud Messaging (FCM), Crashlytics, Analytics
* **Charts & Image Loading:** Vico Chart Library, Coil Compose
* **Testing:** JUnit4, MockK, Kotlinx Coroutines Test, Turbine

### 🚀 Getting Started

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/beratxkeskin/CryptoAndroidApp.git
   cd CryptoAndroidApp
   ```

2. **Firebase Setup:**
   * Create a project in [Firebase Console](https://console.firebase.google.com/).
   * Enable **Authentication** (Email/Password) and **Cloud Firestore**.
   * Add your Android app package (`com.example.cryptoandroidapp`) and download `google-services.json`.
   * Place `google-services.json` inside the `app/` folder.

3. **Build & Run:**
   ```bash
   ./gradlew compileDebugSources
   ```

4. **Run Unit Tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 🇹🇷 Türkçe

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.6.0-4285F4.svg?style=flat&logo=android)](https://developer.android.com/jetpack/compose)
[![Clean Architecture](https://img.shields.io/badge/Mimari-Clean%20%2B%20MVVM-green.svg)](https://developer.android.com/topic/architecture)
[![Hilt](https://img.shields.io/badge/DI-Dagger%20Hilt-brightgreen.svg)](https://dagger.dev/hilt/)
[![Firebase](https://img.shields.io/badge/Sunucu-Firebase-FFCA28.svg?style=flat&logo=firebase)](https://firebase.google.com/)

**CryptoPulse**, **%100 Jetpack Compose**, **Clean Architecture** ve **SOLID Prensipleri** ile geliştirilmiş, yüksek performanslı bir Android Kripto Takip ve Canlı Portföy uygulamasıdır. Canlı fiyat akışları, interaktif finansal grafikler, bulut senkronizasyonlu portföy takibi ve anlık push bildirimleri sunar.

### ✨ Öne Çıkan Özellikler

* ⚡ **Çift Kaynaklı Hibrit Veri Akışı (REST + WebSocket):**
  * **CoinGecko REST API:** Statik veriler, coin logoları, açıklamalar ve geçmiş grafik verilerini çekmek için kullanılır.
  * **Binance WebSocket Akışı (`wss://stream.binance.com`):** Milisaniyeler içinde anlık canlı fiyat güncellemeleri sunar ve ekranda yeşil/kırmızı canlı parlama efektleri oluşturur.
  * **Flow Throttling:** İşlemci ve pil tüketimini optimize etmek için `Flow.sample(300L)` operatörü kullanılmıştır.

* 💼 **Canlı Portföy Takip Modülü:**
  * `CalculatePortfolioUseCase` ile canlı portföy kâr/zarar ve bakiye hesabı.
  * **Firebase Firestore** üzerinde kullanıcıya özel saklama (`/users/{uid}/portfolio`).
  * Eldeki kripto miktarlarını (Örn: `10 ETH`, `0.25 BTC`) Binance WebSocket canlı fiyatıyla çarpan anlık canlı değer hesaplama motoru.

* ⭐ **Bulut Senkronizasyonlu Takip Listesi (Favoriler):**
  * Firebase Firestore (`/users/{uid}/favorites`) ile cihazlar arası anlık favori senkronizasyonu.
  * Ana Sayfa, Takip Listem ve Detay ekranlarında canlı yıldız butonları.

* 📊 **İnteraktif Finansal Grafikler (Vico):**
  * Vico v3 kütüphanesiyle çizilen Çizgi Grafik (Line Chart) ve Mum Grafik (Candlestick OHLC) modları.
  * Dokunmatik veri göstergesi (Marker) ve fiyat baloncukları.

* 🔔 **FCM Push Bildirimleri ve Deep Linking:**
  * Ön plan ve arka plan Firebase Cloud Messaging (FCM) bildirimleri.
  * Bildirime tıklandığında doğrudan ilgili kripto detay sayfasına yönlendiren Deep Link yapısı.

* 👤 **Uzay Temalı Profil & Özelleştirmeler:**
  * Canlı portföy özeti, üyelik süresi ve favori coin sayacı.
  * İsim düzenleme diyaloğu (`updateProfile`), şifre sıfırlama e-postası ve dil/tema değiştirici.

* 🌐 **Çoklu Dil Desteği (Türkçe 🇹🇷 / İngilizce 🇬🇧):**
  * Android `stringResource` altyapısı ile tam Türkçe ve İngilizce dil desteği.

* 📱 **Tüm Cihazlarla %100 Alt Menü Uyumluğu (`navigationBarsPadding`):**
  * Samsung, Xiaomi, Pixel vb. tüm Android cihazlarda 3 sistem tuşu veya jest çubuğu ile çakışmayan dinamik alt menü tasarımı.

### 🏛️ Mimari ve Teknolojiler

```
                  ┌─────────────────────────────────────────┐
                  │            Sunum Katmanı                │
                  │   (Jetpack Compose, ViewModels, UI)    │
                  └────────────────────┬────────────────────┘
                                       │
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │            Domain Katmanı               │
                  │ (UseCases, Entities, Repository Kontrat) │
                  └────────────────────▲────────────────────┘
                                       │
                                       │
                  ┌────────────────────┴────────────────────┐
                  │             Data Katmanı                │
                  │(Repositories Impl, REST, Socket, Firestore)│
                  └─────────────────────────────────────────┘
```

* **Yazılım Dili:** %100 Kotlin
* **Arayüz (UI):** Jetpack Compose, Material 3, Navigation Compose
* **Mimari:** Clean Architecture + MVVM + Repository Pattern
* **Bağımlılık Enjeksiyonu:** Dagger Hilt
* **Asenkron Yapı:** Kotlin Coroutines, `Flow`, `StateFlow`
* **Ağ (Networking):** Retrofit 2, OkHttp 4, Kotlinx Serialization
* **Canlı WebSocket:** OkHttp WebSocket Client
* **Bulut & Backend:** Firebase Auth, Cloud Firestore, FCM Push Notifications, Crashlytics, Analytics
* **Grafik ve Görsel:** Vico Chart Library, Coil Compose
* **Unit Test:** JUnit4, MockK, Coroutines Test, Turbine

### 🚀 Kurulum ve Çalıştırma

1. **Projeyi Klonlayın:**
   ```bash
   git clone https://github.com/beratxkeskin/CryptoAndroidApp.git
   cd CryptoAndroidApp
   ```

2. **Firebase Kurulumu:**
   * [Firebase Console](https://console.firebase.google.com/) üzerinden bir proje oluşturun.
   * Authentication (E-posta/Şifre) ve Firestore özelliklerini aktifleştirin.
   * `google-services.json` dosyasını indirip `app/` dizinine yerleştirin.

3. **Derleyin ve Çalıştırın:**
   ```bash
   ./gradlew compileDebugSources
   ```

4. **Unit Testleri Çalıştırın:**
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 📝 Lisans

Bu proje [MIT Lisansı](LICENSE) altında açık kaynak olarak sunulmaktadır.
