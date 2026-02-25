package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {

    @Query("""
            SELECT m FROM Message m
            WHERE m.conservation.id = :conversationId
                AND (:last IS NULL OR m.id < :last)
            ORDER BY m.id DESC 
            """)
    Page<Message> loadMessages(
            @Param("conversationId") Long conversationId,
            @Param("last") Long lastMessageId,
            Pageable pageable
    );
}
