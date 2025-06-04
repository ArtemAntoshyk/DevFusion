package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
} 