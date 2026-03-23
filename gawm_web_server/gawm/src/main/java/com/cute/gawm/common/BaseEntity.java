package com.cute.gawm.common;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class BaseEntity {
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;


    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
