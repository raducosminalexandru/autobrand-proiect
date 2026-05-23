package com.example.cod_sursa_proiect.service;

import com.example.cod_sursa_proiect.entity.Product;
import com.example.cod_sursa_proiect.repository.ProductRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
public class ScrapingService {

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    public ScrapingService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.restTemplate = new RestTemplate();
    }

    private Double getCursValutarEurToRon() {
        try {
            System.out.println("Preluăm cursul valutar EUR -> RON...");
            String apiUrl = "https://api.frankfurter.app/latest?from=EUR&to=RON";

            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

            if (response != null && response.containsKey("rates")) {
                Map<String, Object> rates = (Map<String, Object>) response.get("rates");
                return Double.valueOf(rates.get("RON").toString());
            }
        } catch (Exception e) {
            System.err.println("Eroare la preluarea cursului valutar. Setăm o valoare de siguranță (5.0). Motiv: " + e.getMessage());
        }
        return 5.0;
    }

    @Scheduled(cron = "0 0 12-18 * * ?")
    public void loginAndScrape() {
        try {
            Double cursEurRon = getCursValutarEurToRon();
            System.out.println("Cursul valutar pentru azi: 1 EUR = " + cursEurRon + " RON");

            System.out.println("Încercăm autentificarea pe site...");

            Connection.Response loginResponse = Jsoup.connect("https://www.web-scraping.dev/api/login")
                    .data("username", "user123")
                    .data("password", "password")
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .execute();

            Map<String, String> cookies = loginResponse.cookies();
            System.out.println("Autentificare reușită!");

            int totalPages = 4;

            for (int i = 1; i <= totalPages; i++) {
                String pageUrl = "https://www.web-scraping.dev/products?category=consumables&page=" + i;
                System.out.println("\nAccesăm pagina " + i + ": " + pageUrl);

                Document doc = Jsoup.connect(pageUrl)
                        .cookies(cookies)
                        .get();

                Elements products = doc.select(".row.product");

                for (Element product : products) {
                    String imageUrl = product.select(".thumbnail img").attr("src");
                    String nume = product.select("h3 a").text();
                    String pretText = product.select(".price").text();
                    String descriere = product.select(".short-description").text();

                    Double pretEur = Double.parseDouble(pretText);

                    Double pretRon = Math.round(pretEur * cursEurRon * 100.0) / 100.0;

                    if (!productRepository.existsByNume(nume)) {
                        Product nouProdus = new Product(imageUrl, nume, pretEur, pretRon, cursEurRon, descriere);
                        productRepository.save(nouProdus);
                        System.out.println("Salvat în baza de date: " + nume + " | " + pretEur + " EUR (" + pretRon + " RON)");
                    } else {
                        System.out.println("Produsul '" + nume + "' exista deja. Sarim peste el.");
                    }
                }

                Thread.sleep(1000);
            }

            System.out.println("\nScraping-ul și salvarea au fost finalizate cu succes!");

        } catch (IOException e) {
            System.err.println("A apărut o eroare de rețea la scraping: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}