package watchapp.dto;

import java.util.List;

public record AccountDTO(String iban, double balance, List<TransactionDTO> lastThreeTransactions) {}