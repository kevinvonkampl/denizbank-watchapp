// Dosya Yolu: src/main/java/watchapp/mapper/TransactionMapper.java
package watchapp.mapper;

import org.mapstruct.*;
import watchapp.dto.TransactionDTO;
import watchapp.service.ObpApiClient;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    // ObpApiClient.ObpTransaction nesnesini TransactionDTO'ya çevirir.
    // 'ObpTransaction' olarak tam adını kullanıyoruz.
    TransactionDTO toDto(ObpApiClient.ObpTransaction transaction);

    // Otomatik olarak bir listeyi de çevirebilir.
    List<TransactionDTO> toDtoList(List<ObpApiClient.ObpTransaction> transactions);
}