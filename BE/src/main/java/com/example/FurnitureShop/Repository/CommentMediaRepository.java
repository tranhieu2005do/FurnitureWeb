package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.CommentMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMediaRepository extends JpaRepository<CommentMedia,Long> {
}
