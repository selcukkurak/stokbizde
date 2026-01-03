# Stokbizde - Stok Yönetim Sistemi

Stokbizde, Java Swing tabanlı bir stok yönetim sistemi uygulamasıdır.

## Özellikler

- Java Swing tabanlı modern GUI
- Maven proje yapısı
- Kullanıcı dostu menü sistemi (Dosya, Düzen, Yardım)
- Temel layout ve durum çubuğu

## Gereksinimler

- Java 17 veya üzeri
- Apache Maven 3.6+

## Kurulum

Projeyi derlemek için:

```bash
mvn clean compile
```

JAR dosyası oluşturmak için:

```bash
mvn package
```

## Çalıştırma

Uygulamayı çalıştırmak için:

```bash
java -jar target/stokbizde-1.0-SNAPSHOT.jar
```

Ya da Maven ile:

```bash
mvn exec:java -Dexec.mainClass="com.stokbizde.StokbizdeApp"
```

## Proje Yapısı

```
stokbizde/
├── pom.xml                          # Maven yapılandırma dosyası
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── stokbizde/
│   │   │           └── StokbizdeApp.java  # Ana uygulama sınıfı
│   │   └── resources/               # Uygulama kaynakları
│   └── test/
│       └── java/                    # Test dosyaları
└── target/                          # Derlenmiş dosyalar (Maven tarafından oluşturulur)
```

## Lisans

MIT License - Detaylar için [LICENSE](LICENSE) dosyasına bakınız.

## Yazar

Selçuk Kurak - © 2025
