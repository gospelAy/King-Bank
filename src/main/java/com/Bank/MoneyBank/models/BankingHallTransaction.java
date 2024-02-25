//package com.Bank.MoneyBank.models;
//
//import jakarta.persistence.Entity;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//public class BankingHallTransaction {
//    @Id
//    @GeneratedValue(strategy = UUID)
//    private String id;
//    @Enumerated(value = EnumType.STRING)
//    private TransactionType type;
//    private BigDecimal amount;
//    private String accountNumber;
//    private LocalDate date;
//    private LocalTime time;
//    @Enumerated(value = EnumType.STRING)
//    private TransactionStatus status = PENDING;
//    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(name = "officer_id")
//    private Officer officer;
//    private String processedBy;
//}
//
