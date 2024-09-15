package sit.int221.mytasksservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.mytasksservice.dtos.response.request.StatusUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.TaskAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.TaskUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.*;
import sit.int221.mytasksservice.models.primary.Boards;
import sit.int221.mytasksservice.models.primary.Statuses;
import sit.int221.mytasksservice.models.primary.Tasks;
import sit.int221.mytasksservice.repositories.primary.*;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class TasksService {
    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private StatusesRepository statusRepository;

    @Autowired
    private BoardsRepository boardsRepository;

    @Autowired
    private ModelMapper modelMapper;

//    public List<TaskTableResponseDTO> getAllTasksByBoardId(String boardsId) {
//        List<Tasks> tasks = tasksRepository.findByBoardsBoardId(boardsId);
//
//        return tasks.stream().map(task -> {
//            TaskTableResponseDTO taskDTO = modelMapper.map(task, TaskTableResponseDTO.class);
//            taskDTO.setStatusName(task.getStatus().getName());
//            taskDTO.setBoardName(task.getBoards().getBoard_name());
//            return taskDTO;
//        }).collect(Collectors.toList());
//    }

    //============================== Get TaskAll(Filter&Sort) ==========================================================
    public List<TaskTableResponseDTO> getAllTasksByBoardId(String boardId, String sortBy, List<String> filterStatuses) {
        // Determine the sort order
        Sort sort = Sort.by(Sort.Direction.ASC, sortBy != null ? sortBy : "createdOn");
        List<Tasks> tasks = tasksRepository.findByBoardsBoardId(boardId, sort);

        // If filterStatuses is empty, return all tasks
        if (filterStatuses == null || filterStatuses.isEmpty()) {
            return tasks.stream().map(task -> {
                TaskTableResponseDTO taskDTO = modelMapper.map(task, TaskTableResponseDTO.class);
                taskDTO.setStatusName(task.getStatus().getName());
                taskDTO.setBoardName(task.getBoards().getBoard_name());
                return taskDTO;
            }).collect(Collectors.toList());
        }

        // Filter tasks by statuses if filterStatuses is provided
        List<Tasks> filteredTasks = tasks.stream()
                .filter(task -> filterStatuses.contains(task.getStatus().getName()))
                .collect(Collectors.toList());

        return filteredTasks.stream().map(task -> {
            TaskTableResponseDTO taskDTO = modelMapper.map(task, TaskTableResponseDTO.class);
            taskDTO.setStatusName(task.getStatus().getName());
            taskDTO.setBoardName(task.getBoards().getBoard_name());
            return taskDTO;
        }).collect(Collectors.toList());
    }


    //============================== Get Task ==========================================================================
    public TaskDetailResponseDTO getTaskByBoardIdAndByTaskID(String boardId, Integer tasksId) {
        if (boardsRepository.findById(boardId).isEmpty()) {
            throw new ItemNotFoundException("Board not found");
        }
        Tasks task = tasksRepository.findByIdAndBoardsBoardId(tasksId, boardId)
                .orElseThrow(() -> new ItemNotFoundException("Task not found in the specified Board"));

        TaskDetailResponseDTO taskDTO = modelMapper.map(task, TaskDetailResponseDTO.class);
        taskDTO.setStatusName(task.getStatus().getName());
        taskDTO.setBoardName(task.getBoards().getBoard_name());

        return taskDTO;
    }


    //=============================== create Task ======================================================================
    public Tasks createNewTask(TaskAddRequestDTO taskAddRequestDTO, String boardsId) {
        if (!boardsRepository.existsById(boardsId)) {
            throw new ItemNotFoundException("Not existing board");
        }

        Tasks task = modelMapper.map(taskAddRequestDTO, Tasks.class);
        processTaskFields(task, taskAddRequestDTO.getDescription(), taskAddRequestDTO.getAssignees());
        validateStatus(taskAddRequestDTO.getStatus());

        Integer convertStatusId = Integer.valueOf(taskAddRequestDTO.getStatus());
        task.setStatus(statusRepository.findByStatusIdAndBoardsBoardId(convertStatusId, boardsId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status Not Found")));

        Boards boards = boardsRepository.findById(boardsId).orElseThrow(ItemNotFoundException::new);
        task.setBoards(boards);
        return tasksRepository.save(task);
    }

    //======================================= Update Task =============================================================
    public Tasks updateTask(TaskUpdateRequestDTO taskUpdateRequestDTO, Integer taskId) {
        Tasks task = tasksRepository.findById(taskId)
                .orElseThrow(() -> new ItemNotFoundException("Not existing task"));

        Boards boards = boardsRepository.findById(taskUpdateRequestDTO.getBoards())
                .orElseThrow(() -> new ItemNotFoundException("Not existing board"));

        // ตรวจสอบว่า Task อยู่ใน Board ที่ระบุหรือไม่
        if (!task.getBoards().getBoardId().equals(boards.getBoardId())) {
            throw new ItemNotFoundException("Task does not belong to the specified Board");
        }

        task.setTitle(taskUpdateRequestDTO.getTitle());
        task.setDescription(taskUpdateRequestDTO.getDescription());
        task.setAssignees(taskUpdateRequestDTO.getAssignees());
        task.setBoards(boards);

        Statuses statuses = statusRepository.findById(Integer.valueOf(taskUpdateRequestDTO.getStatus()))
                .orElseThrow(() -> new ItemNotFoundException("Not existing status"));

        task.setStatus(statuses);

        return tasksRepository.save(task);
    }




    //======================================= Delete Task =============================================================

    public Tasks deleteTask(String boardId, Integer tasksId) {
        if (!boardsRepository.existsById(boardId)) {
            throw new ItemNotFoundException("Not existing board");
        }

        Tasks task = tasksRepository.findByIdAndBoardsBoardId(tasksId, boardId)
                .orElseThrow(() -> new ItemNotFoundException("Not existing task"));

        tasksRepository.delete(task);
        return task;
    }




    //======================================= Function Use =============================================================
    private void trimTaskFields(Tasks task) {
        task.setAssignees(task.getAssignees() != null ? task.getAssignees().trim() : null);
        task.setTitle(task.getTitle() != null ? task.getTitle().trim() : null);
        task.setDescription(task.getDescription() != null ? task.getDescription().trim() : null);
    }
    private void validateStatus(String status) {
        if (status == null || status.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required!");
        }
        try {
            Integer convertStatusId = Integer.valueOf(status);
            if (!statusRepository.existsById(convertStatusId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status does not exist");
            }
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status ID format");
        }
    }
    private void processTaskFields(Tasks task, String description, String assignees) {
        trimTaskFields(task);

        if (description != null && description.isEmpty()) {
            task.setDescription(null);
        } else {
            task.setDescription(description);
        }

        if (assignees != null && assignees.isEmpty()) {
            task.setAssignees(null);
        } else {
            task.setAssignees(assignees);
        }
    }
}
