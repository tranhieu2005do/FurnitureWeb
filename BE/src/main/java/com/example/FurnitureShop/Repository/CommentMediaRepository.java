package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.CommentMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMediaRepository extends JpaRepository<CommentMedia,Long> {

    @Query("""
            SELECT c FROM CommentMedia c
            WHERE c.commentId = :id
            ORDER BY c.position
            """)
    List<CommentMedia> getCommentMediaByCommentId(@Param("id") Long commentId);
}
