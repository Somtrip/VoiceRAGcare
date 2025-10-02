package com.customercare.repository;

import com.customercare.model.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
    List<FAQ> findByCategory(String category);
    @Query("SELECT f FROM FAQ f WHERE f.question LIKE %:keyword% OR f.answer LIKE %:keyword%")
    List<FAQ> findByKeyword(@Param("keyword") String keyword);
    @Query("SELECT f FROM FAQ f WHERE f.embedding IS NOT NULL")
    List<FAQ> findAllWithEmbeddings();
}
