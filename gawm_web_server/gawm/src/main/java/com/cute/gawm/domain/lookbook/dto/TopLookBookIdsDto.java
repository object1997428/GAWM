package com.cute.gawm.domain.lookbook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopLookBookIdsDto {
    private int lookbookId;
    private Integer likeCnt;
}
