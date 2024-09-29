package sit.int221.mytasksservice.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springdoc.core.service.GenericResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.mytasksservice.dtos.response.request.StatusAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.StatusDeleteRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.StatusUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.TaskDeleteRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.StatusDetailResponseDTO;
import sit.int221.mytasksservice.dtos.response.response.StatusTableResponseDTO;
import sit.int221.mytasksservice.models.primary.Statuses;
import sit.int221.mytasksservice.models.primary.Tasks;
import sit.int221.mytasksservice.services.BoardsService;
import sit.int221.mytasksservice.services.StatusesService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = {"http://localhost:5173","http://ip23nw3.sit.kmutt.ac.th:3333","http://intproj23.sit.kmutt.ac.th"})

@RequestMapping("/v3/boards")

public class StatusesController {
    @Autowired
    private StatusesService statusesService;

    @Autowired
    private ModelMapper modelMapper;

//    @Autowired
//    private BoardsService boardsService;


    @GetMapping("/{boardId}/statuses")
    public List<StatusTableResponseDTO> getAllStatus(@PathVariable String boardId){
        List<StatusTableResponseDTO> statuses = statusesService.getAllStatusesByBoard_id(boardId);
        return statuses;
    }
    @GetMapping("/{boardId}/statuses/{statusId}")
    public StatusDetailResponseDTO getStatuses(@PathVariable String boardId, @PathVariable String statusId){
        return statusesService.getStatusesByBoard_idAndByStatusID(boardId, Integer.valueOf(statusId));
    }
    @PostMapping("/{boardId}/statuses")
    public ResponseEntity<StatusAddRequestDTO> addStatuses(@Valid @RequestBody StatusAddRequestDTO statusAddRequestDTO, @PathVariable String boardId){
        statusAddRequestDTO.setBoards(boardId);
        Statuses createStatus = statusesService.createNewStatus(statusAddRequestDTO);
        StatusAddRequestDTO addRequestDTO = modelMapper.map(createStatus, StatusAddRequestDTO.class);
        URI location = URI.create("/"+boardId+"/statuses/");
        return ResponseEntity.created(location).body(addRequestDTO);
    }

    @PutMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<StatusUpdateRequestDTO> updateStatuses (@Valid @RequestBody StatusAddRequestDTO statusAddRequestDTO,@PathVariable String boardId, @PathVariable Integer statusId) {
        statusAddRequestDTO.setBoards(boardId);
        StatusDetailResponseDTO updatedStatus = statusesService.getStatusesByBoard_idAndByStatusID(boardId,statusId);
        StatusUpdateRequestDTO updatedStatusDTO = modelMapper.map(updatedStatus, StatusUpdateRequestDTO.class);
        updatedStatusDTO.setName(statusAddRequestDTO.getName());
        updatedStatusDTO.setDescription(statusAddRequestDTO.getDescription());
        updatedStatusDTO.setBoards(statusAddRequestDTO.getBoards());
        statusesService.updateStatus(updatedStatusDTO,statusId,boardId);
        return ResponseEntity.ok().body(updatedStatusDTO);
    }

    @DeleteMapping("/{boardId}/statuses/{statusId}")
    public ResponseEntity<StatusDeleteRequestDTO>
    deleteStatus(@PathVariable Integer statusId , @PathVariable  String boardId) {
        Statuses deletedStatus = statusesService.deleteStatus(statusId , boardId);
        StatusDeleteRequestDTO deletedStatusDTO = modelMapper.map(deletedStatus, StatusDeleteRequestDTO.class);
//        deletedStatus.setBoards(boardId);
        return ResponseEntity.ok().body(deletedStatusDTO);
    }

    @DeleteMapping("/{boardId}/statuses/{statusId}/{newStatusId}")
    public ResponseEntity<StatusDeleteRequestDTO> deleteStatusAndReassign(@PathVariable Integer statusId, @PathVariable Integer newStatusId ,@PathVariable String boardId) {
        Statuses deletedStatus = statusesService.reassignAndDeleteStatus(statusId, newStatusId,boardId);
        StatusDeleteRequestDTO deletedStatusDTO = modelMapper.map(deletedStatus, StatusDeleteRequestDTO.class);
        return ResponseEntity.ok().body(deletedStatusDTO);
    }



}
