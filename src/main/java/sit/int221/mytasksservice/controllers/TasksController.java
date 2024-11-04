package sit.int221.mytasksservice.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.mytasksservice.dtos.response.request.*;
import sit.int221.mytasksservice.dtos.response.response.StatusDetailResponseDTO;
import sit.int221.mytasksservice.dtos.response.response.StatusTableResponseDTO;
import sit.int221.mytasksservice.dtos.response.response.TaskDetailResponseDTO;
import sit.int221.mytasksservice.dtos.response.response.TaskTableResponseDTO;
import sit.int221.mytasksservice.models.primary.Statuses;
import sit.int221.mytasksservice.models.primary.Tasks;
import sit.int221.mytasksservice.services.StatusesService;
import sit.int221.mytasksservice.services.TasksService;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23nw3.sit.kmutt.ac.th:3333","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("/v3/boards")

public class TasksController {
    @Autowired
    private TasksService tasksService;

    @Autowired
    private ModelMapper modelMapper;

//    @GetMapping("/{boardId}/tasks")
//    public List<TaskTableResponseDTO> getAllTasks(@PathVariable String boardId){
//        List<TaskTableResponseDTO> taskTableResponseDTOS = tasksService.getAllTasksByBoardId(boardId);
//        return taskTableResponseDTOS;
//    }

    //============================ Get TaskAll(Filter&Sort) ============================================================
    @GetMapping("/{boardId}/tasks")
    public List<TaskTableResponseDTO> getAllTasks(
            @PathVariable String boardId,
            @RequestParam(value = "sortBy", defaultValue = "createdOn") String sortBy,
            @RequestParam(value = "FilterStatuses", required = false) List<String> filterStatuses
    ) {
        return tasksService.getAllTasksByBoardId(boardId, sortBy, filterStatuses);
    }

    //================================ Get Task ========================================================================
    @GetMapping("/{boardId}/tasks/{tasksId}")
    public TaskDetailResponseDTO getTask(@PathVariable String boardId, @PathVariable Integer tasksId){
        return tasksService.getTaskByBoardIdAndByTaskID(boardId, tasksId);
    }

    //=================================== Create Task ==================================================================
    @PostMapping("/{boardId}/tasks")
    public ResponseEntity<TaskAddRequestDTO> addTask(@Valid @RequestBody TaskAddRequestDTO taskAddRequestDTO, @PathVariable String boardId){
        taskAddRequestDTO.setBoards(boardId);
        Tasks createTask = tasksService.createNewTask(taskAddRequestDTO,boardId);
        TaskAddRequestDTO addRequestDTO = modelMapper.map(createTask, TaskAddRequestDTO.class);
        URI location = URI.create("/"+boardId+"/tasks/");
        return ResponseEntity.created(location).body(addRequestDTO);
    }

    //=================================== Update Task ==================================================================

    @PutMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<TaskUpdateRequestDTO> updateTask (@Valid @RequestBody TaskAddRequestDTO taskAddRequestDTO, @PathVariable String boardId, @PathVariable Integer taskId) {
        taskAddRequestDTO.setBoards(boardId);
        TaskDetailResponseDTO updatedTask = tasksService.getTaskByBoardIdAndByTaskID(boardId,taskId);
        TaskUpdateRequestDTO taskUpdateRequestDTO = modelMapper.map(updatedTask, TaskUpdateRequestDTO.class);
        taskUpdateRequestDTO.setTitle(taskAddRequestDTO.getTitle());
        taskUpdateRequestDTO.setDescription(taskAddRequestDTO.getDescription());
        taskUpdateRequestDTO.setAssignees(taskAddRequestDTO.getAssignees());
        taskUpdateRequestDTO.setBoards(taskAddRequestDTO.getBoards());
        taskUpdateRequestDTO.setStatus(taskAddRequestDTO.getStatus());
        tasksService.updateTask(taskUpdateRequestDTO,taskId, boardId);
        return ResponseEntity.ok().body(taskUpdateRequestDTO);
    }

    //=================================== Delete Task ==================================================================
    @DeleteMapping("/{boardId}/tasks/{tasksId}")
    public ResponseEntity<TaskDeleteRequestDTO>
    deleteTask(@PathVariable Integer tasksId, @PathVariable String boardId) {
        Tasks deletedTask = tasksService.deleteTask(boardId, tasksId);
        TaskDeleteRequestDTO deletedTaskDTO = modelMapper.map(deletedTask, TaskDeleteRequestDTO.class);
        deletedTaskDTO.setStatusName(deletedTask.getStatus().getName());

        return ResponseEntity.ok().body(deletedTaskDTO);
    }


}
