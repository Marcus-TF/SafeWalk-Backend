package com.safewalk.service.impl;

import com.safewalk.dto.HotspotDTO;
import com.safewalk.dto.OccurrenceRequest;
import com.safewalk.dto.OccurrenceResponse;
import com.safewalk.exception.ResourceNotFoundException;
import com.safewalk.exception.UnauthorizedException;
import com.safewalk.model.Occurrence;
import com.safewalk.model.User;
import com.safewalk.model.enums.OccurrenceEnum;
import com.safewalk.model.enums.RiskLevelEnum;
import com.safewalk.repository.OccurrenceRepository;
import com.safewalk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OccurrenceServiceImplTest {

    @Mock
    private OccurrenceRepository occurrenceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OccurrenceServiceImpl occurrenceService;

    private User user;
    private User otherUser;
    private Occurrence occurrence;
    private OccurrenceRequest occurrenceRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Marcus User")
                .email("marcus@user.com")
                .build();

        otherUser = User.builder()
                .id(2L)
                .name("Other User")
                .email("other@user.com")
                .build();

        occurrence = Occurrence.builder()
                .id(100L)
                .type(OccurrenceEnum.ROBBERY)
                .description("Assalto a mão armada")
                .latitude(-23.5505)
                .longitude(-46.6333)
                .location("Praça da Sé")
                .risk(RiskLevelEnum.HIGH)
                .user(user)
                .anonymous(false)
                .createdAt(LocalDateTime.now())
                .isActive(false)
                .build();

        occurrenceRequest = new OccurrenceRequest();
        occurrenceRequest.setType("Assalto");
        occurrenceRequest.setDescription("Assalto a mão armada");
        occurrenceRequest.setLatitude(-23.5505);
        occurrenceRequest.setLongitude(-46.6333);
        occurrenceRequest.setLocation("Praça da Sé");
        occurrenceRequest.setRisk("Alto");
        occurrenceRequest.setAnonymous(false);
    }

    @Test
    void create_WithValidRequestAndUser_ShouldReturnOccurrenceResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(occurrenceRepository.save(any(Occurrence.class))).thenReturn(occurrence);

        OccurrenceResponse response = occurrenceService.create(occurrenceRequest, 1L);

        assertNotNull(response);
        assertEquals(occurrence.getId(), response.getId());
        assertEquals("Assalto", response.getType());
        assertEquals("Alto", response.getRisk());
        assertEquals("Marcus User", response.getUserName());
        verify(occurrenceRepository, times(1)).save(any(Occurrence.class));
    }

    @Test
    void create_WithInvalidUser_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> occurrenceService.create(occurrenceRequest, 99L));
        verify(occurrenceRepository, never()).save(any(Occurrence.class));
    }

    @Test
    void findAll_ShouldReturnOrderedOccurrences() {
        when(occurrenceRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(occurrence));

        List<OccurrenceResponse> result = occurrenceService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(occurrence.getId(), result.get(0).getId());
    }

    @Test
    void findByUserId_ShouldReturnUserOccurrences() {
        when(occurrenceRepository.findByUserId(1L)).thenReturn(Arrays.asList(occurrence));

        List<OccurrenceResponse> result = occurrenceService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(occurrence.getId(), result.get(0).getId());
    }

    @Test
    void findById_WithValidId_ShouldReturnOccurrence() {
        when(occurrenceRepository.findById(100L)).thenReturn(Optional.of(occurrence));

        OccurrenceResponse response = occurrenceService.findById(100L);

        assertNotNull(response);
        assertEquals(occurrence.getId(), response.getId());
    }

    @Test
    void findById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        when(occurrenceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> occurrenceService.findById(999L));
    }

    @Test
    void delete_WithAuthorizedUser_ShouldDeleteOccurrence() {
        when(occurrenceRepository.findById(100L)).thenReturn(Optional.of(occurrence));

        occurrenceService.delete(100L, 1L);

        verify(occurrenceRepository, times(1)).delete(occurrence);
    }

    @Test
    void delete_WithUnauthorizedUser_ShouldThrowUnauthorizedException() {
        when(occurrenceRepository.findById(100L)).thenReturn(Optional.of(occurrence));

        assertThrows(UnauthorizedException.class, () -> occurrenceService.delete(100L, 2L));
        verify(occurrenceRepository, never()).delete(any(Occurrence.class));
    }

    @Test
    void update_WithValidRequestAndAuthorizedUser_ShouldUpdateOccurrence() {
        when(occurrenceRepository.findById(100L)).thenReturn(Optional.of(occurrence));
        when(occurrenceRepository.save(any(Occurrence.class))).thenReturn(occurrence);

        occurrenceRequest.setDescription("New description");
        OccurrenceResponse response = occurrenceService.update(100L, occurrenceRequest, 1L);

        assertNotNull(response);
        verify(occurrenceRepository, times(1)).save(occurrence);
    }

    @Test
    void update_WithUnauthorizedUser_ShouldThrowUnauthorizedException() {
        when(occurrenceRepository.findById(100L)).thenReturn(Optional.of(occurrence));

        assertThrows(UnauthorizedException.class, () -> occurrenceService.update(100L, occurrenceRequest, 2L));
        verify(occurrenceRepository, never()).save(any(Occurrence.class));
    }

    @Test
    void validateOccurrence_WithValidId_ShouldSetIsActiveToTrue() {
        when(occurrenceRepository.findById(100L)).thenReturn(Optional.of(occurrence));
        when(occurrenceRepository.save(any(Occurrence.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OccurrenceResponse response = occurrenceService.validateOccurrence(100L);

        assertNotNull(response);
        assertTrue(response.isActive());
        verify(occurrenceRepository, times(1)).save(occurrence);
    }

    @Test
    void getHotspots_WithFiveCloseOccurrences_ShouldCreateHotspot() {
        Occurrence occ1 = createMockOccurrence(101L, -23.5505, -46.6333, "Alto");
        Occurrence occ2 = createMockOccurrence(102L, -23.5506, -46.6334, "Alto");
        Occurrence occ3 = createMockOccurrence(103L, -23.5504, -46.6332, "Médio");
        Occurrence occ4 = createMockOccurrence(104L, -23.5505, -46.6335, "Baixo");
        Occurrence occ5 = createMockOccurrence(105L, -23.5506, -46.6331, "Baixo");

        when(occurrenceRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(occ1, occ2, occ3, occ4, occ5));

        List<HotspotDTO> hotspots = occurrenceService.getHotspots();

        assertNotNull(hotspots);
        assertEquals(1, hotspots.size());
        assertEquals("Alto", hotspots.get(0).getRisk());
        assertEquals(5, hotspots.get(0).getOccurrencesList().size());
    }

    @Test
    void getHotspots_WithFewerThanFiveCloseOccurrences_ShouldNotCreateHotspot() {
        Occurrence occ1 = createMockOccurrence(101L, -23.5505, -46.6333, "Alto");
        Occurrence occ2 = createMockOccurrence(102L, -23.5506, -46.6334, "Alto");

        when(occurrenceRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(occ1, occ2));

        List<HotspotDTO> hotspots = occurrenceService.getHotspots();

        assertNotNull(hotspots);
        assertTrue(hotspots.isEmpty());
    }

    private Occurrence createMockOccurrence(Long id, double lat, double lon, String riskDesc) {
        return Occurrence.builder()
                .id(id)
                .type(OccurrenceEnum.ROBBERY)
                .description("Desc")
                .latitude(lat)
                .longitude(lon)
                .location("Loc")
                .risk(RiskLevelEnum.fromDescription(riskDesc))
                .user(user)
                .anonymous(false)
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .build();
    }
}
