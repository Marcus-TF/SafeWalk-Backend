package com.safewalk.util;

import com.safewalk.model.Occurrence;
import com.safewalk.model.enums.OccurrenceEnum;
import com.safewalk.model.enums.RiskLevelEnum;
import com.safewalk.model.User;
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
        if (userRepository.count() > 0) {
            return;
        }

        User joao = User.builder()
                .name("João Silva")
                .email("joao@test.com")
                .password(passwordEncoder.encode("123456"))
                .build();

        User maria = User.builder()
                .name("Maria Santos")
                .email("maria@test.com")
                .password(passwordEncoder.encode("123456"))
                .build();

        User pedro = User.builder()
                .name("Pedro Oliveira")
                .email("pedro@test.com")
                .password(passwordEncoder.encode("123456"))
                .build();

        List<User> users = userRepository.saveAll(Arrays.asList(joao, maria, pedro));

        Occurrence occ1 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao("Assalto"))
                .description("Assalto à mão armada em frente ao mercado")
                .latitude(-3.7319)
                .longitude(-38.5267)
                .location("Centro - Praça do Ferreira")
                .risk(RiskLevelEnum.fromDescricao("Alto"))
                .user(users.get(0))
                .anonymous(false)
                .build();

        Occurrence occ2 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao("Furto"))
                .description("Furto de celular no transporte público")
                .latitude(-3.7436)
                .longitude(-38.5370)
                .location("Benfica - Terminal do Benfica")
                .risk(RiskLevelEnum.fromDescricao("Médio"))
                .user(users.get(0))
                .anonymous(true)
                .build();

        Occurrence occ3 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao("Iluminação Precária"))
                .description("Rua muito escura à noite")
                .latitude(-3.7250)
                .longitude(-38.5150)
                .location("Meireles - Rua dos Tabajaras")
                .risk(RiskLevelEnum.fromDescricao("Baixo"))
                .user(users.get(0))
                .anonymous(false)
                .build();

        Occurrence occ4 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao("Roubo de Veículo"))
                .description("Carro roubado no estacionamento")
                .latitude(-3.7340)
                .longitude(-38.5400)
                .location("Aldeota - Shopping Del Paseo")
                .risk(RiskLevelEnum.fromDescricao("Alto"))
                .user(users.get(1))
                .anonymous(true)
                .build();

        Occurrence occ5 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao("Vandalismo"))
                .description("Pichação e quebra de vidros")
                .latitude(-3.7170)
                .longitude(-38.5430)
                .location("Parquelândia")
                .risk(RiskLevelEnum.fromDescricao("Médio"))
                .user(users.get(1))
                .anonymous(false)
                .build();

        Occurrence occ6 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao("Assalto"))
                .description("Assalto a pedestre na calçada")
                .latitude(-3.7700)
                .longitude(-38.5500)
                .location("Messejana")
                .risk(RiskLevelEnum.fromDescricao("Alto"))
                .user(users.get(1))
                .anonymous(true)
                .build();

        Occurrence occ7 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao("Pessoa Suspeita"))
                .description("Pessoa suspeita rondando carros")
                .latitude(-3.7900)
                .longitude(-38.6000)
                .location("Jangurussu")
                .risk(RiskLevelEnum.fromDescricao("Baixo"))
                .user(users.get(1))
                .anonymous(false)
                .build();

        Occurrence occ8 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao("Furto"))
                .description("Furto em residência durante o dia")
                .latitude(-3.7650)
                .longitude(-38.4900)
                .location("Sapiranga")
                .risk(RiskLevelEnum.fromDescricao("Alto"))
                .user(users.get(2))
                .anonymous(true)
                .build();

        Occurrence occ9 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao("Assalto"))
                .description("Tentativa de assalto frustrada")
                .latitude(-3.7100)
                .longitude(-38.5500)
                .location("Monte Castelo")
                .risk(RiskLevelEnum.fromDescricao("Médio"))
                .user(users.get(2))
                .anonymous(false)
                .build();

        Occurrence occ10 = Occurrence.builder()
                .type(OccurrenceEnum.fromDescricao("Iluminação Precária"))
                .description("Poste de luz queimado há semanas")
                .latitude(-3.7350)
                .longitude(-38.5200)
                .location("Praia de Iracema")
                .risk(RiskLevelEnum.fromDescricao("Baixo"))
                .user(users.get(2))
                .anonymous(true)
                .build();

        occurrenceRepository.saveAll(Arrays.asList(
                occ1, occ2, occ3, occ4, occ5, occ6, occ7, occ8, occ9, occ10
        ));
    }
}
