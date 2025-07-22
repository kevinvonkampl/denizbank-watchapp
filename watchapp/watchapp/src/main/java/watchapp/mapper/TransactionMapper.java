package watchapp.mapper;

import org.mapstruct.*;
import watchapp.dto.TransactionDTO;
import watchapp.service.ObpApiClient;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    // ObpApiClient.Transaction nesnesini TransactionDTO'ya çevirir.
    TransactionDTO toDto(ObpApiClient.Transaction transaction);

    // Otomatik olarak bir listeyi de çevirebilir.
    List<TransactionDTO> toDtoList(List<ObpApiClient.Transaction> transactions);
}