package sit.int221.mytasksservice.services;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        List<BoardsResponseDTO> collaborativeBoards = new ArrayList<>();

        if (username.equals("anonymousUser")) {
            boardsSet.addAll(boardsRepository.findPublicBoards());
        } else {
            Users users = usersRepository.findByUsername(username);
            boardsSet.addAll(boardsRepository.findByOidOrVisibility(users.getOid()));

            // สร้างตัวแปรเพื่อเก็บ Oid ของผู้ใช้ที่ล็อกอิน
            String loggedInOid = users.getOid();

            // ตรวจสอบบอร์ดร่วม
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

                            CollabBoard collabInfo = collabBoardRepository.findAccessRightByBoardsIdAndOid(board.getBoardId(), loggedInOid)
                                    .orElse(null);

                            if (collabInfo != null) {
                                dto.setAccessRight(collabInfo.getAccessRight());
                            }

                            return dto;
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
                .map(BoardsResponseDTO::getBoardId)
                .toList();

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
            currentUser = usersRepository.findByUsername(username);
        }

        return mapBoardDetails(board, currentUser);
    }

    public BoardUpdateRequestDTO updateBoardVisibility(String id, BoardUpdateRequestDTO boardupdateRequestDTO) {
        Boards board = boardsRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board not found"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = usersRepository.findByUsername(username);

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
            throw new ItemNotFoundException();
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
