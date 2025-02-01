# Base for Activity

## Mô tả

## Cài đặt

### 1. Thêm các folder base vào dự án
- **BaseMainActivity.kt**: Chỉ sử dụng cho MainActivity.
- **BaseActivity.kt**: Có thể sử dụng cho tất cả các Activity trừ MainActivity.
- **BaseSubscriptionActivity.kt**: Chỉ sử dụng cho SubscriptionActivity.

### 2. Tạo file
Tạo file `MainActivity.kt` kế thừa BaseMainActivity.
Tạo file `SubscriptionActivity.kt` kế thừa BaseSubscriptionActivity.
Tạo các file Activity khác kế thừa BaseActivity.

```kotlin
class MainActivity : BaseMainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        
    }
}
```

```kotlin
class SubscriptionActivity : BaseSubscriptionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        
    }
}
```

```kotlin
class MainActivity : BaseMainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        
    }
}
```

### 3. BannerAds

- **showAdBanner.kt**: Chỉ sử dụng trong Activity.

### 3.1. Trong `MainActivity`
```kotlin
class MainActivity : BaseMainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hiển thị banner
        showAdBanner(Pair(AdKeyPosition.BANNER_AD_SC_MAIN, binding.bannerView))
    }
}
```

### 3.2. Trong `DifferentActivity`
```kotlin
class DifferentActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hiển thị banner
        showAdBanner(Pair(AdKeyPosition.BANNER_AD_SC_DIFFERENT, binding.bannerView))
    }
}
```

### 4. InterstitialAds

Có thể sử dụng trong tất cả các Activity kể cả MainActivity.

- **showAdInterstitial.kt**: Dùng để show ad InterstitialAds.
- **onClosedAdInterstitial.kt**: Sẽ hoạt động khi Ad InterstitialAds close.
- **cleanToFinish.kt**: Dùng để clean và finish Activity.

```kotlin
class MainActivity : BaseMainActivity() {
    override fun onClosedAdInterstitial(keyPosition: String) {
        super.onClosedAdInterstitial(keyPosition)
        if (keyPosition == AdKeyPosition.INTERSTITIAL_AD_SC_HOME.name) {
            val intent = Intent(this, DifferentActivity::class.java)
            startActivity(intent)
        }
        if (keyPosition == AdKeyPosition.INTERSTITIAL_AD_SC_EVENT.name) {
            cleanToFinish()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnShowAd.setOnClickListener {
            showAdInterstitial(AdKeyPosition.INTERSTITIAL_AD_SC_HOME)
        }
    }
}
```

### 5. RewardAds

Dùng để show ad RewardAds.
Có thể sử dụng trong tất cả các Activity kể cả MainActivity.

- **showAdInterstitial.kt**: Dùng để show ad InterstitialAds.

### 5.1. Reward Ads to NoAds for ? Days

Mặc định là 7 ngày.

```kotlin
class MainActivity : BaseMainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnNoAs.setOnClickListener {
            showAdReward(AdKeyPosition.REWARD_AD_REMOVE_ADS)
        }
    }
}
```

- **REWARD_AD_REMOVE_ADS**: Key dùng sử dụng reward ad để tắt ad dùng sai key hàm sẽ không hoạt động.

### 5.2. Reward Ads

```kotlin
class MainActivity : BaseMainActivity() {
    override fun onClosedAdReward(keyPosition: String) {
        super.onClosedAdReward(keyPosition)
        if (keyPosition == AdKeyPosition.REWARD_AD_SC_MAIN.name) {
            cleanToFinish() // ví dụ
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnNoAs.setOnClickListener {
            showAdReward(AdKeyPosition.REWARD_AD_SC_MAIN)
        }
    }
}
```

### 6. NativeAds

Dùng để show ad NativeAds.

### 6.1. Load NativeAds khi khởi tạo Activity

```kotlin
class MainActivity : BaseMainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showAdNative(Pair(AdKeyPosition.NATIVE_AD_SC_MAIN, binding.nativeAdView))
    }
}
```

--**showAdNative**: Mất 1 khoảng thời gian để load ad. Khi load xong mới hiển thị.

### 6.2. Load NativeAds ngoài Activity

Load NativeAds ngoài Activity, ví dụ trong Fragment, BottomSheet, ...

Trong `Activity` chứa `Fragment`, `BottomSheet`, ...

Preload NativeAds trước khi hiển thị.

```kotlin
class MainActivity : BaseMainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        
    }
}
```