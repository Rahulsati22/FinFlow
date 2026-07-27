package com.rahul.finflow.infrastructure.persistence.entity.tracker;


import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="expenses")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private UserEntity user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id")
    private CategoryEntity category;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Enumerated(EnumType.STRING)
    @Column(name = "split_type")
    private SplitType splitType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;


    @Column(nullable = false, length = 255)
    private String description;


    @Column(name="expense_date", nullable=false)
    private LocalDate expenseDate;

    @CreatedDate
    @Column(name="created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
