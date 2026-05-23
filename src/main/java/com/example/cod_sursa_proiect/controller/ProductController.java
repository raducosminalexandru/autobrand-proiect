package com.example.cod_sursa_proiect.controller;

import com.example.cod_sursa_proiect.entity.Product;
import com.example.cod_sursa_proiect.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository repo;

    @GetMapping("/")
    public String listaProduse(Model model,
                               @RequestParam(name = "sort", required = false) String sort,
                               @RequestParam(name = "search", required = false) String search) {

        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("produse", repo.findByNumeContainingIgnoreCase(search.trim()));
            model.addAttribute("searchKeyword", search); // Păstrăm cuvântul pentru a-l afișa în input
        }
        else if ("nume_asc".equals(sort)) {
            model.addAttribute("produse", repo.findAll(Sort.by(Sort.Direction.ASC, "nume")));
            model.addAttribute("searchKeyword", "");
        }
        else {
            model.addAttribute("produse", repo.findAll());
            model.addAttribute("searchKeyword", "");
        }

        return "lista";
    }

    @GetMapping("/edit/{id}")
    public String arataFormularEditare(@PathVariable Long id, Model model) {
        model.addAttribute("produs", repo.findById(id).orElseThrow());
        return "edit";
    }

    @PostMapping("/update")
    public String salveazaModificari(@ModelAttribute Product produsDinFormular) {
        Product produsExistent = repo.findById(produsDinFormular.getId())
                .orElseThrow(() -> new IllegalArgumentException("Produsul nu a fost găsit!"));

        produsExistent.setNume(produsDinFormular.getNume());
        produsExistent.setPretEur(produsDinFormular.getPretEur());
        produsExistent.setPretRon(produsDinFormular.getPretRon());

        repo.save(produsExistent);

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String stergeProdus(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/";
    }
}