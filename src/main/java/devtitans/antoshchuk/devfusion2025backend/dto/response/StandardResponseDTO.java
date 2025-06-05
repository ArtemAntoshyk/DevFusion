package devtitans.antoshchuk.devfusion2025backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> StandardResponseDTO<T> success(T data) {
        return new StandardResponseDTO<>(true, "Success", data);
    }

    public static <T> StandardResponseDTO<T> error(String message) {
        return new StandardResponseDTO<>(false, message, null);
    }
} 