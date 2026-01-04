# PTT Adres Verisi Entegrasyonu

## Genel Bakış

Bu sistem, Türkiye'deki tüm il/ilçe/mahalle verilerini PTT'nin posta kodu sisteminden otomatik olarak çeker ve MongoDB veritabanına kaydeder. Veriler bir kez yüklendikten sonra, uygulama başlatıldığında tekrar PTT'den çekilmez.

## Özellikler

✅ **Otomatik Veri Çekme**: Uygulama ilk kez başlatıldığında PTT'den tüm adres verilerini çeker
✅ **Akıllı Kontrol**: Veriler zaten yüklenmişse tekrar çekilmez
✅ **Asenkron Çalışma**: Veri çekme işlemi arka planda çalışır, UI'ı bloklamaz
✅ **Veritabanı Entegrasyonu**: Tüm veriler MongoDB'de saklanır
✅ **Şube/Depo Yönetimi**: Şube eklerken dinamik il/ilçe/mahalle seçimi
✅ **Posta Kodu Desteği**: Mahallelerle birlikte posta kodları da gösterilir

## Yeni Eklenen Dosyalar

### Model Sınıfları
- `src/main/java/com/stokbizde/model/Province.java` - İl verisi modeli
- `src/main/java/com/stokbizde/model/District.java` - İlçe verisi modeli
- `src/main/java/com/stokbizde/model/Neighborhood.java` - Mahalle verisi modeli

### DAO Sınıfları
- `src/main/java/com/stokbizde/dao/LocationDAO.java` - Adres verilerini yöneten DAO

### Servis Sınıfları
- `src/main/java/com/stokbizde/service/PttAddressScraper.java` - PTT'den veri çeken scraper
- `src/main/java/com/stokbizde/service/InitializationService.java` - Uygulama başlangıç servisi

## Güncellenen Dosyalar

- `pom.xml` - Jsoup ve Gson bağımlılıkları eklendi
- `src/main/java/com/stokbizde/MainApp.java` - Başlangıçta adres verisi kontrolü eklendi
- `src/main/java/com/stokbizde/ui/BranchManagementPanel.java` - Veritabanı verilerini kullanacak şekilde güncellendi

## Kullanım

### İlk Kurulum

1. Maven bağımlılıklarını yükleyin:
```bash
mvn clean install
```

2. MongoDB'nin çalıştığından emin olun (localhost:27017)

3. Uygulamayı başlatın:
```bash
mvn exec:java -Dexec.mainClass="com.stokbizde.MainApp"
```

### İlk Çalıştırma

Uygulama ilk kez başlatıldığında:

1. Veritabanında adres verisi olup olmadığı kontrol edilir
2. Veri yoksa PTT'den çekilir (bu işlem 30-60 dakika sürebilir)
3. Çekilen veriler MongoDB'ye kaydedilir
4. Sonraki çalıştırmalarda PTT'ye istek atılmaz, veriler veritabanından okunur

Konsol çıktısı:
```
=== Adres Verisi Kontrolü ===
⚠ Adres verileri veritabanında bulunamadı.
→ PTT'den veri çekme işlemi başlatılıyor...
→ Bu işlem uzun sürebilir (30-60 dakika), lütfen bekleyiniz...

PTT Adres Verisi Çekme İşlemi Başlatılıyor...
Toplam 81 il bulundu.
[1/81] Adana ili işleniyor...
  -> 15 ilçe bulundu.
    [1/15] Aladağ ilçesi işleniyor...
      -> 42 mahalle eklendi.
...
```

### Sonraki Çalıştırmalar

```
=== Adres Verisi Kontrolü ===
→ Veritabanında 81 il kaydı bulundu.
✓ Adres verileri zaten veritabanında mevcut.
✓ PTT'den veri çekme işlemi atlanıyor.
===================================
```

## Şube Ekleme

1. Ana menüden **Tanımlar** > **Şube/Depo** seçeneğine tıklayın
2. Sol taraftaki formda:
   - **Şube Adı**: Şubenin adını girin
   - **İl**: İl seçin (81 il)
   - **İlçe**: İlçe seçin (seçilen ile göre dinamik)
   - **Mahalle**: Mahalle seçin (seçilen ilçeye göre dinamik, posta koduyla birlikte)
3. **Ekle** butonuna tıklayın

## Veritabanı Yapısı

### Koleksiyonlar

**provinces**: İl verileri
```json
{
  "_id": ObjectId("..."),
  "provinceId": "1",
  "provinceName": "Adana",
  "districts": [
    {
      "districtId": "1439",
      "districtName": "Aladağ",
      "neighborhoods": [
        {
          "neighborhoodId": "...",
          "neighborhoodName": "Akören",
          "postalCode": "01720"
        }
      ]
    }
  ]
}
```

**location_metadata**: Veri yükleme durumu
```json
{
  "key": "address_data_initialized",
  "value": true,
  "timestamp": 1704384000000,
  "provinceCount": 81
}
```

## LocationDAO Metodları

### Kontrol Metodları
```java
boolean isAddressDataInitialized() // Veri yüklenmiş mi?
```

### Veri Kaydetme
```java
void saveAllProvinces(List<Province> provinces) // Toplu kayıt
```

### Veri Okuma
```java
List<Province> getAllProvinces() // Tüm illeri getir
Province getProvinceByName(String provinceName) // İsme göre il getir
Province getProvinceById(String provinceId) // ID'ye göre il getir
```

## Manuel Yenileme

Verileri manuel olarak yenilemek isterseniz (örneğin PTT yeni mahalleler eklediyse):

```java
InitializationService initService = new InitializationService();
initService.forceReloadAddressData();
```

Bu metod:
- Mevcut tüm verileri siler
- PTT'den yeniden çeker
- Veritabanına kaydeder

## Hata Yönetimi

### Veri Bulunamadı Hatası

Eğer uygulama başlatıldığında adres verisi yoksa ve PTT'den çekilemiyorsa:

1. MongoDB'nin çalıştığından emin olun
2. İnternet bağlantınızı kontrol edin
3. PTT web sitesinin erişilebilir olduğunu kontrol edin
4. Konsol loglarını inceleyin

### Bağlantı Hataları

Scraper sınıfı:
- Her istek arasında 1-2 saniye bekler (rate limiting)
- Bağlantı hataları için otomatik retry yapmaz (manuel restart gerekir)

## Performans

- **İlk yükleme**: ~30-60 dakika (81 il, ~900 ilçe, ~50.000 mahalle)
- **Sonraki başlatmalar**: <1 saniye (sadece veritabanı kontrolü)
- **Şube ekleme**: Anında (veriler bellekte)

## Güvenlik Notları

- PTT'nin robots.txt dosyasına uygun hareket edilmelidir
- Rate limiting uygulanmıştır (her istek arasında bekleme)
- Verilerin bir kez çekilip saklanması sunucuya gereksiz yük bindirmez

## Sorun Giderme

### Problem: "Veritabanında 0 il kaydı bulundu"
**Çözüm**: MongoDB çalışıyor mu kontrol edin, uygulama ilk yüklemeyi tamamlamamış olabilir

### Problem: "PTT'den veri çekilemedi"
**Çözüm**: İnternet bağlantısını ve PTT web sitesini kontrol edin

### Problem: "Mahalleler yüklenmiyor"
**Çözüm**: İlk olarak bir il, sonra bir ilçe seçtiğinizden emin olun

## Lisans

Bu proje Stokbizde uygulamasının bir parçasıdır.

