package sit.int221.mytasksservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sit.int221.mytasksservice.dtos.response.request.StatusAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.StatusUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.*;
import sit.int221.mytasksservice.models.primary.*;
import sit.int221.mytasksservice.models.secondary.Users;
import sit.int221.mytasksservice.repositories.primary.BoardsRepository;
import sit.int221.mytasksservice.repositories.primary.CollabBoardRepository;
import sit.int221.mytasksservice.repositories.primary.StatusesRepository;
import sit.int221.mytasksservice.repositories.primary.TasksRepository;
import sit.int221.mytasksservice.repositories.secondary.UsersRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CollabBoardRepository collabBoardRepository;


    public List<StatusTableResponseDTO> getAllStatusesByBoard_id(String boardsId) {
        Boards board = boardsRepository.findById(boardsId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = null;

        if (!username.equals("anonymousUser")) {
            currentUser = usersRepository.findByUsername(username).orElseThrow(() -> new ItemNotFoundException("User not found"));
        }

        boolean isOwner = currentUser != null && board.getOid().equals(currentUser.getOid());
        boolean isCollaborator = currentUser != null && collabBoardRepository.existsByOidAndBoardsId(currentUser.getOid(), boardsId);
        boolean isPublicBoard = board.getVisibility().equals("public");

        if (isOwner || isCollaborator || isPublicBoard) {
            return board.getStatuses().stream()
                    .sorted(Comparator.comparing(Statuses::getStatusId))
                    .map(status -> modelMapper.map(status, StatusTableResponseDTO.class))
                    .collect(Collectors.toList());
        } else {
            throw new ForbiddenException("Access Denied");
        }
    }

    public StatusDetailResponseDTO getStatusesByBoard_idAndByStatusID(String boardsId, Integer statusId) {
        Boards board = boardsRepository.findById(boardsId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = null;

        if (!username.equals("anonymousUser")) {
            currentUser = usersRepository.findByUsername(username).orElseThrow(() -> new ItemNotFoundException("User not found"));
        }

        boolean isOwner = currentUser != null && board.getOid().equals(currentUser.getOid());
        boolean isCollaborator = currentUser != null && collabBoardRepository.existsByOidAndBoardsId(currentUser.getOid(), boardsId);
        boolean isPublicBoard = board.getVisibility().equals("public");

        if (isOwner || isCollaborator || isPublicBoard) {
            Statuses statuses = statusesRepository.findByStatusIdAndBoardsBoardId(statusId, boardsId)
                    .orElseThrow(() -> new ItemNotFoundException("Status not found in the specified Board"));

            StatusDetailResponseDTO statusDetailResponseDTO = modelMapper.map(statuses, StatusDetailResponseDTO.class);
            return statusDetailResponseDTO;
        } else {
            throw new ForbiddenException("Access Denied");
        }
    }

    public Statuses createNewStatus(StatusAddRequestDTO statusAddRequestDTO) {
        Users currentUser = checkBoardAccess(statusAddRequestDTO.getBoards());

        Boards boards = boardsRepository.findById(statusAddRequestDTO.getBoards())
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));

        Statuses status = modelMapper.map(statusAddRequestDTO, Statuses.class);
        trimAndValidateStatusFields(status, statusAddRequestDTO.getName(), statusAddRequestDTO.getDescription());
        status.setBoards(boards);

        return statusesRepository.save(status);
    }

    public Statuses updateStatus(StatusUpdateRequestDTO statusUpdateRequestDTO, Integer statusId, String boardId) {
        Users currentUser = checkBoardAccess(boardId);

        Statuses status = statusesRepository.findById(statusId)
                .orElseThrow(() -> new ItemNotFoundException("Status not found"));

        if ("No Status".equals(status.getName())) {
            throw new BadRequestException("Cannot update this status");
        }

        if (!statusId.equals(statusUpdateRequestDTO.getStatusId())) {
            throw new ItemNotFoundException("Status does not belong to the specified Board");
        }

        Boards boards = boardsRepository.findById(statusUpdateRequestDTO.getBoards())
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));

        status.setName(statusUpdateRequestDTO.getName());
        status.setDescription(statusUpdateRequestDTO.getDescription());
        status.setBoards(boards);

        return statusesRepository.save(status);
    }

    public Statuses deleteStatus(Integer statusId, String boardId) {
        Users currentUser = checkBoardAccess(boardId);

        Statuses status = statusesRepository.findByStatusIdAndBoardsBoardId(statusId , boardId)
                .orElseThrow(() -> new ItemNotFoundException("Status not found"));

        if ("No Status".equals(status.getName()) || "Done".equals(status.getName())) {
            throw new BadRequestException("Cannot delete this status");
        }
        if (!tasksRepository.findByStatus_StatusIdAndBoards_BoardId(statusId ,boardId ).isEmpty()) {
            throw new BadRequestException("Cannot delete status with associated tasks");
        }
        statusesRepository.delete(status);
        return status;
    }

    public Statuses reassignAndDeleteStatus(Integer statusId, Integer newStatusId, String boardId) {
        Users currentUser = checkBoardAccess(boardId);

        if (statusId.equals(newStatusId)) {
            throw new BadRequestException("Source and destination statuses cannot be the same");
        }

        Statuses oldStatus = statusesRepository.findByStatusIdAndBoardsBoardId(statusId,boardId)
                .orElseThrow(() -> new ItemNotFoundException("Status not found"));

        Statuses newStatus = statusesRepository.findByStatusIdAndBoardsBoardId(newStatusId,boardId)
                .orElseThrow(() -> new ItemNotFoundException("New status not found"));

        List<Tasks> tasksWithThisStatus = tasksRepository.findByStatus_StatusIdAndBoards_BoardId(statusId,boardId);

        if (tasksWithThisStatus.isEmpty()) {
            throw new BadRequestException("Destination status not found");
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
            throw new BadRequestException("Status name cannot be null or empty!");
        }
        if (description != null && description.trim().isEmpty()) {
            throw new BadRequestException("Status description cannot be empty!");
        }
        if (description != null){
            status.setDescription(description.trim());

        }else {
            status.setName(name.trim());
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
            Optional<CollabBoard> optionalInvitation = collabBoardRepository.findCollabByOidAndBoardsId(currentUser.getOid(), boardId);
            CollabBoard invitation = optionalInvitation.get();

            if (InviteStatus.PENDING.equals(invitation.getStatusInvite())) {
                throw new ForbiddenException("You're not collaborator");
            }

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
