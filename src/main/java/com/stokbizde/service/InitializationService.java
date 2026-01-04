package com.stokbizde.service;

import com.stokbizde.dao.CompanyDAO;
import com.stokbizde.dao.LocationDAO;
import com.stokbizde.model.Province;

import java.util.List;

/**
 * Uygulama başlangıcında bir kerelik çalışacak initialization işlemlerini yönetir
 */
public class InitializationService {
    private final LocationDAO locationDAO;
    private final CompanyDAO companyDAO;
    private final PttAddressScraper pttScraper;

    public InitializationService() {
        this.locationDAO = new LocationDAO();
        this.companyDAO = new CompanyDAO();
        this.pttScraper = new PttAddressScraper();
    }

    /**
     * Şirket bilgileri kurulumu yapılmış mı kontrol eder
     */
    public boolean isCompanySetupCompleted() {
        return companyDAO.isInitialized();
    }

    /**
     * Uygulama başlangıcında çağrılır
     * Eğer adres verileri henüz yüklenmediyse PTT'den çeker ve veritabanına kaydeder
     */
    public void initializeAddressData() {
        try {
            System.out.println("=== Adres Verisi Kontrolü ===");

            // Önce veritabanında veri var mı kontrol et
            if (locationDAO.isAddressDataInitialized()) {
                System.out.println("✓ Adres verileri zaten veritabanında mevcut.");
                System.out.println("✓ PTT'den veri çekme işlemi atlanıyor.");
                System.out.println("===================================\n");
                return; // Veriler varsa metoddan çık, PTT'ye istek atma
            }

            // Veriler yoksa PTT'den çek
            System.out.println("⚠ Adres verileri veritabanında bulunamadı.");
            System.out.println("→ PTT'den veri çekme işlemi başlatılıyor...");
            System.out.println("→ Bu işlem uzun sürebilir (30-60 dakika), lütfen bekleyiniz...\n");

            // PTT'den verileri çek
            List<Province> provinces = pttScraper.scrapeAllAddresses();

            if (provinces == null || provinces.isEmpty()) {
                System.err.println("✗ HATA: PTT'den hiç veri çekilemedi!");
                System.err.println("===================================\n");
                return;
            }

            // Veritabanına kaydet
            locationDAO.saveAllProvinces(provinces);

            System.out.println("\n✓ Adres verileri başarıyla yüklendi ve veritabanına kaydedildi!");
            System.out.println("✓ Toplam " + provinces.size() + " il bilgisi kaydedildi.");
            System.out.println("===================================\n");

        } catch (Exception e) {
            System.err.println("\n✗ HATA: Adres verileri yüklenirken bir hata oluştu!");
            System.err.println("✗ Hata mesajı: " + e.getMessage());
            System.err.println("===================================\n");
            e.printStackTrace();
        }
    }

    /**
     * Adres verilerini manuel olarak yeniden yüklemek için kullanılır
     * Dikkat: Mevcut verileri siler ve PTT'den yeniden çeker
     */
    public void forceReloadAddressData() {
        System.out.println("=== Adres Verisi Zorla Yenileme ===");
        System.out.println("Mevcut veriler silinecek ve PTT'den yeniden çekilecek...\n");

        try {
            List<Province> provinces = pttScraper.scrapeAllAddresses();
            locationDAO.saveAllProvinces(provinces);

            System.out.println("\n✓ Adres verileri başarıyla yenilendi!");
            System.out.println("===================================\n");

        } catch (Exception e) {
            System.err.println("HATA: Adres verileri yenilenirken bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

