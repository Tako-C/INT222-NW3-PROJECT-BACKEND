package sit.int221.mytasksservice.controllers;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.mytasksservice.dtos.response.request.BoardsAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.StatusAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.BoardsResponseDTO;
import sit.int221.mytasksservice.models.primary.Boards;
import sit.int221.mytasksservice.models.primary.Statuses;
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

    @GetMapping("/boards")
    public List<BoardsResponseDTO> getAllBoards(){
        return boardsService.getAllBoards();
    }

    @PostMapping("/boards")
    public ResponseEntity<BoardsAddRequestDTO> addBoards(@Valid @RequestBody BoardsAddRequestDTO boardsAddRequestDTO){

        String generatedBoardId = NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, 10);
        boardsAddRequestDTO.setBoardId(generatedBoardId);
        Boards createBoard = boardsService.createBoards(boardsAddRequestDTO);
        BoardsAddRequestDTO addRequestDTO = modelMapper.map(createBoard, BoardsAddRequestDTO.class);

        URI location = URI.create("/boards/" + generatedBoardId);
        return ResponseEntity.created(location).body(addRequestDTO);
    }


}
