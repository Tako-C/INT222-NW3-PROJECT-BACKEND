package sit.int221.mytasksservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.mytasksservice.dtos.response.request.StatusAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.StatusUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.ItemNotFoundException;
import sit.int221.mytasksservice.dtos.response.response.StatusDetailResponseDTO;
import sit.int221.mytasksservice.dtos.response.response.StatusTableResponseDTO;
import sit.int221.mytasksservice.models.primary.*;
import sit.int221.mytasksservice.repositories.primary.BoardsRepository;
import sit.int221.mytasksservice.repositories.primary.StatusesRepository;
import sit.int221.mytasksservice.repositories.primary.TasksRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatusesService {
    @Autowired
    private StatusesRepository statusesRepository;

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private BoardsRepository boardsRepository;

    @Autowired
    private ModelMapper modelMapper;


    public List<StatusTableResponseDTO> getAllStatusesByBoard_id(String boardsId) {
        Boards boards =  boardsRepository.findById(boardsId).orElseThrow(ItemNotFoundException::new);
        return boards.getStatuses().stream().map(status ->
            modelMapper.map(status, StatusTableResponseDTO.class)
        ).collect(Collectors.toList());
    }
    public StatusDetailResponseDTO getStatusesByBoard_idAndByStatusID(String boardsId, Integer statusId) {
        Statuses statuses =  statusesRepository.findByStatusIdAndBoardsBoardId(statusId,boardsId).orElseThrow(ItemNotFoundException::new);

        StatusDetailResponseDTO statusDetailResponseDTO = modelMapper.map(statuses, StatusDetailResponseDTO.class);
        return statusDetailResponseDTO;
    }

    public Statuses createNewStatus(StatusAddRequestDTO statusAddRequestDTO) {
        checkStatusNameExists(statusAddRequestDTO.getName());

        Statuses status = modelMapper.map(statusAddRequestDTO, Statuses.class);
        trimAndValidateStatusFields(status, statusAddRequestDTO.getName(), statusAddRequestDTO.getDescription());

        Boards boards = boardsRepository.findById(statusAddRequestDTO.getBoards()).orElseThrow(ItemNotFoundException::new);
        status.setBoards(boards);
        return statusesRepository.save(status);
    }

    public Statuses updateStatus(StatusUpdateRequestDTO statusUpdateRequestDTO ,Integer statusId) {
        checkStatusNameExists(statusUpdateRequestDTO.getName());
        Statuses status = modelMapper.map(statusUpdateRequestDTO, Statuses.class);
        trimAndValidateStatusFields(status, statusUpdateRequestDTO.getName(), statusUpdateRequestDTO.getDescription());

        Statuses statuses = statusesRepository.findById(statusId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status not found"));

        if ("No Status".equals(statuses.getName()) || "Done".equals(statuses.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify this status");
        }
        Boards boards = boardsRepository.findById(statusUpdateRequestDTO.getBoards()).orElseThrow(ItemNotFoundException::new);
        status.setName(statusUpdateRequestDTO.getName());
        status.setDescription(statusUpdateRequestDTO.getDescription());
        status.setBoards(boards);

        return statusesRepository.save(status);
    }


    public Statuses deleteStatus(Integer statusId, String boardId) {
        Statuses status = statusesRepository.findByStatusIdAndBoardsBoardId(statusId , boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status not found"));
        if ("No Status".equals(status.getName()) || "Done".equals(status.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete this status");
        }
        if (!tasksRepository.findByStatus_StatusIdAndBoards_BoardId(statusId ,boardId ).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete status with associated tasks");
        }
        statusesRepository.delete(status);
        return status;
    }

    public Statuses reassignAndDeleteStatus(Integer statusId, Integer newStatusId, String boardId) {
        if (statusId.equals(newStatusId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source and destination statuses cannot be the same");
        }

        Statuses oldStatus = statusesRepository.findByStatusIdAndBoardsBoardId(statusId,boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source status not found"));

        Statuses newStatus = statusesRepository.findByStatusIdAndBoardsBoardId(newStatusId,boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination status not found"));

        List<Tasks> tasksWithThisStatus = tasksRepository.findByStatus_StatusIdAndBoards_BoardId(statusId,boardId);

        if (tasksWithThisStatus.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destination status not found");
        }
        tasksWithThisStatus.forEach(task -> {
            task.setStatus(newStatus);
            tasksRepository.save(task);
        });

        statusesRepository.delete(oldStatus);
        return oldStatus;
    }

    private void trimAndValidateStatusFields(Statuses status, String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status name cannot be null or empty!");
        }
        if (description != null && description.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status description cannot be empty!");
        }
        if (description != null){
            status.setDescription(description.trim());

        }else {
            status.setName(name.trim());
        }
    }

    private void checkStatusNameExists(String name) {
        if (statusesRepository.findByName(name) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status name already exists!");
        }
    }

}
