package com.example.FurnitureShop.Util;

import com.example.FurnitureShop.DTO.Response.VerifyResponse;
import com.example.FurnitureShop.Model.User;
import com.example.FurnitureShop.Repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    private final UserRepository userRepository;

    protected static final String SIGNER_KEY = "f2a9fc56b9447c5d269999d2a9469a388bd1f23e86714c9968fc9139a3dcca60";

    public VerifyResponse verify(String token) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean valid = signedJWT.verify(verifier);

        return VerifyResponse.builder()
                .valid(valid && expiryTime.after(new Date()))
                .build();
    }

    public String generateToken(String phone){
        // Xac dinh thuat toan dung
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        User user = userRepository.findByPhoneWithRole(phone).get();

        // Tao claim de luu thong tin can dung
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(phone)
                .claim("roles", user.getRole().getName())
                .issuer("TranHieu")
                .jwtID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .build();

        // Tao payload, dong goi thong tin
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        // Tao jws hoan chinh sau khi co header + payload
        JWSObject jwsObject = new JWSObject(header, payload);


        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token, ", e);
            throw new RuntimeException(e);
        }
    }

    public String extractUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public String extractJti(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getJWTID();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public String extractRole(String token){
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getClaimAsString("roles");
        } catch (ParseException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public long getRemainingTime(String token) {
        try {
            Date exp = SignedJWT.parse(token)
                    .getJWTClaimsSet()
                    .getExpirationTime();
            return exp.getTime() - System.currentTimeMillis();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // verify signature
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            if (!signedJWT.verify(verifier)) {
                return false;
            }

            // verify expiration
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
            return exp != null && exp.after(new Date());

        } catch (Exception e) {
            return false;
        }
    }
}
