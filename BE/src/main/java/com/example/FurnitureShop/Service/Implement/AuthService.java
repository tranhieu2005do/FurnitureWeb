package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.AuthRequest;
import com.example.FurnitureShop.DTO.Request.MailRequest;
import com.example.FurnitureShop.DTO.Request.OrderCreatedMailRequest;
import com.example.FurnitureShop.DTO.Request.UserRequest;
import com.example.FurnitureShop.DTO.Response.AuthResponse;
import com.example.FurnitureShop.DTO.Response.UserResponse;
import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Exception.GlobalExceptionHandler;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.User;
import com.example.FurnitureShop.Repository.UserRepository;
import com.example.FurnitureShop.Util.JwtUtil;
import com.nimbusds.jose.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig("auth")
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthResponse authenticate(AuthRequest authRequest){
        Optional<User> user = userRepository.findByPhoneWithRole(authRequest.getPhone());
        if(user.isEmpty()){
            log.info("User not found");
            throw new NotFoundException("User not found");
        }
        User n_user = user.get();

        boolean matches = passwordEncoder.matches(authRequest.getPassword(), n_user.getPassword());
        if(!matches){
            log.info("Wrong password");
            throw new AuthException("Wrong password");
        }

        String token = jwtUtil.generateToken(authRequest.getPhone());
        return AuthResponse.builder()
                .phone(n_user.getPhone())
                .token(token)
                .build();
    }

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendAuthenticatedMail(MailRequest mailRequest) throws MessagingException {
        log.info("Sending email to", mailRequest.getTo());
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

        Context context = new Context();
        context.setVariables(mailRequest.getProps());

        String html = templateEngine.process("client", context);

        helper.setFrom("dotranhieu925@gmai.com");
        helper.setTo(mailRequest.getTo());
        helper.setSubject(mailRequest.getSubject());
        helper.setText(html, true);

        javaMailSender.send(message);
    }

    @Async
    public void sendOrderCreatedMail(OrderCreatedMailRequest mailRequest) throws MessagingException {
        log.info("=== SENDING EMAIL ===");
        log.info("To: {}", mailRequest.getTo());
        log.info("Subject: {}", mailRequest.getSubject());
        log.info("Props: {}", mailRequest.getProps());
        log.info("OrderItems in sendMail: {}", mailRequest.getOrderItems());

        if (mailRequest.getOrderItems() == null) {
            log.error("OrderItems is NULL in sendOrderCreatedMail!");
            return;
        }
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

        Context context = new Context();
        context.setVariables(mailRequest.getProps());
        context.setVariable("subject", mailRequest.getSubject());
        context.setVariable("content", mailRequest.getContent());
//        context.setVariable("fullName", mailRequest.getProps().get("fullName"));
//        context.setVariable("address", mailRequest.getProps().get("address"));
//        context.setVariable("phone", mailRequest.getProps().get("phone"));
//        context.setVariable("email", mailRequest.getProps().get("email"));
//        context.setVariable("realPrice", mailRequest.getProps().get("realPrice"));
//        context.setVariable("totalPrice", mailRequest.getProps().get("totalPrice"));
//        context.setVariable("salePercentage", mailRequest.getProps().get("salePercentage"));
//        context.setVariable("trackNumber", mailRequest.getProps().get("trackNumber"));
//        context.setVariable("discount",  mailRequest.getProps().get("discount"));
        context.setVariable("orderItems", mailRequest.getOrderItems());

        String html = templateEngine.process("order_created", context);

        helper.setFrom("dotranhieu925@gmail.com");
        helper.setTo(mailRequest.getTo());
        helper.setSubject(mailRequest.getSubject());
        helper.setText(html, true);

        javaMailSender.send(message);
    }

    @CacheEvict(allEntries = true)
    public AuthResponse register (UserRequest request) {
        User new_user = new User();

        if(userRepository.existsByPhone(request.getPhone())){
            log.info("Phone exist");
            throw new AuthException("Số điện thoại này đã được dùng để đăng ký trước đó.");
        }

        new_user.setPhone(request.getPhone());
        new_user.setPassword(passwordEncoder.encode(request.getPassword()));
        new_user.setEmail(request.getEmail());
        new_user.setActive(true);
        new_user.setFullName(request.getFullName());
        new_user.setAddress(request.getAddress());
        new_user.setDateOfBirth(request.getBirthday());
        new_user.setCreatedAt(LocalDateTime.now());
        new_user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(new_user);

        return AuthResponse.builder()
                .phone(new_user.getPhone())
                .token(jwtUtil.generateToken(new_user.getPhone()))
                .build();
    }
}
