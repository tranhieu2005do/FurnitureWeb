package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.MessageMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageMediaRepository extends JpaRepository<MessageMedia,Long> {

    @Query("""
            SELECT md FROM MessageMedia md
            WHERE md.message.id = :id
            """)
    List<MessageMedia> findByMessageId(@Param("id") Long messageId);
}
