package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.*;
import com.example.FurnitureShop.DTO.Response.*;
import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.Conservation;
import com.example.FurnitureShop.Model.CustomUserDetails;
import com.example.FurnitureShop.Model.Role;
import com.example.FurnitureShop.Model.User;
import com.example.FurnitureShop.Repository.ConservationRepository;
import com.example.FurnitureShop.Repository.RoleRepository;
import com.example.FurnitureShop.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service("userService")
@RequiredArgsConstructor
@CacheConfig("users")
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderService orderService;
    private final InventoryLogService inventoryLogService;
    private final ConservationRepository conservationRepository;

//    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @CacheEvict(allEntries = true)
    public AccountResponse createUser(AccountResquest accountResquest) {
        if(userRepository.existsByPhone(accountResquest.getPhone())) {
            throw new AuthException("Số điện thoại này đã được đăng ký");
        }

        Role role = roleRepository.findById(accountResquest.getRoleId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy vai trò tương ứng"));

        User new_user = User.builder()
                .phone(accountResquest.getPhone())
                .password(passwordEncoder.encode(accountResquest.getPassword()))
                .role(role)
                .createdAt(LocalDateTime.now())
                .dateOfBirth(accountResquest.getDateOfBirth())
                .fullName(accountResquest.getFullName())
                .isActive(true)
                .email(accountResquest.getEmail())
                .updatedAt(LocalDateTime.now())
                .address(accountResquest.getAddress())
                .build();
        userRepository.save(new_user);

        Conservation newConservation = Conservation.builder()
                .customer(new_user)
                .createdAt(LocalDateTime.now())
                .build();
        conservationRepository.save(newConservation);

        MailRequest mailRequest = MailRequest.builder()
                .subject("XÁC NHẬN NGƯỜI DÙNG ĐĂNG KÝ TÀI KHOẢN THÀNH CÔNG")
                .to(accountResquest.getEmail())
                .content("WELCOME")
                .build();

        Map<String,Object> props = new HashMap<>();
        props.put("name", accountResquest.getFullName());
        props.put("username", accountResquest.getPhone());
        props.put("password", accountResquest.getPassword());
        props.put("address", accountResquest.getAddress());

        mailRequest.setProps(props);
//        kafkaTemplate.send("Mail",  mailRequest);
        return AccountResponse.fromUser(new_user);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            key = "T(java.util.Objects).hash(#pageable.pageNumber, #pageable.pageSize, #roleId, #isActive)"
    )
    public PageResponse<UserResponse> fromPage(Pageable pageable, Integer roleId, Boolean isActive){
        return PageResponse.fromPage(
                userRepository.fromPage(pageable, roleId, isActive)
                                .map(UserResponse::fromEntity)
        );
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void InActiveUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // soft delete
    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteUser(Long id){
        User existingUser = userRepository.findById(id).orElse(null);
        if(existingUser == null){
            throw new NotFoundException("User not found");
        }
        existingUser.setActive(false);
        userRepository.save(existingUser);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            key = "T(java.util.Objects).hash(#pageable.pageNumber, #pageable.pageSize, #pageable.sort)"
    )
    public Page<UserResponse> findByActive(Pageable pageable){
        Page<User> users = userRepository.findByActive(pageable);
        return users.map(UserResponse::fromEntity);
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public UserResponse updateUser(Long userId, UpdateUserRequest userRequest) {
        User currentUser = userRepository.findById(userId).orElse(null);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng với id: " + userId);
        }
        if (userRequest.getFullName() != null && !userRequest.getFullName().equals(currentUser.getFullName())) {
            currentUser.setFullName(userRequest.getFullName());
        }

        if (userRequest.getEmail() != null && !userRequest.getEmail().equals(currentUser.getEmail())) {
            if (!existsByEmail(userRequest.getEmail())) {
                currentUser.setEmail(userRequest.getEmail());
            } else {
                throw new AuthException("Email này đã có người dùng.");
            }
        }

        if (userRequest.getPhone() != null && !userRequest.getPhone().equals(currentUser.getPhone())) {
            if (!userRepository.existsByPhone(userRequest.getPhone())) {
                currentUser.setPhone(userRequest.getPhone());
            } else {
                throw new AuthException("Số điện thoại này đã được dùng.");
            }
        }

        if (userRequest.getDateOfBirth() != null) {
            currentUser.setDateOfBirth(userRequest.getDateOfBirth());
        }

        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);
        return UserResponse.fromEntity(currentUser);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#id")
    public UserResponse getUserInforById(Long id){
        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new NotFoundException("User not found");
        }
        return UserResponse.fromEntity(user);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void changePassword(Long userId, ChangePasswordRequest cpRequest) {
        User currentUser = userRepository.findById(userId).orElse(null);

        if (currentUser == null) {
            throw new NotFoundException("User not found");
        }

        if(!currentUser.isActive()){
            throw new AuthException("Tài khoản đã bị khóa.");
        }

        if(passwordEncoder.matches(cpRequest.getOldPassword(), currentUser.getPassword())){
            currentUser.setPassword(passwordEncoder.encode(cpRequest.getNewPassword()));
        }
        else{
            throw new AuthException("Mật khẩu cũ không chính xác");
        }
        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'customer_' + #customerId")
    public CustomerDetailResponse getDetailCustomer(Long customerId){
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay nguoi dung"));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        PageResponse<OrderResponse> totalOrders = orderService.getOrdersByUserId(customerId, pageable, null, null);

        return CustomerDetailResponse.builder()
                .name(customer.getFullName())
                .address(customer.getAddress())
                .dateOfBirth(customer.getDateOfBirth())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .totalOrders(totalOrders)
                .build();
    }

    @Cacheable(key = "'shipper_' + #shipperId")
    @Transactional(readOnly = true)
    public ShipperDetailResponse getDetailShipper(Long shipperId){
        User shipper = userRepository.findById(shipperId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay nguoi dung"));

        if(shipper.getRole().getId() != 3){
            throw new AuthException("Nguoi dung khong thuoc nhom Shipper&Installer");
        }

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        PageResponse<OrderOfShipperResponse> ordersOfShipper = orderService.getOrdersInstalledByShipperId(pageable,shipperId);

        return ShipperDetailResponse.builder()
                .shipperName(shipper.getFullName())
                .shipperPhone(shipper.getPhone())
                .shipperEmail(shipper.getEmail())
                .orderOfShipper(ordersOfShipper)
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'logStaff_' + #inventoryLogStaffId")
    public InventoryLogStaffResponse getInventoryLogStaff(Long inventoryLogStaffId){
        User invetoryLogStaff = userRepository.findById(inventoryLogStaffId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay nguoi dung"));

        if(invetoryLogStaff.getRole().getId() != 5){
            throw new AuthException("Nguoi dung khong thuoc nhom Ware House Manager");
        }
        Object logs = inventoryLogService.getLogs(
                inventoryLogStaffId,
                null,
                null,
                null,
                null
        );

        List<InventoryLogResponse> totalInventoryLogs;

        if (logs instanceof List<?>) {
            totalInventoryLogs = (List<InventoryLogResponse>) logs;
        } else {
            throw new IllegalStateException("Expected List<InventoryLogResponse> but got " + logs.getClass());
        }

        return InventoryLogStaffResponse.builder()
                .name(invetoryLogStaff.getFullName())
                .phone(invetoryLogStaff.getPhone())
                .email(invetoryLogStaff.getEmail())
                .totalInventoryLogs(totalInventoryLogs)
                .build();
    }

    public boolean isSelf(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return false;
        }

        return userDetails.getUserId().equals(id);
    }
}
