package sit.int221.mytasksservice.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.mytasksservice.dtos.response.request.CollabAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.CollabUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.CollabResponseDTO;
import sit.int221.mytasksservice.services.CollabService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v3/boards/{boardId}")
public class CollabController {

    @Autowired
    private CollabService collabService;

    @GetMapping("/collabs")
    public ResponseEntity<Map<String, Object>> getAllCollabs(@PathVariable String boardId) {
        Map<String, Object> collabResponse = collabService.getAllCollabs(boardId);
        return ResponseEntity.ok(collabResponse);
    }

    @GetMapping("/collabs/{collab_oid}")
    public ResponseEntity<CollabResponseDTO> getCollabByOid(@PathVariable String boardId, @PathVariable String collab_oid) {
        CollabResponseDTO collabResponseDTO = collabService.getCollabByOid(boardId, collab_oid);
        return ResponseEntity.ok(collabResponseDTO);
    }

    @PostMapping("/collabs")
    public ResponseEntity<CollabResponseDTO> addCollabToBoard(@PathVariable String boardId, @Valid @RequestBody CollabAddRequestDTO collabAddRequestDTO) {
        CollabResponseDTO newCollab = collabService.addCollabToBoard(boardId, collabAddRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCollab);
    }

    @PatchMapping("/collabs/{collab_oid}")
    public ResponseEntity<CollabResponseDTO> updateCollabAccessRight(
            @PathVariable String boardId,
            @PathVariable String collab_oid,
            @Valid @RequestBody CollabUpdateRequestDTO collabUpdateRequestDTO) {
        CollabResponseDTO updatedCollab = collabService.updateCollabAccessRight(boardId, collab_oid, collabUpdateRequestDTO);
        return ResponseEntity.ok(updatedCollab);
    }

    @DeleteMapping("/collabs/{collab_oid}")
    public ResponseEntity<CollabResponseDTO> removeCollabFromBoard(
            @PathVariable String boardId,
            @PathVariable String collab_oid) {
        CollabResponseDTO removedCollab = collabService.removeCollabFromBoard(boardId, collab_oid);
        return ResponseEntity.ok(removedCollab);
    }
}
