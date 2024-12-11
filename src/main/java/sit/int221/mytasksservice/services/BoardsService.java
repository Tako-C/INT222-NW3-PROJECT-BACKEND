package sit.int221.mytasksservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sit.int221.mytasksservice.dtos.response.request.BoardUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.BoardsAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.*;
import sit.int221.mytasksservice.models.primary.Boards;
import sit.int221.mytasksservice.models.primary.CollabBoard;
import sit.int221.mytasksservice.models.secondary.Users;
import sit.int221.mytasksservice.repositories.primary.BoardsRepository;
import sit.int221.mytasksservice.repositories.primary.CollabBoardRepository;
import sit.int221.mytasksservice.repositories.secondary.UsersRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardsService {
    @Autowired
    private BoardsRepository boardsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CollabBoardRepository collabBoardRepository;

    public Map<String, Object> getAllBoards() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Set<Boards> boardsSet = new HashSet<>();
        List<Map<String, Object>> collaborativeBoards = new ArrayList<>();

        if (username.equals("anonymousUser")) {
            boardsSet.addAll(boardsRepository.findPublicBoards());
        } else {
            Users users = usersRepository.findByUsername(username)
                    .orElseThrow(() -> new ItemNotFoundException("User not found"));
            boardsSet.addAll(boardsRepository.findByOidOrVisibility(users.getOid()));

            String loggedInOid = users.getOid();

            List<String> collabBoardIds = collabBoardRepository.findBoardsIdByOid(loggedInOid);
            if (!collabBoardIds.isEmpty()) {
                List<Boards> collabBoards = boardsRepository.findByBoardIdIn(collabBoardIds);
                collaborativeBoards = collabBoards.stream()
                        .map(board -> {
                            BoardsResponseDTO dto = modelMapper.map(board, BoardsResponseDTO.class);

                            Owner ownerInfo = new Owner();
                            ownerInfo.setName(getOwnerByOid(board.getOid()).getName());
                            ownerInfo.setOid(board.getOid());
                            dto.setOwner(ownerInfo);

                            CollabBoard collabInfo = collabBoardRepository
                                    .findAccessRightByBoardsIdAndOid(board.getBoardId(), loggedInOid)
                                    .orElse(null);

                            Map<String, Object> boardMap = new HashMap<>();
                            boardMap.put("boardId", dto.getBoardId());
                            boardMap.put("owner", dto.getOwner());
                            boardMap.put("board_name", dto.getBoard_name());
                            boardMap.put("visibility", dto.getVisibility());
                            boardMap.put("accessRight", collabInfo.getAccessRight());
                            boardMap.put("createdOn", dto.getCreatedOn());
                            boardMap.put("updatedOn", dto.getUpdatedOn());

                            if (collabInfo != null && collabInfo.getStatusInvite() != null) {
                                boardMap.put("status", collabInfo.getStatusInvite().name());
                            } else {
                                boardMap.put("status", "PENDING");
                            }

                            return boardMap;
                        })
                        .collect(Collectors.toList());
            }
        }

        List<BoardsResponseDTO> personalBoards = boardsSet.stream()
                .map(board -> {
                    BoardsResponseDTO dto = modelMapper.map(board, BoardsResponseDTO.class);

                    Owner ownerInfo = new Owner();
                    ownerInfo.setName(getOwnerByOid(board.getOid()).getName());
                    ownerInfo.setOid(board.getOid());
                    dto.setOwner(ownerInfo);

                    return dto;
                })
                .collect(Collectors.toList());

        List<String> collabBoardIdsToRemove = collaborativeBoards.stream()
                .map(board -> (String) board.get("boardId"))
                .collect(Collectors.toList());

        personalBoards.removeIf(dto -> collabBoardIdsToRemove.contains(dto.getBoardId()));

        Map<String, Object> response = new HashMap<>();
        response.put("boards", personalBoards);
        response.put("collaborate", collaborativeBoards);

        return response;
    }

    public Boards createBoards(BoardsAddRequestDTO boardsAddRequestDTO) {
        Boards boards = modelMapper.map(boardsAddRequestDTO, Boards.class);
        return boardsRepository.save(boards);
    }

    public BoardDetailResponseDTO getBoardById(String id) {
        Boards board = boardsRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = null;

        if (!username.equals("anonymousUser")) {
            currentUser = usersRepository.findByUsername(username).orElseThrow(() -> new ItemNotFoundException("User not found"));
        }

        return mapBoardDetails(board, currentUser);
    }

    public BoardUpdateRequestDTO updateBoardVisibility(String id, BoardUpdateRequestDTO boardupdateRequestDTO) {

        Boards board = boardsRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board not found"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = usersRepository.findByUsername(username).orElseThrow(() -> new ItemNotFoundException("User not found"));

        if (!board.getOid().equals(currentUser.getOid())) {
            throw new ForbiddenException("Access Denied");
        }

        board.setVisibility(boardupdateRequestDTO.getVisibility());
        boardsRepository.save(board);

        return modelMapper.map(board, BoardUpdateRequestDTO.class);
    }


    public Users findByOid(String oid) {
        Users user = usersRepository.findByOid(oid);
        if (user == null) {
            throw new ItemNotFoundException("Oid not found");
        }
        return user;
    }

    public Owner getOwnerByOid(String oid) {
        Users user = usersRepository.findByOid(oid);
        if (user != null) {
            Owner owner = new Owner();
            owner.setOid(user.getOid());
            owner.setName(user.getName());
            return owner;
        }
        return null;
    }

    private BoardDetailResponseDTO mapBoardDetails(Boards board, Users currentUser) {
        String currentUserOid = (currentUser != null) ? currentUser.getOid() : null;

        if ((currentUserOid != null && board.getOid().equals(currentUserOid))
                || (currentUserOid != null && collabBoardRepository.existsByOidAndBoardsId(currentUserOid, board.getBoardId()))  // ผู้ใช้เป็น collaborator
                || board.getVisibility().equals("public")) {

            BoardDetailResponseDTO dto = modelMapper.map(board, BoardDetailResponseDTO.class);
            dto.setOwner(getOwnerByOid(board.getOid()));

            dto.setTasks(board.getTasks().stream()
                    .map(task -> {
                        TaskTableResponseDTO taskDTO = modelMapper.map(task, TaskTableResponseDTO.class);
                        taskDTO.setBoardName(board.getBoard_name());
                        return taskDTO;
                    })
                    .collect(Collectors.toList()));

            dto.setStatuses(board.getStatuses().stream()
                    .map(status -> modelMapper.map(status, StatusTableResponseDTO.class))
                    .collect(Collectors.toList()));

            return dto;
        } else {
            throw new ForbiddenException("Access Denied");
        }
    }
}
