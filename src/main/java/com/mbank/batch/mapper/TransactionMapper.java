package com.mbank.batch.mapper;

import com.mbank.batch.dto.TransactionHistoryDto;
import com.mbank.batch.entity.TransactionHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  TransactionHistoryDto toDto(TransactionHistory en);

  TransactionHistory toEntity(TransactionHistoryDto dto);

}
