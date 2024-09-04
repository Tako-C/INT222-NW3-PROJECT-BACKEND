package sit.int221.mytasksservice.controllers;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.mytasksservice.config.JwtTokenUtil;
import sit.int221.mytasksservice.dtos.response.request.BoardsAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.BoardsResponseDTO;
import sit.int221.mytasksservice.dtos.response.response.ItemNotFoundException;
import sit.int221.mytasksservice.models.primary.Boards;
import sit.int221.mytasksservice.models.secondary.Users;
import sit.int221.mytasksservice.services.BoardsService;

import java.net.URI;
import java.util.List;


@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23nw3.sit.kmutt.ac.th:3333","http://intproj23.sit.kmutt.ac.th"})

@RequestMapping("/v3")
public class BoardsController {
    @Autowired
    private BoardsService boardsService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/boards")
    public List<BoardsResponseDTO> getAllBoards(){
        return boardsService.getAllBoards();
    }

    @PostMapping("/boards")
    public ResponseEntity<BoardsAddRequestDTO> addBoards(@Valid @RequestBody BoardsAddRequestDTO boardsAddRequestDTO,
                                                         @RequestHeader("Authorization") String token ){
        String oid = jwtTokenUtil.getOid(token.replace("Bearer ", ""));
        Users user = boardsService.findByOid(oid);

        String generatedBoardId = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 10);
        boardsAddRequestDTO.setBoardId(generatedBoardId);
        boardsAddRequestDTO.setOid(user.getOid());

        Boards createBoard = boardsService.createBoards(boardsAddRequestDTO);
        BoardsAddRequestDTO addRequestDTO = modelMapper.map(createBoard, BoardsAddRequestDTO.class);

        URI location = URI.create("/boards/" + generatedBoardId);
        return ResponseEntity.created(location).body(addRequestDTO);
    }
}
