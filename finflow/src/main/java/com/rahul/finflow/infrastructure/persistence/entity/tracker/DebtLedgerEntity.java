package com.rahul.finflow.infrastructure.persistence.entity.tracker;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "debt_ledgers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DebtLedgerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "lender_id", nullable = false)
    private UserEntity lender;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "borrower_id", nullable = false)
    private UserEntity borrower;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp @Column(updatable = false)
    private LocalDateTime createdAt;
}