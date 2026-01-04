package com.stokbizde.service;

import com.stokbizde.model.District;
import com.stokbizde.model.Neighborhood;
import com.stokbizde.model.Province;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PttAddressScraper {
    private static final String BASE_URL = "https://postakodu.ptt.gov.tr";
    private Map<String, String> cookies;

    public PttAddressScraper() {
        this.cookies = new HashMap<>();
    }

    public List<Province> scrapeAllAddresses() throws IOException, InterruptedException {
        List<Province> provinces = new ArrayList<>();

        System.out.println("PTT Adres Verisi Çekme İşlemi Başlatılıyor...");

        // İlk sayfayı yükle ve çerezleri al
        Connection.Response initialResponse = Jsoup.connect(BASE_URL)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0")
                .method(Connection.Method.GET)
                .execute();

        cookies.putAll(initialResponse.cookies());
        Document doc = initialResponse.parse();

        // İl listesini al
        Element provinceSelect = doc.selectFirst("select#MainContent_DropDownList1");
        if (provinceSelect == null) {
            throw new IOException("İl listesi bulunamadı");
        }

        Elements provinceOptions = provinceSelect.select("option[value]:not([value=-1])");
        System.out.println("Toplam " + provinceOptions.size() + " il bulundu.");

        int provinceCount = 0;
        for (Element provinceOption : provinceOptions) {
            provinceCount++;
            String provinceId = provinceOption.attr("value");
            String provinceName = cleanText(provinceOption.text());

            System.out.println(String.format("[%d/%d] %s ili işleniyor...",
                provinceCount, provinceOptions.size(), provinceName));

            Province province = new Province(provinceId, provinceName);

            // İlçeleri çek
            List<District> districts = getDistricts(doc, provinceId);
            province.setDistricts(districts);

            provinces.add(province);

            // Sunucuyu yormamak için kısa bekleme
            Thread.sleep(2000);
        }

        System.out.println("\nİşlem tamamlandı!");
        return provinces;
    }

    private List<District> getDistricts(Document previousDoc, String provinceId)
            throws IOException, InterruptedException {
        List<District> districts = new ArrayList<>();

        // ViewState ve EventValidation değerlerini al
        String viewState = getInputValue(previousDoc, "__VIEWSTATE");
        String eventValidation = getInputValue(previousDoc, "__EVENTVALIDATION");
        String viewStateGenerator = getInputValue(previousDoc, "__VIEWSTATEGENERATOR");

        // İl seçimi için POST isteği
        Connection.Response districtResponse = Jsoup.connect(BASE_URL)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0")
                .cookies(cookies)
                .data("__EVENTTARGET", "ctl00$MainContent$DropDownList1")
                .data("__EVENTARGUMENT", "")
                .data("__VIEWSTATE", viewState)
                .data("__EVENTVALIDATION", eventValidation)
                .data("__VIEWSTATEGENERATOR", viewStateGenerator)
                .data("ctl00$MainContent$DropDownList1", provinceId)
                .method(Connection.Method.POST)
                .execute();

        cookies.putAll(districtResponse.cookies());
        Document districtDoc = districtResponse.parse();

        // İlçe listesini al
        Element districtSelect = districtDoc.selectFirst("select#MainContent_DropDownList2");
        if (districtSelect == null) {
            return districts;
        }

        Elements districtOptions = districtSelect.select("option[value]:not([value=-1])");
        System.out.println("  -> " + districtOptions.size() + " ilçe bulundu.");

        int districtCount = 0;
        for (Element districtOption : districtOptions) {
            districtCount++;
            String districtId = districtOption.attr("value");
            String districtName = cleanText(districtOption.text());

            System.out.println(String.format("    [%d/%d] %s ilçesi işleniyor...",
                districtCount, districtOptions.size(), districtName));

            District district = new District(districtId, districtName);

            // Mahalleleri çek
            List<Neighborhood> neighborhoods = getNeighborhoods(districtDoc, provinceId, districtId);
            district.setNeighborhoods(neighborhoods);

            districts.add(district);

            Thread.sleep(1000);
        }

        return districts;
    }

    private List<Neighborhood> getNeighborhoods(Document previousDoc, String provinceId, String districtId)
            throws IOException, InterruptedException {
        List<Neighborhood> neighborhoods = new ArrayList<>();

        // ViewState ve EventValidation değerlerini al
        String viewState = getInputValue(previousDoc, "__VIEWSTATE");
        String eventValidation = getInputValue(previousDoc, "__EVENTVALIDATION");
        String viewStateGenerator = getInputValue(previousDoc, "__VIEWSTATEGENERATOR");

        // İlçe seçimi için POST isteği
        Connection.Response neighborhoodResponse = Jsoup.connect(BASE_URL)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0")
                .cookies(cookies)
                .data("__EVENTTARGET", "ctl00$MainContent$DropDownList2")
                .data("__EVENTARGUMENT", "")
                .data("__VIEWSTATE", viewState)
                .data("__EVENTVALIDATION", eventValidation)
                .data("__VIEWSTATEGENERATOR", viewStateGenerator)
                .data("ctl00$MainContent$DropDownList1", provinceId)
                .data("ctl00$MainContent$DropDownList2", districtId)
                .method(Connection.Method.POST)
                .execute();

        cookies.putAll(neighborhoodResponse.cookies());
        Document neighborhoodDoc = neighborhoodResponse.parse();

        // Mahalle listesini al
        Element neighborhoodSelect = neighborhoodDoc.selectFirst("select#MainContent_DropDownList3");
        if (neighborhoodSelect == null) {
            return neighborhoods;
        }

        Elements neighborhoodOptions = neighborhoodSelect.select("option[value]:not([value=-1])");

        for (Element neighborhoodOption : neighborhoodOptions) {
            String neighborhoodId = cleanId(neighborhoodOption.attr("value"));
            String neighborhoodText = cleanText(neighborhoodOption.text());

            // Posta kodunu çıkar
            String postalCode = extractPostalCode(neighborhoodText);

            // Mahalle adından posta kodunu temizle
            String neighborhoodName = neighborhoodText.replaceAll("\\s*/\\s*.*$", "").trim();

            neighborhoods.add(new Neighborhood(neighborhoodId, neighborhoodName, postalCode));
        }

        System.out.println("      -> " + neighborhoods.size() + " mahalle eklendi.");

        return neighborhoods;
    }

    private String getInputValue(Document doc, String inputName) {
        Element input = doc.selectFirst("input[name=" + inputName + "]");
        return input != null ? input.attr("value") : "";
    }

    private String cleanText(String text) {
        if (text == null) return "";

        // HTML entity'lerini çöz
        text = text.trim();

        // Birden fazla boşluğu tek boşluğa çevir
        text = text.replaceAll("\\s+", " ");

        return text;
    }

    private String cleanId(String id) {
        if (id == null) return "";
        return id.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private String extractPostalCode(String text) {
        Pattern pattern = Pattern.compile("(\\d{5})");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

