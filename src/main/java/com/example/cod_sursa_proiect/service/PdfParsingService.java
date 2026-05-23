package com.example.cod_sursa_proiect.service;

import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.StringWriter;
import java.util.*;
import java.util.regex.*;

@Service
public class PdfParsingService {

    private static final Pattern PRODUCT_CODE_PATTERN = Pattern.compile(
            "\\b((?:[01][0-9]|2[0-3]))((?:[0-4][0-9]|5[0-3]))((?:[01][0-9]|2[0-6]))([A-F])\\b"
    );

    public String parseInvoiceToCsv(MultipartFile file) throws Exception {
        PDDocument document = Loader.loadPDF(file.getInputStream().readAllBytes());
        PDFTextStripper stripper = new PDFTextStripper();

        stripper.setSortByPosition(true);

        String fullText = stripper.getText(document);
        document.close();

        String normalizedText = fullText.replaceAll("\\s+", " ");

        Map<String, String[]> uniqueProducts = new LinkedHashMap<>();
        Matcher matcher = PRODUCT_CODE_PATTERN.matcher(normalizedText);

        while (matcher.find()) {
            String codMasina = matcher.group(1);
            String codArie = matcher.group(2);
            String codComp = matcher.group(3);
            String tip = matcher.group(4);
            String fullCode = codMasina + codArie + codComp + tip;

            String denumire = buildDenumire(codMasina, codArie, codComp, tip);

            int searchStart = matcher.end();
            int searchEnd = Math.min(searchStart + 200, normalizedText.length());
            String textChunk = normalizedText.substring(searchStart, searchEnd);

            Matcher dataMatcher = Pattern.compile("(-?\\d+\\.\\d{2})\\s+(?:RON\\s+)?(-?\\d+)\\s+(-?\\d+)").matcher(textChunk);

            if (dataMatcher.find()) {
                String pret = dataMatcher.group(1);
                String cantitateFacturata = dataMatcher.group(3);

                int cantAbsoluta = Math.abs(Integer.parseInt(cantitateFacturata));
                String cantitate = String.valueOf(cantAbsoluta);

                uniqueProducts.put(fullCode, new String[]{
                        fullCode, denumire, pret, "RON", cantitate
                });
            } else {

                uniqueProducts.putIfAbsent(fullCode, new String[]{
                        fullCode, denumire, "N/A", "RON", "0"
                });
            }
        }

        List<String[]> csvRows = new ArrayList<>();
        csvRows.add(new String[]{"Cod Produs", "Denumire", "Pret", "Moneda", "Cantitate"});
        csvRows.addAll(uniqueProducts.values());

        StringWriter writer = new StringWriter();
        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeAll(csvRows);
        }

        return writer.toString();
    }

    private String buildDenumire(String codMasina, String codArie, String codComp, String tip) {
        // Aceste câmpuri sunt opționale, regex-ul a fost dezvoltat în așa fel încât să simuleze un caz real, în care sunt mai multe
        // componente pe o factură
        Map<String, String> masini = Map.of("17", "AUTO PERSONALA");
        Map<String, String> arii = Map.of("28", "SISTEM DEMARARE");

        Map<String, String> componente = Map.of("12", "COMUTATOR ELECTRIC");
        Map<String, String> tipuri = Map.of(
                "A", "BOSCH", "B", "VALEO", "C", "HELLA",
                "D", "DELPHI", "E", "MAGNETI MARELLI", "F", "FEBI"
        );

        String numeComp = componente.getOrDefault(codComp, "COMP_" + codComp);
        String numeTip = tipuri.getOrDefault(tip, "TIP_" + tip);

        return numeComp + " " + numeTip;
    }
}