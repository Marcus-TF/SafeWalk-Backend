package com.safewalk.util;

import com.safewalk.model.Occurrence;
import com.safewalk.model.User;
import com.safewalk.model.enums.OccurrenceEnum;
import com.safewalk.model.enums.RiskLevelEnum;
import com.safewalk.repository.OccurrenceRepository;
import com.safewalk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User joao = userRepository.findByEmail("joao@test.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("João Silva")
                        .email("joao@test.com")
                        .password(passwordEncoder.encode("123456"))
                        .isActive(true)
                        .build()));

        User maria = userRepository.findByEmail("Marcust.mpf@gmail.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Marcus Túlio")
                        .email("Marcust.mpf@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .isActive(true)
                        .build()));

        User pedro = userRepository.findByEmail("pedro@test.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Pedro Oliveira")
                        .email("pedro@test.com")
                        .password(passwordEncoder.encode("123456"))
                        .isActive(true)
                        .build()));

        if (occurrenceRepository.count() == 0) {
            Occurrence occ1 = Occurrence.builder()
                    .type(OccurrenceEnum.fromDescription("Assalto"))
                    .description("Assalto à mão armada em frente ao mercado")
                    .latitude(-3.7319)
                    .longitude(-38.5267)
                    .location("Centro - Praça do Ferreira")
                    .risk(RiskLevelEnum.fromDescription("Alto"))
                    .user(joao)
                    .anonymous(false)
                    .isActive(true)
                    .build();

            Occurrence occ2 = Occurrence.builder()
                    .type(OccurrenceEnum.fromDescription("Assalto"))
                    .description("Assalto na calçada")
                    .latitude(-3.7315)
                    .longitude(-38.5265)
                    .location("Centro - Praça do Ferreira")
                    .risk(RiskLevelEnum.fromDescription("Alto"))
                    .user(maria)
                    .anonymous(false)
                    .isActive(true)
                    .build();

            Occurrence occ3 = Occurrence.builder()
                    .type(OccurrenceEnum.fromDescription("Assalto"))
                    .description("Assalto em frente à farmácia")
                    .latitude(-3.7321)
                    .longitude(-38.5269)
                    .location("Centro - Praça do Ferreira")
                    .risk(RiskLevelEnum.fromDescription("Alto"))
                    .user(pedro)
                    .anonymous(false)
                    .isActive(true)
                    .build();

            Occurrence occ4 = Occurrence.builder()
                    .type(OccurrenceEnum.fromDescription("Furto"))
                    .description("Furto de celular no transporte público")
                    .latitude(-3.7436)
                    .longitude(-38.5370)
                    .location("Benfica - Terminal do Benfica")
                    .risk(RiskLevelEnum.fromDescription("Médio"))
                    .user(joao)
                    .anonymous(true)
                    .isActive(true)
                    .build();

            Occurrence occ5 = Occurrence.builder()
                    .type(OccurrenceEnum.fromDescription("Vandalismo"))
                    .description("Pichação e quebra de vidros")
                    .latitude(-3.7438)
                    .longitude(-38.5372)
                    .location("Benfica - Terminal do Benfica")
                    .risk(RiskLevelEnum.fromDescription("Médio"))
                    .user(maria)
                    .anonymous(false)
                    .isActive(true)
                    .build();

            Occurrence occ6 = Occurrence.builder()
                    .type(OccurrenceEnum.fromDescription("Furto"))
                    .description("Furto de bicicleta")
                    .latitude(-3.7434)
                    .longitude(-38.5368)
                    .location("Benfica - Terminal do Benfica")
                    .risk(RiskLevelEnum.fromDescription("Médio"))
                    .user(pedro)
                    .anonymous(true)
                    .isActive(true)
                    .build();

            Occurrence occ7 = Occurrence.builder()
                    .type(OccurrenceEnum.fromDescription("Iluminação Precária"))
                    .description("Rua muito escura à noite")
                    .latitude(-3.7250)
                    .longitude(-38.5150)
                    .location("Meireles - Rua dos Tabajaras")
                    .risk(RiskLevelEnum.fromDescription("Baixo"))
                    .user(joao)
                    .anonymous(false)
                    .isActive(true)
                    .build();

            Occurrence occ8 = Occurrence.builder()
                    .type(OccurrenceEnum.fromDescription("Pessoa Suspeita"))
                    .description("Pessoa suspeita rondando carros")
                    .latitude(-3.7252)
                    .longitude(-38.5152)
                    .location("Meireles - Rua dos Tabajaras")
                    .risk(RiskLevelEnum.fromDescription("Baixo"))
                    .user(maria)
                    .anonymous(false)
                    .isActive(true)
                    .build();

            Occurrence occ9 = Occurrence.builder()
                    .type(OccurrenceEnum.fromDescription("Iluminação Precária"))
                    .description("Poste queimado")
                    .latitude(-3.7248)
                    .longitude(-38.5148)
                    .location("Meireles - Rua dos Tabajaras")
                    .risk(RiskLevelEnum.fromDescription("Baixo"))
                    .user(pedro)
                    .anonymous(true)
                    .isActive(true)
                    .build();

            Occurrence occ10 = Occurrence.builder()
                    .type(OccurrenceEnum.fromDescription("Iluminação Precária"))
                    .description("Poste de luz queimado há semanas")
                    .latitude(-3.7100)
                    .longitude(-38.5500)
                    .location("Monte Castelo")
                    .risk(RiskLevelEnum.fromDescription("Baixo"))
                    .user(joao)
                    .anonymous(true)
                    .isActive(true)
                    .build();

            occurrenceRepository.saveAll(Arrays.asList(
                    occ1, occ2, occ3, occ4, occ5, occ6, occ7, occ8, occ9, occ10
            ));
        }
    }
}
