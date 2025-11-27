# TugisCAD - Android CAD Uygulaması

TugisCAD, Android platformu için geliştirilmiş profesyonel bir CAD (Bilgisayar Destekli Tasarım) uygulamasıdır. NETCAD masaüstü yazılımından esinlenerek mobil cihazlar için optimize edilmiştir.

## 🎯 Özellikler

### 📋 Proje Yönetimi
- Yeni proje oluşturma
- Proje açma ve kaydetme
- Farklı kaydetme
- Yazdırma desteği
- Proje özellikleri (ölçek, birim, projeksiyon)

### ✏️ Çizim Araçları
- **Temel Şekiller**
  - Nokta
  - Çizgi
  - Çoklu Çizgi (Polyline)
  - Daire
  - Yay
  - Elips
  - Dikdörtgen

- **İleri Düzey**
  - Metin ekleme
  - Tarama (Hatch) desenleri
  - Sembol yerleştirme
  - Ölçülendirme araçları

### 🔧 Düzenleme Araçları
- Seçim ve çoklu seçim
- Taşıma
- Kopyalama
- Döndürme
- Ölçeklendirme
- Aynalama
- Kes/Kırp
- Uzatma
- Paralel kopyalama (Offset)
- Obje silme

### 👁️ Görüntüleme
- Zoom In/Out (Yakınlaştır/Uzaklaştır)
- Pan (Kaydırma)
- Tümünü görüntüle
- Grid gösterimi
- Çoklu pencere desteği (planlı)

### 🎯 Yakalama Modları (Snap)
- Grid yakalama
- Uç nokta yakalama
- Orta nokta yakalama
- Merkez yakalama
- Kesişim noktası yakalama
- Dik nokta yakalama
- Teğet nokta yakalama
- En yakın nokta yakalama

### 📊 Sorgu Araçları
- Obje özellikleri sorgulama
- Alan hesaplama
- Koordinat sorgulama
- Mesafe ölçümü
- Açı ölçümü

### 🗂️ Tabaka (Layer) Sistemi
- 255 adete kadar tabaka desteği
- Her tabaka için:
  - Özel renk
  - Çizgi tipi
  - Görünürlük kontrolü
  - Kilitleme özelliği
- Aktif tabaka yönetimi

### 🎨 Çizgi Tipleri
- Sürekli (Continuous)
- Kesikli (Dashed)
- Noktalı (Dotted)
- Çizgi-Nokta (Dash-Dot)
- Merkez çizgisi (Center)
- Özel çizgi tipleri tanımlama

## 🏗️ Teknik Mimari

### Kullanılan Teknolojiler
- **Dil:** Kotlin
- **UI Framework:** Jetpack Compose
- **Minimum Android:** API 24 (Android 7.0)
- **Target Android:** API 34

### Kütüphaneler
- **AndroidX Core & AppCompat:** Temel Android bileşenleri
- **Material Design 3:** Modern UI bileşenleri
- **Jetpack Compose:** Declarative UI
- **Navigation Compose:** Ekran yönlendirme
- **Lifecycle & ViewModel:** MVVM mimarisi
- **Room Database:** Yerel veri saklama (planlı)
- **Kotlin Coroutines:** Asenkron işlemler
- **Gson:** JSON serialization

### Proje Yapısı
```
tugiscad/
├── app/
│   ├── src/main/
│   │   ├── java/com/tugi/tugiscad/
│   │   │   ├── data/
│   │   │   │   └── model/          # Veri modelleri
│   │   │   │       ├── CADObject.kt    # CAD objeleri
│   │   │   │       ├── CADProject.kt   # Proje modeli
│   │   │   │       └── Layer.kt        # Tabaka sistemi
│   │   │   ├── ui/
│   │   │   │   ├── components/     # UI bileşenleri
│   │   │   │   │   ├── CADTopBar.kt
│   │   │   │   │   └── DrawingToolbar.kt
│   │   │   │   ├── screen/         # Ekranlar
│   │   │   │   │   ├── CADCanvas.kt
│   │   │   │   │   └── MainScreen.kt
│   │   │   │   ├── theme/          # Tema ve renkler
│   │   │   │   └── viewmodel/      # ViewModels
│   │   │   │       └── CADViewModel.kt
│   │   │   └── MainActivity.kt
│   │   └── res/                    # Kaynaklar
│   └── build.gradle
└── README.md
```

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Android Studio Hedgehog | 2023.1.1 veya üzeri
- JDK 8 veya üzeri
- Android SDK 34
- Gradle 8.2.0

### Adımlar
1. Projeyi klonlayın:
```bash
git clone https://github.com/tugi64/tugiscad.git
```

2. Android Studio'da projeyi açın

3. Gradle senkronizasyonunu tamamlayın

4. Uygulamayı bir emülatör veya gerçek cihazda çalıştırın

## 📱 Kullanım

### Yeni Proje Oluşturma
1. Menüden **Proje → Yeni Proje** seçin
2. Proje adını ve özelliklerini girin
3. Çizime başlayın

### Çizim Yapma
1. Sol taraftaki araç çubuğundan bir çizim aracı seçin
2. Canvas üzerinde tıklayarak çizim yapın
3. Snap modlarını kullanarak hassas noktalara yakalayın

### Obje Düzenleme
1. Seçim aracını aktif edin
2. Düzenlemek istediğiniz objeyi seçin
3. Menüden veya toolbar'dan düzenleme işlemini seçin

## 🗺️ Yol Haritası

### v1.0 (Mevcut)
- ✅ Temel çizim araçları
- ✅ Tabaka sistemi
- ✅ Zoom ve Pan
- ✅ Snap modları
- ✅ Temel düzenleme araçları

### v1.1 (Planlanan)
- [ ] Dosya kaydetme/açma (DWG, DXF formatları)
- [ ] Veritabanı entegrasyonu
- [ ] Metin çizimi implementasyonu
- [ ] Tarama desenleri
- [ ] Sembol kütüphanesi

### v1.2 (Planlanan)
- [ ] Çoklu pencere desteği
- [ ] Raster görüntü desteği
- [ ] GPS entegrasyonu
- [ ] Harita servisleri entegrasyonu
- [ ] Bulut senkronizasyonu

### v2.0 (Gelecek)
- [ ] 3D çizim desteği
- [ ] AR (Artırılmış Gerçeklik) özellikleri
- [ ] Çoklu kullanıcı işbirliği
- [ ] Gelişmiş analiz araçları

## 👨‍💻 Geliştirici

**Tugi**
- GitHub: [@tugi64](https://github.com/tugi64)

## 📄 Lisans

Bu proje özel bir projedir. Tüm hakları saklıdır.

## 🙏 Teşekkürler

- NETCAD yazılımına referans alındığı için teşekkürler
- Android ve Jetpack Compose topluluğuna katkıları için teşekkürler

## 📞 İletişim

Sorularınız veya önerileriniz için GitHub Issues kullanabilirsiniz.

---

**Not:** Bu uygulama aktif geliştirme aşamasındadır. Özellikler ve işlevsellik zamanla geliştirilecektir.



