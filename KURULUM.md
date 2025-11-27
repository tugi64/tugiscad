# 🔧 TugisCAD Kurulum Rehberi

## ⚠️ Yeşil RUN Butonu Yanmıyorsa

Eğer Android Studio'da yeşil RUN butonu yanmıyorsa, aşağıdaki adımları takip edin:

### 1️⃣ Gradle Sync Yapın
1. Android Studio'yu açın
2. Menü → **File → Sync Project with Gradle Files** tıklayın
3. Veya üst toolbar'da **🐘 Sync** ikonuna tıklayın
4. Gradle sync tamamlanana kadar bekleyin (birkaç dakika sürebilir)

### 2️⃣ SDK Yolunu Kontrol Edin
1. Menü → **File → Project Structure** (Ctrl+Alt+Shift+S)
2. **SDK Location** sekmesine gidin
3. **Android SDK location** kontrol edin:
   - Varsayılan: `C:\Users\CASPER\AppData\Local\Android\Sdk`
4. SDK yüklü değilse:
   - **Android Studio → Tools → SDK Manager**
   - En az **Android 14 (API 34)** yükleyin
   - **Android SDK Build-Tools 34** yükleyin

### 3️⃣ Gradle Wrapper'ı Yeniden Oluşturun
Eğer hala sorun devam ediyorsa:

```bash
# Terminal'de (Android Studio içinde)
cd C:\Users\CASPER\AndroidStudioProjects\tugiscad
gradle wrapper --gradle-version 8.2
```

### 4️⃣ Cache Temizle ve Yeniden Başlat
1. Menü → **File → Invalidate Caches**
2. **Invalidate and Restart** seçin
3. Android Studio yeniden başlayacak

### 5️⃣ Build Yapın
1. Menü → **Build → Clean Project**
2. Sonra → **Build → Rebuild Project**
3. Build tamamlandıktan sonra yeşil RUN butonu aktif olacak

## ✅ Çalıştırma

### Emülatör ile:
1. **Device Manager** açın (sağ üst köşe)
2. Bir emülatör oluşturun (örn: Pixel 7, Android 14)
3. Emülatörü başlatın
4. Yeşil **RUN** ▶️ butonuna tıklayın

### Gerçek Cihaz ile:
1. Android cihazınızda **Geliştirici Seçenekleri** açın
2. **USB Debugging** aktif edin
3. Cihazı bilgisayara bağlayın
4. Cihazı üst dropdown'dan seçin
5. Yeşil **RUN** ▶️ butonuna tıklayın

## 🐛 Sorun Giderme

### "SDK location not found" Hatası
```
1. File → Project Structure → SDK Location
2. Android SDK location'ı ayarlayın
3. OK → Sync Project
```

### "Gradle sync failed" Hatası
```
1. Internet bağlantınızı kontrol edin
2. File → Settings → Build → Gradle
3. "Offline work" kapalı olduğundan emin olun
4. Sync Project with Gradle Files
```

### "Compilation failed" Hatası
```
1. Build → Clean Project
2. Build → Rebuild Project
3. Hala hata varsa: File → Invalidate Caches → Invalidate and Restart
```

## 📱 İlk Çalıştırma

Uygulama başarıyla çalıştığında:

1. ✅ **TugisCAD** splash ekranı göreceksiniz
2. ✅ Ana ekranda **koyu tema** ile CAD arayüzü açılır
3. ✅ Sol tarafta **çizim araçları**
4. ✅ Üstte **menü çubuğu** (Proje, Çiz, Düzenle, vb.)
5. ✅ Sağda **tabaka yöneticisi**
6. ✅ Ortada **çizim canvas'ı** (grid ile)

### İlk Çizim:
1. Sol toolbar'dan **Çizgi** aracını seçin
2. Canvas üzerinde bir noktaya tıklayın
3. İkinci bir noktaya tıklayın
4. ✨ Çizginiz oluştu!

## 🎯 Gereksinimler

- ✅ Android Studio Hedgehog | 2023.1.1 veya üzeri
- ✅ JDK 8 veya üzeri
- ✅ Android SDK 34
- ✅ Gradle 8.2.0
- ✅ Minimum 4 GB RAM
- ✅ 2 GB boş disk alanı

## 📞 Yardım

Sorun devam ediyorsa:
1. Android Studio → **Help → Show Log in Explorer**
2. `idea.log` dosyasını kontrol edin
3. Hata mesajlarını GitHub Issues'a bildirin

---

**🚀 Kolay gelsin! TugisCAD ile harika CAD çizimleri yapın!**

