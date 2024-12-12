package sit.int221.mytasksservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sit.int221.mytasksservice.dtos.response.request.CollabAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.CollabUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.CollabListResponseDTO;
import sit.int221.mytasksservice.dtos.response.response.CollabResponseDTO;
import sit.int221.mytasksservice.dtos.response.response.InvitationResponseDTO;
import sit.int221.mytasksservice.models.primary.CollabBoard;
import sit.int221.mytasksservice.services.CollabService;

@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23nw3.sit.kmutt.ac.th:3333","http://intproj23.sit.kmutt.ac.th"})
@RequestMapping("v3/boards/{boardId}")
public class CollabController {

    @Autowired
    private CollabService collabService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/collabs")
    public ResponseEntity<CollabListResponseDTO> getAllCollabs(@PathVariable String boardId) {
        CollabListResponseDTO collabResponse = collabService.getAllCollabs(boardId);
        return ResponseEntity.ok(collabResponse);
    }

    @GetMapping("/collabs/{collab_oid}")
    public ResponseEntity<CollabResponseDTO> getCollabByOid(@PathVariable String boardId, @PathVariable String collab_oid) {
        CollabResponseDTO collabResponseDTO = collabService.getCollabByOid(boardId, collab_oid);
        return ResponseEntity.ok(collabResponseDTO);
    }

    @DeleteMapping("/collabs/{collab_oid}")
    public ResponseEntity<CollabResponseDTO> removeCollabFromBoard(
            @PathVariable String boardId,
            @PathVariable String collab_oid,
            Authentication authentication) {
        String ownerUsername = authentication.getName();

        CollabResponseDTO removedCollab = collabService.removeCollabFromBoard(boardId, collab_oid, ownerUsername);
        return ResponseEntity.ok(removedCollab);
    }

    @PostMapping("/collabs/invitations")
    public ResponseEntity<InvitationResponseDTO> sendInvitation(
            @PathVariable String boardId,
            @Valid @RequestBody CollabAddRequestDTO invitationRequestDTO,
            Authentication authentication,
            HttpServletRequest request
    ) {
        String inviterUsername = authentication.getName();
        CollabBoard collabBoard = collabService.sendInvitation(boardId, invitationRequestDTO, inviterUsername);

        CollabResponseDTO collabResponseDTO = modelMapper.map(collabBoard, CollabResponseDTO.class);

        InvitationResponseDTO response = new InvitationResponseDTO(
                HttpStatus.CREATED.value(),
                "Invitation sent successfully",
                request.getRequestURI(),
                collabResponseDTO
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/collabs/invitations/accept")
    public ResponseEntity<InvitationResponseDTO> acceptInvitation(
            @PathVariable String boardId,
            @RequestParam String token,
            Authentication authentication,
            HttpServletRequest request
    ) {
        String inviteeUsername = authentication.getName();
        CollabBoard collabBoard = collabService.acceptInvitation(token, inviteeUsername);

        CollabResponseDTO collabResponseDTO = modelMapper.map(collabBoard, CollabResponseDTO.class);

        InvitationResponseDTO response = new InvitationResponseDTO(
                HttpStatus.OK.value(),
                "Invitation accepted",
                request.getRequestURI(),
                collabResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/collabs/invitations/decline")
    public ResponseEntity<InvitationResponseDTO> declineInvitation(
            @PathVariable String boardId,
            @RequestParam String token,
            Authentication authentication,
            HttpServletRequest request
    ) {
        String inviteeUsername = authentication.getName();
        CollabResponseDTO collabResponseDTO = collabService.declineInvitation(token, inviteeUsername);

        InvitationResponseDTO response = new InvitationResponseDTO(
                HttpStatus.OK.value(),
                "Invitation declined",
                request.getRequestURI(),
                collabResponseDTO
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/collabs/invitations/{collab_oid}")
    public ResponseEntity<InvitationResponseDTO> updateInvitationAccessRight(
            @PathVariable String boardId,
            @PathVariable String collab_oid,
            @Valid @RequestBody CollabUpdateRequestDTO updateDTO,
            Authentication authentication,
            HttpServletRequest request
    ) {
        String ownerUsername = authentication.getName();
        CollabResponseDTO updatedCollab = collabService.updateInvitationAccessRight(boardId, collab_oid, updateDTO, ownerUsername);

        InvitationResponseDTO response = new InvitationResponseDTO(
                HttpStatus.OK.value(),
                "Invitation access right updated",
                request.getRequestURI(),
                updatedCollab
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/collabs/invitations/{collab_oid}")
    public ResponseEntity<InvitationResponseDTO> cancelInvitation(
            @PathVariable String boardId,
            @PathVariable String collab_oid,
            Authentication authentication,
            HttpServletRequest request
    ) {
        String ownerUsername = authentication.getName();
        collabService.cancelInvitation(boardId, collab_oid, ownerUsername);

        InvitationResponseDTO response = new InvitationResponseDTO(
                HttpStatus.OK.value(),
                "Invitation canceled",
                request.getRequestURI(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
