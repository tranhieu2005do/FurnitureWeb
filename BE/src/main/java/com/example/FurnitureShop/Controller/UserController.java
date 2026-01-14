package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.AccountResquest;
import com.example.FurnitureShop.DTO.Request.ChangePasswordRequest;
import com.example.FurnitureShop.DTO.Request.UpdateUserRequest;
import com.example.FurnitureShop.DTO.Response.*;
import com.example.FurnitureShop.Service.Implement.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createUser(@RequestBody @Valid AccountResquest accountResquest) throws MessagingException {
        AccountResponse response = userService.createUser(accountResquest);
        return ResponseEntity.ok(ApiResponse.<AccountResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User created")
                .data(response)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
            Pageable pageable,
            @RequestParam(required = false) Integer roleId,
            @RequestParam(required = false) Boolean isActive){
        return ResponseEntity.ok(ApiResponse.<PageResponse<UserResponse>>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Get all users successfully")
                        .data(userService.fromPage(pageable, roleId, isActive))
                .build());
    }

    @PreAuthorize("@userService.isSelf(#userId)")
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long userId, @RequestBody @Valid UpdateUserRequest  updateUserRequest){
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Update successfully")
                .data(userService.updateUser(userId, updateUserRequest))
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> inActiveUser(@PathVariable Long userId){
        userService.InActiveUser(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Inactive user")
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @userService.isSelf(#id))")
    @GetMapping("/{id}/information")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInformation(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get user information")
                .data(userService.getUserInforById(id))
                .build());
    }

    @PreAuthorize("@userService.isSelf(#userId)")
    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable Long userId, @RequestBody @Valid ChangePasswordRequest changePasswordRequest){
        userService.changePassword(userId, changePasswordRequest);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{id}/customer-detail")
    public ResponseEntity<ApiResponse<CustomerDetailResponse>> getDetailCustomer(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.<CustomerDetailResponse>builder()
                .message("Get customer detail successfully")
                .data(userService.getDetailCustomer(id))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{id}/shipper-detail")
    public ResponseEntity<ApiResponse<ShipperDetailResponse>> getDetailShipper(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.<ShipperDetailResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get shipper detail successfully")
                .data(userService.getDetailShipper(id))
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{id}/inventory-log-staff-detail")
    public ResponseEntity<ApiResponse<InventoryLogStaffResponse>> getDetailInventoryLogStaff(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.<InventoryLogStaffResponse>builder()
                .data(userService.getInventoryLogStaff(id))
                .message("Get inventory log staff successfully")
                .statusCode(HttpStatus.OK.value())
                .build());
    }
}
