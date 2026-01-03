# Stokbizde Kullanıcı Arayüzü

## Ana Pencere Görünümü

### Menü Çubuğu
```
┌────────────────────────────────────────────────────────────┐
│ Dosya    Düzen    Yardım                                    │
└────────────────────────────────────────────────────────────┘
```

**Dosya Menüsü:**
- Yeni
- Aç
- Kaydet
- ──────────
- Çıkış

**Düzen Menüsü:**
- Kes
- Kopyala
- Yapıştır

**Yardım Menüsü:**
- Yardım Konuları
- ──────────
- Hakkında

### Ana Pencere (1024x768 piksel)
```
┌──────────────────────────────────────────────────────────────┐
│ Stokbizde - Stok Yönetim Sistemi                       ☐ ☒  │
├──────────────────────────────────────────────────────────────┤
│ Dosya    Düzen    Yardım                                     │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│                                                              │
│                                                              │
│                                                              │
│                  Stokbizde'ye Hoş Geldiniz                   │
│                   Stok Yönetim Sistemi                       │
│                                                              │
│                                                              │
│                                                              │
│                                                              │
│                                                              │
├──────────────────────────────────────────────────────────────┤
│ Hazır                                                        │
└──────────────────────────────────────────────────────────────┘
```

### Hakkında Diyalogu
```
┌─────────────────────────────────────┐
│            Hakkında             ✕   │
├─────────────────────────────────────┤
│                                     │
│         Stokbizde v1.0              │
│    Stok Yönetim Sistemi             │
│                                     │
│     © 2025 Selçuk Kurak             │
│                                     │
├─────────────────────────────────────┤
│              [ Tamam ]              │
└─────────────────────────────────────┘
```

## Teknik Detaylar

### Layout Yapısı
- **Ana Layout**: BorderLayout
  - **NORTH**: Menü Çubuğu (JMenuBar)
  - **CENTER**: Hoş Geldiniz Paneli (GridBagLayout)
  - **SOUTH**: Durum Çubuğu (FlowLayout)

### Renkler
- **Arka Plan**: RGB(240, 240, 240) - Açık Gri
- **Başlık Yazı Tipi**: Arial, Kalın, 28pt
- **Alt Başlık Yazı Tipi**: Arial, Normal, 16pt

### Özellikler
- System Look and Feel kullanımı
- Ekran ortasında açılma (setLocationRelativeTo(null))
- Event Dispatch Thread (EDT) üzerinde güvenli başlatma
- Çıkış işlemi için olay dinleyicisi (ActionListener)

### Pencere Boyutları
- **Genişlik**: 1024 piksel
- **Yükseklik**: 768 piksel
- **Kapatma İşlemi**: EXIT_ON_CLOSE

## Genişletme Planları

Gelecek sürümlerde eklenecek özellikler:
1. Ürün listesi tablosu
2. Ürün ekleme/düzenleme formları
3. Stok miktarı takibi
4. Raporlama özellikleri
5. Veritabanı entegrasyonu
