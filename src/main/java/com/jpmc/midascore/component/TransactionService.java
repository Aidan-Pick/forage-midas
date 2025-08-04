package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionService(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            RestTemplate restTemplate
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        float amount = transaction.getAmount();

        // Checking if senderId and recipientID is valid
        if (sender == null || recipient == null) {
            System.out.println("Sender or recipient is null");
            return false;
        }

        // Checking if sender has sufficient funds
        if (sender.getBalance() < amount) {
            System.out.println("Insufficient funds for sender: "  + sender.getName());
            return false;
        }

        // At this point we have met the criteria for a transaction to be considered valid

        // make POST to Incentive API
        String endpoint = "http://localhost:8080/incentive";
        Incentive incentive = restTemplate.postForObject(endpoint, transaction, Incentive.class);
        float incentiveAmount = incentive != null ? incentive.getAmount() : 0f;

        // Adjust balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record the transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRepository.save(record);

        System.out.println("Transaction processed: " + record);
        return true; // transaction was a success
    }

}
