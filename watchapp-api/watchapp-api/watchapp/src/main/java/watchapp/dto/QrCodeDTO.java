package watchapp.dto;

// Bir QR kodunu temsil eden veri.
// 'data' alanı, QR kodunun içine gömülecek olan metindir (örn: bir URL veya ödeme bilgisi).
// 'expiresAt' alanı, bu QR kodunun ne zamana kadar geçerli olduğunu belirtir.
public record QrCodeDTO(String data, String expiresAt) {}