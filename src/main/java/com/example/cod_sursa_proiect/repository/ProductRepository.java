package com.example.cod_sursa_proiect.repository;

import com.example.cod_sursa_proiect.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByNume(String nume);

    List<Product> findByNumeContainingIgnoreCase(String nume);
}