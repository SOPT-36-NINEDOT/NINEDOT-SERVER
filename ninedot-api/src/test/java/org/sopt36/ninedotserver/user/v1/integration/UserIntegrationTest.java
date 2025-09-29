package org.sopt36.ninedotserver.user.v1.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.sopt36.ninedotserver.global.security.WithMockPrincipal;
import org.sopt36.ninedotserver.user.model.User;
import org.sopt36.ninedotserver.user.persistence.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockPrincipal()
    void 유저정보를_성공적으로_조회한다() throws Exception {
        // given
        User user = User.create(
            "홍길동",
            "test@example.com",
            "http://image.png",
            "2001.07.02",
            "DEVELOPER"
        );

        userRepository.save(user);

        // when & then
        mockMvc.perform(get("/api/v1/users/info")
                .with(user(user.getId().toString()))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("유저 정보를 성공적으로 조회했습니다."))
            .andExpect(jsonPath("$.data.id").value(user.getId()))
            .andExpect(jsonPath("$.data.name").value("홍길동"))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andExpect(jsonPath("$.data.profileImageUrl").value("http://image.png"));

    }
}
