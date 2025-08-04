package com.jpmc.midascore.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private float incentive;

    // This is a JPA requirement, marked as protected to limit misuse
    protected TransactionRecord() {}

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = Instant.now();
        this.incentive = incentive;
    }

    public UserRecord getSender() {
        return sender;
    }

    public UserRecord getRecipient() {
        return recipient;
    }

    public float getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public float getIncentive() {
        return incentive;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TransactionRecord{" +
                "id=" + id +
                ", sender=" + sender.getName() +
                ", recipient=" + recipient.getName() +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", incentive=" + incentive +
                '}';
    }
}
