package com.rahul.finflow.infrastructure.persistence.entity.tracker;


import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="budgets",
uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "category_id", "month_val", "year_val"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private UserEntity user; //many budgets can have the same user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id", nullable=false)
    private CategoryEntity category;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal amount;

    @Column(nullable=false, name="month_val")
    private Integer month;

    @Column(nullable = false, name="year_val")
    private Integer year;

    @CreatedDate
    @Column(name="created_at", nullable=false, updatable=false)
    private Instant createdAt;
}
