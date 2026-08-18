package com.portfolio.taskmanager.service;

import com.portfolio.taskmanager.dto.TaskRequest;
import com.portfolio.taskmanager.exception.TaskNotFoundException;
import com.portfolio.taskmanager.model.Task;
import com.portfolio.taskmanager.model.TaskPriority;
import com.portfolio.taskmanager.model.TaskStatus;
import com.portfolio.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task("Write unit tests", "Cover the service layer", TaskPriority.HIGH);
        sampleTask.setId(1L);
        sampleTask.setStatus(TaskStatus.TODO);
    }

    @Test
    void getAllTasks_returnsAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        List<Task> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Write unit tests");
    }

    @Test
    void getTaskById_whenExists_returnsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        Task result = taskService.getTaskById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getTaskById_whenMissing_throwsException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createTask_savesAndReturnsTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Deploy to cloud");
        request.setDescription("Push container to Azure");
        request.setPriority(TaskPriority.MEDIUM);

        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task result = taskService.createTask(request);

        assertThat(result).isNotNull();
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_whenExists_removesTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(sampleTask);
    }

    @Test
    void deleteTask_whenMissing_throwsException() {
        when(taskRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(42L))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).delete(any(Task.class));
    }
}
