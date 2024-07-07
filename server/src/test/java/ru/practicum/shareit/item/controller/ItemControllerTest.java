package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Constants;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemUpdateDto itemUpdateDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "Вещь", "Описание", true, 1L, null, null, null, List.of());
        itemUpdateDto = new ItemUpdateDto(1L, "Обновленная вещь", "Обновленное описание", true);
        commentDto = new CommentDto(1L, "Комментарий", 1L, 1L, "Автор", null);
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(eq(1L), any(ItemDto.class))).thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header(Constants.CONST_SHARED_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class))).thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .header(Constants.CONST_SHARED_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItem(eq(1L), eq(1L))).thenReturn(itemDto);
        mockMvc.perform(get("/items/1")
                        .header(Constants.CONST_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void getItems() throws Exception {
        List<ItemDto> items = Arrays.asList(itemDto);
        when(itemService.getItems(eq(1L), eq(0), eq(10))).thenReturn(items);
        mockMvc.perform(get("/items")
                        .header(Constants.CONST_SHARED_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void deleteItem() throws Exception {
        doNothing().when(itemService).deleteItem(eq(1L));
        mockMvc.perform(delete("/items/1")
                        .header(Constants.CONST_SHARED_USER_ID, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems() throws Exception {
        List<ItemDto> items = Arrays.asList(itemDto);
        when(itemService.searchItems(eq("text"), eq(0), eq(10))).thenReturn(items);
        mockMvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(eq(1L), eq(1L), any(CommentDto.class))).thenReturn(commentDto);
        mockMvc.perform(post("/items/1/comment")
                        .header(Constants.CONST_SHARED_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }
}
