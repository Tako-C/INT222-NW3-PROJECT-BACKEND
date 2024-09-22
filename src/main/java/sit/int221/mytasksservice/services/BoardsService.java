package sit.int221.mytasksservice.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.mytasksservice.dtos.response.request.BoardUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.BoardsAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.*;
import sit.int221.mytasksservice.models.primary.Boards;
import sit.int221.mytasksservice.models.secondary.Users;
import sit.int221.mytasksservice.repositories.primary.BoardsRepository;
import sit.int221.mytasksservice.repositories.secondary.UsersRepository;

import java.util.List;
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

    public List<BoardsResponseDTO> getBoardsByOid() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Boards> boardsList;

        if (username.equals("anonymousUser")) {
            boardsList = boardsRepository.findPublicBoards();
        } else {
            Users users = usersRepository.findByUsername(username);
            boardsList = boardsRepository.findByOidOrVisibility(users.getOid());
        }

        return boardsList.stream().map(board -> {
            BoardsResponseDTO dto = modelMapper.map(board, BoardsResponseDTO.class);
            dto.setOwner(getOwnerByOid(board.getOid()));

//            dto.setTasks(board.getTasks().stream()
//                    .map(task -> {
//                        TaskTableResponseDTO taskDTO = modelMapper.map(task, TaskTableResponseDTO.class);
//                        taskDTO.setBoardName(board.getBoard_name());
//                        return taskDTO;
//                    })
//                    .collect(Collectors.toList()));
//            dto.setStatuses(board.getStatuses().stream()
//                    .map(status -> modelMapper.map(status, StatusTableResponseDTO.class))
//                    .collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }


//    public List<BoardsResponseDTO> getAllBoards(){
//        List<Boards> boards = boardsRepository.findAll();
//        return boards.stream().map(board ->
//                modelMapper.map(board, BoardsResponseDTO.class)
//        ).collect(Collectors.toList());
//    }

    public Boards createBoards(BoardsAddRequestDTO boardsAddRequestDTO){
        Boards boards = modelMapper.map(boardsAddRequestDTO, Boards.class);
        return boardsRepository.save(boards);
    }

    public BoardsResponseDTO getBoardById(String id) {
        Boards board = boardsRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board not found"));

        // ตรวจสอบว่ามีผู้ใช้ล็อกอินหรือไม่
        Users currentUser = null;
        String username = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;

        if (username != null) {
            currentUser = usersRepository.findByUsername(username);
        }

        // ตรวจสอบสิทธิ์การเข้าถึง
        if (currentUser != null && board.getOid().equals(currentUser.getOid())) {
            return modelMapper.map(board, BoardsResponseDTO.class);
        } else if (board.getVisibility().equals("public")) {
            // ให้เข้าถึงบอร์ดสาธารณะได้
            return modelMapper.map(board, BoardsResponseDTO.class);
        } else {
            throw new ForbiddenException("Access Denied");
        }
    }

    public BoardUpdateRequestDTO updateBoardVisibility(String id, BoardUpdateRequestDTO boardupdateRequestDTO) {
        Boards board = boardsRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Board not found"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = usersRepository.findByUsername(username);

        // ตรวจสอบว่าเป็นเจ้าของหรือไม่
        if (!board.getOid().equals(currentUser.getOid())) {
            throw new ForbiddenException("Access Denied");
        }

        // อัปเดต visibility
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
}
