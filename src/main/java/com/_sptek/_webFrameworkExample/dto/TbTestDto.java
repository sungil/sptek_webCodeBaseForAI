package com._sptek._webFrameworkExample.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
TEST 테이블(sample 테이블)에 대한 DTO로 모든 DTO는 가능하면 builder 방식으로 사용하면 좋음
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbTestDto {
    private int c1;
    private int c2;
    private int c3;
}
