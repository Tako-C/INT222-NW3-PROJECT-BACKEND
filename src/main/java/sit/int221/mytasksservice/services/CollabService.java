package sit.int221.mytasksservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sit.int221.mytasksservice.dtos.response.request.CollabAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.CollabResponseDTO;
import sit.int221.mytasksservice.dtos.response.response.ForbiddenException;
import sit.int221.mytasksservice.dtos.response.response.ItemNotFoundException;
import sit.int221.mytasksservice.models.primary.Boards;
import sit.int221.mytasksservice.models.primary.CollabBoard;
import sit.int221.mytasksservice.models.secondary.Users;
import sit.int221.mytasksservice.repositories.primary.BoardsRepository;
import sit.int221.mytasksservice.repositories.primary.CollabBoardRepository;
import sit.int221.mytasksservice.repositories.secondary.UsersRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollabService {

    @Autowired
    private CollabBoardRepository collabBoardRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BoardsRepository boardsRepository;

    @Autowired
    private UsersRepository usersRepository;

    public List<CollabResponseDTO> getAllCollabs(String boardId) {
        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = usersRepository.findByUsername(username);

        boolean isOwner = board.getOid().equals(currentUser.getOid());
        boolean isCollaborator = collabBoardRepository.existsByOidAndBoardsId(currentUser.getOid(), boardId);
        boolean isPublicBoard = board.getVisibility().equals("public");

        if (!isOwner && !isCollaborator && !isPublicBoard) {
            throw new ForbiddenException("You are not allowed to access the collaborators of this board.");
        }

        List<CollabBoard> collabList = collabBoardRepository.findByBoardsId(boardId);

        return collabList.stream()
                .map(collab -> modelMapper.map(collab, CollabResponseDTO.class))
                .collect(Collectors.toList());
    }

    public CollabResponseDTO getCollabByOid(String boardId, String collabOid) {
        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = usersRepository.findByUsername(username);

        CollabBoard collabBoard = collabBoardRepository.findCollabByOidAndBoardsId(collabOid, boardId)
                .orElseThrow(() -> new ItemNotFoundException("Collaborator not found in the specified board."));

        boolean isCollaborator = collabBoardRepository.existsByOidAndBoardsId(collabOid, boardId);
        boolean isOwner = board.getOid().equals(currentUser.getOid());
        boolean isPublicBoard = board.getVisibility().equals("public");
        boolean isCurrentUserCollaborator = collabBoardRepository.existsByOidAndBoardsId(currentUser.getOid(), boardId);

        if (isCollaborator && (isPublicBoard || isOwner || isCurrentUserCollaborator)) {
            return modelMapper.map(collabBoard, CollabResponseDTO.class);
        } else {
            throw new ForbiddenException("You are not allowed to access this collab.");
        }
    }

    public CollabResponseDTO addCollabToBoard(String boardId, CollabAddRequestDTO collabAddRequestDTO) {
        Boards board = boardsRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board not found"));

        Users collaborator = usersRepository.findByEmail(collabAddRequestDTO.getEmail())
                .orElseThrow(() -> new ItemNotFoundException("User with this email not found"));

        CollabBoard collabBoard = new CollabBoard();
        collabBoard.setBoardsId(boardId);
        collabBoard.setOid(collaborator.getOid());
        collabBoard.setEmail(collaborator.getEmail());
        collabBoard.setName(collaborator.getName());
        collabBoard.setAccessRight(collabAddRequestDTO.getAccessRight());

        collabBoardRepository.save(collabBoard);

        return modelMapper.map(collabBoard, CollabResponseDTO.class);
    }
}
