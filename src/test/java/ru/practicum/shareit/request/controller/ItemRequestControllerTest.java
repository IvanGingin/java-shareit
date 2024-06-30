package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.Constants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto(1L, "Описание запроса", LocalDateTime.now(), 1L, null);
    }

    @Test
    void addRequest() throws Exception {
        when(itemRequestService.addRequest(eq(1L), any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(Constants.CONST_SHARED_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void getUserRequests() throws Exception {
        List<ItemRequestDto> requests = Arrays.asList(itemRequestDto);
        when(itemRequestService.getUserRequests(eq(1L))).thenReturn(requests);
        mockMvc.perform(get("/requests")
                        .header(Constants.CONST_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].created").exists());
    }

    @Test
    void getRequest() throws Exception {
        when(itemRequestService.getRequest(eq(1L), eq(1L))).thenReturn(itemRequestDto);
        mockMvc.perform(get("/requests/1")
                        .header(Constants.CONST_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void getAllRequests() throws Exception {
        List<ItemRequestDto> requests = Arrays.asList(itemRequestDto);
        when(itemRequestService.getAllRequests(eq(1L), eq(0), eq(20))).thenReturn(requests);
        mockMvc.perform(get("/requests/all")
                        .header(Constants.CONST_SHARED_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].created").exists());
    }
}
