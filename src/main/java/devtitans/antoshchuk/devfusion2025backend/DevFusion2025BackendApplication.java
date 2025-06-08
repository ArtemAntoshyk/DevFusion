package devtitans.antoshchuk.devfusion2025backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@SpringBootApplication
public class DevFusion2025BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevFusion2025BackendApplication.class, args);
    }

    // Add global exception handler for diagnostics
    @ControllerAdvice
    @RestController
    public static class GlobalExceptionHandler {
        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleException(Exception e) {
            log.error("[GlobalExceptionHandler] Exception caught: ", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Internal Server Error",
                "message", e.getMessage()
            ));
        }
    }

}
