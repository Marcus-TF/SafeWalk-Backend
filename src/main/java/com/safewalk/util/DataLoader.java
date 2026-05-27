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
@Profile("prod")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User joao = userRepository.findByEmailIgnoreCase("joao@test.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("João Silva")
                        .email("joao@test.com")
                        .password(passwordEncoder.encode("123456"))
                        .isActive(true)
                        .build()));

        User maria = userRepository.findByEmailIgnoreCase("Marcust.mpf@gmail.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Marcus Túlio")
                        .email("Marcust.mpf@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .isActive(true)
                        .build()));

        User pedro = userRepository.findByEmailIgnoreCase("pedro@test.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Pedro Oliveira")
                        .email("pedro@test.com")
                        .password(passwordEncoder.encode("123456"))
                        .isActive(true)
                        .build()));

        occurrenceRepository.deleteAll();

        // Hotspot 1 (Alto Risco) - Centralizado no CEP
        Occurrence occ1 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Assalto"))
                .description("Assalto na rua principal")
                .latitude(-3.8028)
                .longitude(-38.5650)
                .location("Rua Dr. Paulo de Mello Machado")
                .risk(RiskLevelEnum.fromDescription("Alto"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ2 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Assalto"))
                .description("Assalto à mão armada")
                .latitude(-3.8029)
                .longitude(-38.5651)
                .location("Rua Dr. Paulo de Mello Machado")
                .risk(RiskLevelEnum.fromDescription("Alto"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ3 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Roubo de Veículo"))
                .description("Roubo de carro estacionado")
                .latitude(-3.8027)
                .longitude(-38.5649)
                .location("Rua Dr. Paulo de Mello Machado")
                .risk(RiskLevelEnum.fromDescription("Alto"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ4 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Assalto"))
                .description("Assalto perto da esquina")
                .latitude(-3.8028)
                .longitude(-38.5652)
                .location("Rua Dr. Paulo de Mello Machado")
                .risk(RiskLevelEnum.fromDescription("Alto"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ5 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Assalto"))
                .description("Assalto na calçada")
                .latitude(-3.8029)
                .longitude(-38.5648)
                .location("Rua Dr. Paulo de Mello Machado")
                .risk(RiskLevelEnum.fromDescription("Alto"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();

        // Hotspot 2 (Médio Risco) - ~330m ao Sul
        Occurrence occ6 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Furto"))
                .description("Furto de carteira")
                .latitude(-3.8058)
                .longitude(-38.5650)
                .location("Rua São Lázaro")
                .risk(RiskLevelEnum.fromDescription("Médio"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ7 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Furto"))
                .description("Furto de bicicleta")
                .latitude(-3.8059)
                .longitude(-38.5651)
                .location("Rua São Lázaro")
                .risk(RiskLevelEnum.fromDescription("Médio"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ8 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Vandalismo"))
                .description("Lixeira depredada")
                .latitude(-3.8057)
                .longitude(-38.5649)
                .location("Rua São Lázaro")
                .risk(RiskLevelEnum.fromDescription("Médio"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ9 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Furto"))
                .description("Furto de celular")
                .latitude(-3.8058)
                .longitude(-38.5652)
                .location("Rua São Lázaro")
                .risk(RiskLevelEnum.fromDescription("Médio"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ10 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Vandalismo"))
                .description("Pichação no muro")
                .latitude(-3.8059)
                .longitude(-38.5648)
                .location("Rua São Lázaro")
                .risk(RiskLevelEnum.fromDescription("Médio"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();

        // Hotspot 3 (Baixo Risco) - ~380m ao Oeste
        Occurrence occ11 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Iluminação Precária"))
                .description("Poste apagado")
                .latitude(-3.8028)
                .longitude(-38.5685)
                .location("Rua Elias de Souza")
                .risk(RiskLevelEnum.fromDescription("Baixo"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ12 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Iluminação Precária"))
                .description("Rua muito escura")
                .latitude(-3.8029)
                .longitude(-38.5686)
                .location("Rua Elias de Souza")
                .risk(RiskLevelEnum.fromDescription("Baixo"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ13 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Pessoa Suspeita"))
                .description("Rondando residências")
                .latitude(-3.8027)
                .longitude(-38.5684)
                .location("Rua Elias de Souza")
                .risk(RiskLevelEnum.fromDescription("Baixo"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ14 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Iluminação Precária"))
                .description("Luz piscando")
                .latitude(-3.8028)
                .longitude(-38.5687)
                .location("Rua Elias de Souza")
                .risk(RiskLevelEnum.fromDescription("Baixo"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();
        Occurrence occ15 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescription("Pessoa Suspeita"))
                .description("Olhando portões")
                .latitude(-3.8029)
                .longitude(-38.5683)
                .location("Rua Elias de Souza")
                .risk(RiskLevelEnum.fromDescription("Baixo"))
                .user(maria)
                .anonymous(false)
                .isActive(true)
                .build();

        occurrenceRepository.saveAll(Arrays.asList(
                occ1, occ2, occ3, occ4, occ5, occ6, occ7, occ8, occ9, occ10,
                occ11, occ12, occ13, occ14, occ15
        ));
        }
    }