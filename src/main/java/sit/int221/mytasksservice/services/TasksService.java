package sit.int221.mytasksservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.mytasksservice.dtos.response.request.TaskAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.TaskUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.*;
import sit.int221.mytasksservice.models.primary.Boards;
import sit.int221.mytasksservice.models.primary.Statuses;
import sit.int221.mytasksservice.models.primary.Tasks;
import sit.int221.mytasksservice.models.secondary.Users;
import sit.int221.mytasksservice.repositories.primary.*;
import sit.int221.mytasksservice.repositories.secondary.UsersRepository;

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
    private UsersRepository usersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CollabBoardRepository collabBoardRepository;

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
        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = null;

        if (!username.equals("anonymousUser")) {
            currentUser = usersRepository.findByUsername(username).orElseThrow(() -> new ItemNotFoundException("User not found"));
        }

        boolean isOwner = currentUser != null && board.getOid().equals(currentUser.getOid());
        boolean isCollaborator = currentUser != null && collabBoardRepository.existsByOidAndBoardsId(currentUser.getOid(), boardId);
        boolean isPublicBoard = board.getVisibility().equals("public");

        if (isOwner || isCollaborator || isPublicBoard) {
            Sort sort = Sort.by(Sort.Direction.ASC, sortBy != null ? sortBy : "createdOn");
            List<Tasks> tasks = tasksRepository.findByBoardsBoardId(boardId, sort);

            if (filterStatuses == null || filterStatuses.isEmpty()) {
                return tasks.stream().map(task -> {
                    TaskTableResponseDTO taskDTO = modelMapper.map(task, TaskTableResponseDTO.class);
                    taskDTO.setStatusName(task.getStatus().getName());
                    taskDTO.setBoardName(task.getBoards().getBoard_name());
                    return taskDTO;
                }).collect(Collectors.toList());
            }

            List<Tasks> filteredTasks = tasks.stream()
                    .filter(task -> filterStatuses.contains(task.getStatus().getName()))
                    .toList();

            return filteredTasks.stream().map(task -> {
                TaskTableResponseDTO taskDTO = modelMapper.map(task, TaskTableResponseDTO.class);
                taskDTO.setStatusName(task.getStatus().getName());
                taskDTO.setBoardName(task.getBoards().getBoard_name());
                return taskDTO;
            }).collect(Collectors.toList());
        } else {
            throw new ForbiddenException("Access Denied");
        }
    }



    //============================== Get Task ==========================================================================
    public TaskDetailResponseDTO getTaskByBoardIdAndByTaskID(String boardId, Integer tasksId) {
        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = null;

        if (!username.equals("anonymousUser")) {
            currentUser = usersRepository.findByUsername(username).orElseThrow(() -> new ItemNotFoundException("User not found"));
        }

        boolean isOwner = currentUser != null && board.getOid().equals(currentUser.getOid());
        boolean isCollaborator = currentUser != null && collabBoardRepository.existsByOidAndBoardsId(currentUser.getOid(), boardId);
        boolean isPublicBoard = board.getVisibility().equals("public");

        if (isOwner || isCollaborator || isPublicBoard) {
            Tasks task = tasksRepository.findByIdAndBoardsBoardId(tasksId, boardId)
                    .orElseThrow(() -> new ItemNotFoundException("Task not found in the specified Board"));

            TaskDetailResponseDTO taskDTO = modelMapper.map(task, TaskDetailResponseDTO.class);
            taskDTO.setStatusName(task.getStatus().getName());
            taskDTO.setBoardName(task.getBoards().getBoard_name());

            return taskDTO;
        } else {
            throw new ForbiddenException("Access Denied");
        }
    }



    //=============================== create Task ======================================================================
    public Tasks createNewTask(TaskAddRequestDTO taskAddRequestDTO, String boardsId) {
        Users currentUser = checkBoardAccess(boardsId);

        Tasks task = modelMapper.map(taskAddRequestDTO, Tasks.class);
        processTaskFields(task, taskAddRequestDTO.getDescription(), taskAddRequestDTO.getAssignees());
        validateStatus(taskAddRequestDTO.getStatus());

        Integer convertStatusId = Integer.valueOf(taskAddRequestDTO.getStatus());
        task.setStatus(statusRepository.findByStatusIdAndBoardsBoardId(convertStatusId, boardsId)
                .orElseThrow(() -> new BadRequestException("Status Not Found")));

        Boards boards = boardsRepository.findById(boardsId).orElseThrow(ItemNotFoundException::new);
        task.setBoards(boards);
        return tasksRepository.save(task);
    }

    //======================================= Update Task =============================================================
    public Tasks updateTask(TaskUpdateRequestDTO taskUpdateRequestDTO, Integer taskId, String boardId) {
        Users currentUser = checkBoardAccess(boardId);

        Tasks task = tasksRepository.findByIdAndBoardsBoardId(taskId, boardId)
                .orElseThrow(() -> new ItemNotFoundException("Not existing task"));

        Boards boards = boardsRepository.findById(taskUpdateRequestDTO.getBoards())
                .orElseThrow(() -> new ItemNotFoundException("Not existing board"));

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
        Users currentUser = checkBoardAccess(boardId);

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

    public Users checkBoardAccess(String boardId) {
        Boards board = boardsRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board not found"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser =  null;

        if (!username.equals("anonymousUser")) {
            currentUser = usersRepository.findByUsername(username).orElseThrow(() -> new ItemNotFoundException("User not found"));

            boolean isOwner = board.getOid().equals(currentUser.getOid());
            boolean isCollaboratorWithWriteAccess = collabBoardRepository.existsByOidAndBoardsIdAndAccessRight(currentUser.getOid(), boardId, "write");

            if (!isOwner && !isCollaboratorWithWriteAccess) {
                throw new ForbiddenException("Access Denied");
            }
        } else {
            if (board.getVisibility().equals("private")) {
                throw new ForbiddenException("Access Denied");
            }
        }
        return currentUser;
    }
}
