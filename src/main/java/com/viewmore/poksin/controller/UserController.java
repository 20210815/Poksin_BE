package com.viewmore.poksin.controller;

import com.viewmore.poksin.code.ErrorCode;
import com.viewmore.poksin.code.SuccessCode;
import com.viewmore.poksin.dto.RegisterDTO;
import com.viewmore.poksin.dto.UpdateUserDTO;
import com.viewmore.poksin.dto.UserResponseDTO;
import com.viewmore.poksin.jwt.JWTUtil;
import com.viewmore.poksin.response.ResponseDTO;
import com.viewmore.poksin.service.UserService;
import com.viewmore.poksin.util.TokenErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REGISTER, null));
    }

    @GetMapping("/mypage")
    public ResponseEntity<ResponseDTO> mypage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDTO response = userService.mypage(username);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, response));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 헤더에서 refresh키에 담긴 토큰을 꺼냄
        String refreshToken = request.getHeader("refresh");

        if (refreshToken == null) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_MISSING);
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
        }

        String type = jwtUtil.getType(refreshToken);
        if (!type.equals("refreshToken")) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // redis 연결하면 추가 예정
//        Optional<RefreshEntity> isExist = refreshRedisRepository.findById(refreshToken);
//        if (isExist.isEmpty()) {
//            TokenErrorResponse.sendErrorResponse(response, "토큰이 만료되었습니다.");
//        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 새로운 Access token과 refreshToken 생성
        String newAccessToken = jwtUtil.createJwt("accessToken", username, role, 600000L);
        String newRefreshToken = jwtUtil.createJwt("refreshToken", username, role, 600000L);

        response.setHeader("accessToken", "Bearer " + newAccessToken);
        response.setHeader("refreshToken", "Bearer " + newRefreshToken);

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REISSUE, null));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateUser(@RequestBody  UpdateUserDTO updateUserDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDTO response = userService.updateUser(username, updateUserDTO);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_UPDATE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_UPDATE_USER, response));
    }
}
