package com.example.FurnitureShop.Exception;

import com.example.FurnitureShop.DTO.Response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Bắt các lỗi mà số lượng sản phẩm không đáp ứng , lỗi liến quan đến sự cung cấp của doanh nghiệp
    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BussinessException b){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(b.getMessage())
                        .build());
    }

    //Bắt các lỗi từ Auth như đã tồn tại, hoặc thông tin không chính xác.
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthException(AuthException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder()
                        .statusCode(401)
                        .message(e.getMessage())
                        .build());
    }

    // Bắt các lỗi không tìm thấy thông tin
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(NotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .statusCode(404)
                        .message(e.getMessage())
                        .build());
    }

    //  Bắt RuntimeException (ví dụ bạn đang throw rất nhiều)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())   // ✅ trả message cho FE
                        .build());
    }

    // Bắt lỗi validate (@NotBlank, @NotNull...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .statusCode(400)
                        .message(message)
                        .build());
    }

    //  Bắt lỗi không mong muốn
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .statusCode(500)
                        .message("Lỗi hệ thống, vui lòng thử lại sau")
                        .build());
    }
}