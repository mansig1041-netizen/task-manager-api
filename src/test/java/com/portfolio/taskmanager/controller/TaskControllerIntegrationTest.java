package com.portfolio.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.taskmanager.dto.TaskRequest;
import com.portfolio.taskmanager.model.TaskPriority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_thenFetchIt_endToEnd() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Integration test task");
        request.setDescription("Verify full create-then-read flow");
        request.setPriority(TaskPriority.HIGH);

        String response = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Integration test task")))
                .andExpect(jsonPath("$.status", is("TODO")))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/v1/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Integration test task")));
    }

    @Test
    void createTask_withBlankTitle_returnsBadRequest() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("");

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    void getTaskById_whenMissing_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteTask_thenGet_returns404() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Task to delete");

        String response = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/v1/tasks/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/tasks/" + id))
                .andExpect(status().isNotFound());
    }
}
