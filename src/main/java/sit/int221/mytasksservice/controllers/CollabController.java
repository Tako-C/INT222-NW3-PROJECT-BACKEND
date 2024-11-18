package sit.int221.mytasksservice.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sit.int221.mytasksservice.dtos.response.request.CollabAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.CollabUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.InvitationRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.CollabResponseDTO;
import sit.int221.mytasksservice.services.CollabService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23nw3.sit.kmutt.ac.th:3333","http://intproj23.sit.kmutt.ac.th"})
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

//    @PostMapping("/collabs")
//    public ResponseEntity<CollabResponseDTO> addCollabToBoard(@PathVariable String boardId, @Valid @RequestBody CollabAddRequestDTO collabAddRequestDTO) {
//        CollabResponseDTO newCollab = collabService.addCollabToBoard(boardId, collabAddRequestDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(newCollab);
//    }

//    @PatchMapping("/collabs/{collab_oid}")
//    public ResponseEntity<CollabResponseDTO> updateCollabAccessRight(
//            @PathVariable String boardId,
//            @PathVariable String collab_oid,
//            @Valid @RequestBody CollabUpdateRequestDTO collabUpdateRequestDTO) {
//        CollabResponseDTO updatedCollab = collabService.updateCollabAccessRight(boardId, collab_oid, collabUpdateRequestDTO);
//        return ResponseEntity.ok(updatedCollab);
//    }

    @DeleteMapping("/collabs/{collab_oid}")
    public ResponseEntity<CollabResponseDTO> removeCollabFromBoard(
            @PathVariable String boardId,
            @PathVariable String collab_oid) {
        CollabResponseDTO removedCollab = collabService.removeCollabFromBoard(boardId, collab_oid);
        return ResponseEntity.ok(removedCollab);
    }

    @PostMapping("/collabs/invitations")
    public ResponseEntity<String> sendInvitation(
            @PathVariable String boardId,
            @Valid @RequestBody InvitationRequestDTO invitationRequestDTO,
            Authentication authentication
    ) {
        String inviterUsername = authentication.getName();
        collabService.sendInvitation(boardId, invitationRequestDTO, inviterUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body("Invitation sent successfully");
    }

    @PostMapping("/collabs/invitations/accept")
    public ResponseEntity<String> acceptInvitation(
            @PathVariable String boardId,
            @RequestParam String token,
            Authentication authentication
    ) {
        String inviteeUsername = authentication.getName();
        collabService.acceptInvitation(token, inviteeUsername);
        return ResponseEntity.ok("Invitation accepted");
    }

    @PostMapping("/collabs/invitations/decline")
    public ResponseEntity<String> declineInvitation(
            @PathVariable String boardId,
            @RequestParam String token,
            Authentication authentication
    ) {
        String inviteeUsername = authentication.getName();
        collabService.declineInvitation(token, inviteeUsername);
        return ResponseEntity.ok("Invitation declined");
    }
}
