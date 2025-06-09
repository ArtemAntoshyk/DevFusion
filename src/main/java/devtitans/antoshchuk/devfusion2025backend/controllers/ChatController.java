package devtitans.antoshchuk.devfusion2025backend.controllers;

import devtitans.antoshchuk.devfusion2025backend.models.chat.ChatMessage;
import devtitans.antoshchuk.devfusion2025backend.services.ChatService;
import devtitans.antoshchuk.devfusion2025backend.services.ChatService.ChatPreview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Endpoints for chat between company and seeker. All messages are stored in MongoDB.")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/send")
    @Operation(
        summary = "Send a message",
        description = "Send a message in the chat between a seeker and a company.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SendMessageRequest.class),
                examples = @ExampleObject(
                    value = """
                    {
                      \"seekerId\": 1,
                      \"companyId\": 2,
                      \"senderType\": \"SEEKER\",
                      \"message\": \"Hello, I am interested in your job!\"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Message sent successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatMessage.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          \"id\": \"664f1b2c8e4b2c6f1a2b3c4d\",
                          \"seekerId\": 1,
                          \"companyId\": 2,
                          \"senderType\": \"SEEKER\",
                          \"message\": \"Hello, I am interested in your job!\",
                          \"timestamp\": \"2024-06-10T12:34:56.000+00:00\"
                        }
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody SendMessageRequest request) {
        ChatMessage message = chatService.sendMessage(request.getSeekerId(), request.getCompanyId(), request.getSenderType(), request.getMessage());
        return ResponseEntity.ok(message);
    }

    @GetMapping("/history")
    @Operation(
        summary = "Get chat history",
        description = "Get the full chat history between a seeker and a company, sorted by timestamp ascending.",
        parameters = {
            @Parameter(name = "seekerId", description = "Seeker ID", required = true, example = "1"),
            @Parameter(name = "companyId", description = "Company ID", required = true, example = "2")
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Chat history returned successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatMessage.class, type = "array"),
                    examples = @ExampleObject(
                        value = """
                        [
                          {
                            \"id\": \"664f1b2c8e4b2c6f1a2b3c4d\",
                            \"seekerId\": 1,
                            \"companyId\": 2,
                            \"senderType\": \"SEEKER\",
                            \"message\": \"Hello, I am interested in your job!\",
                            \"timestamp\": \"2024-06-10T12:34:56.000+00:00\"
                          },
                          {
                            \"id\": \"664f1b2c8e4b2c6f1a2b3c4e\",
                            \"seekerId\": 1,
                            \"companyId\": 2,
                            \"senderType\": \"COMPANY\",
                            \"message\": \"Please send your CV.\",
                            \"timestamp\": \"2024-06-10T12:35:10.000+00:00\"
                          }
                        ]
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @RequestParam Integer seekerId,
            @RequestParam Integer companyId
    ) {
        List<ChatMessage> history = chatService.getChatHistory(seekerId, companyId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/user-chats")
    @Operation(
        summary = "Get all chats for user",
        description = "Get all chats for a seeker or a company by their id. Returns a list of unique chats with the last message for each chat. At least one of seekerId or companyId is required.",
        parameters = {
            @Parameter(name = "seekerId", description = "Seeker ID", required = false, example = "1"),
            @Parameter(name = "companyId", description = "Company ID", required = false, example = "2")
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of chats returned successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatPreview.class, type = "array"),
                    examples = @ExampleObject(
                        value = """
                        [
                          {
                            \"seekerId\": 1,
                            \"companyId\": 2,
                            \"lastMessage\": \"Hello, I am interested in your job!\",
                            \"senderType\": \"SEEKER\",
                            \"timestamp\": \"2024-06-10T12:34:56.000+00:00\"
                          },
                          {
                            \"seekerId\": 1,
                            \"companyId\": 3,
                            \"lastMessage\": \"Are you available for an interview?\",
                            \"senderType\": \"COMPANY\",
                            \"timestamp\": \"2024-06-10T13:00:00.000+00:00\"
                          }
                        ]
                        """
                    )
                )
            )
        }
    )
    public ResponseEntity<List<ChatPreview>> getUserChats(
            @RequestParam(required = false) Integer seekerId,
            @RequestParam(required = false) Integer companyId
    ) {
        if (seekerId != null) {
            return ResponseEntity.ok(chatService.getChatsForSeeker(seekerId));
        } else if (companyId != null) {
            return ResponseEntity.ok(chatService.getChatsForCompany(companyId));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Data
    public static class SendMessageRequest {
        @Parameter(description = "Seeker ID", required = true, example = "1")
        @Schema(description = "Seeker ID", example = "1")
        private Integer seekerId;
        @Parameter(description = "Company ID", required = true, example = "2")
        @Schema(description = "Company ID", example = "2")
        private Integer companyId;
        @Parameter(description = "Sender type (SEEKER or COMPANY)", required = true, example = "SEEKER")
        @Schema(description = "Sender type (SEEKER or COMPANY)", example = "SEEKER")
        private String senderType;
        @Parameter(description = "Message text", required = true, example = "Hello!")
        @Schema(description = "Message text", example = "Hello!")
        private String message;
    }
} 