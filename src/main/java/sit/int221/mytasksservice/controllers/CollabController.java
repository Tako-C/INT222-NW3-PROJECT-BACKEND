package sit.int221.mytasksservice.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.mytasksservice.dtos.response.request.CollabAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.CollabResponseDTO;
import sit.int221.mytasksservice.repositories.primary.CollabBoardRepository;
import sit.int221.mytasksservice.services.CollabService;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}")
public class CollabController {

    @Autowired
    private CollabBoardRepository collabBoardRepository;

    @Autowired
    private CollabService collabService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/collabs")
    public List<CollabResponseDTO> getAllCollabs(@PathVariable String boardId) {
        return collabService.getAllCollabs(boardId);
    }

    @GetMapping("/collabs/{collab_oid}")
    public ResponseEntity<CollabResponseDTO> getCollabByOid(@PathVariable String boardId, @PathVariable String collab_oid) {
        CollabResponseDTO collabResponseDTO = collabService.getCollabByOid(boardId, collab_oid);
        return ResponseEntity.ok(collabResponseDTO);
    }

    @PostMapping("/collabs")
    public ResponseEntity<CollabResponseDTO> addCollabToBoard(@PathVariable String boardId, @Valid @RequestBody CollabAddRequestDTO collabAddRequestDTO) {
        CollabResponseDTO newCollab = collabService.addCollabToBoard(boardId, collabAddRequestDTO);
        return ResponseEntity.ok(newCollab);
    }
}
