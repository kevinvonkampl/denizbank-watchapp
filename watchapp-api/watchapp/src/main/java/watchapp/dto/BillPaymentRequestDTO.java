package watchapp.dto;

// Fatura ödeme isteğini temsil eden DTO.
// 'billerCode': Fatura kesen kurumun kodu (örn: "TURKCELL", "İSKİ").
// 'subscriberNumber': Müşterinin o kurumdaki abone numarası.
// 'amount': Ödenecek fatura tutarı.
public record BillPaymentRequestDTO(String billerCode, String subscriberNumber, double amount) {}