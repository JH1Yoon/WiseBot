package com.develop.wisebot.domain.chat.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Schema(description = "페이지 응답 DTO")
public record PageResponse<T>(
        @ArraySchema(schema = @Schema(description = "요청 결과 리스트")) List<T> content,

        @Schema(description = "현재 페이지 번호", example = "0") int page,

        @Schema(description = "페이지 당 항목 수", example = "10") int size,

        @Schema(description = "전체 항목 수", example = "1") long totalElements,

        @Schema(description = "전체 페이지 수", example = "1") int totalPages,

        @Schema(description = "마지막 페이지 여부", example = "false") boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}