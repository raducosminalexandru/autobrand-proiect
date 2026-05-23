package com.example.cod_sursa_proiect.controller;

import com.example.cod_sursa_proiect.service.PdfParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PdfController {

    @Autowired
    private PdfParsingService pdfParsingService;

    @PostMapping("/upload-invoice")
    public ResponseEntity<byte[]> uploadInvoice(@RequestParam("file") MultipartFile file) throws Exception {
        String csvContent = pdfParsingService.parseInvoiceToCsv(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"factura_extrasa.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvContent.getBytes());
    }
}