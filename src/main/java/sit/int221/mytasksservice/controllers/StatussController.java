//package sit.int221.mytasksservice.controllers;
//
//import jakarta.validation.Valid;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import sit.int221.mytasksservice.dtos.response.request.StatusAddRequestDTO;
//import sit.int221.mytasksservice.dtos.response.request.StatusDeleteRequestDTO;
//import sit.int221.mytasksservice.dtos.response.request.StatusUpdateRequestDTO;
//import sit.int221.mytasksservice.dtos.response.response.StatusDetailResponseDTO;
//import sit.int221.mytasksservice.dtos.response.response.StatusTableResponseDTO;
//import sit.int221.mytasksservice.models.primary.Status;
//import sit.int221.mytasksservice.services.StatusService;
//
//import java.net.URI;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@CrossOrigin(origins = {"http://localhost:5173","http://ip23nw3.sit.kmutt.ac.th:3333","http://intproj23.sit.kmutt.ac.th"})
//
//@RequestMapping("/v2")
//public class StatussController {
//    @Autowired
//    private StatusService service;
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @GetMapping("/statuses")
//    public List<StatusTableResponseDTO> getAllStatus(){
//        List<Status> statusList = service.getAllStatus();
//        return statusList.stream()
//                .map(status -> {
//                    StatusTableResponseDTO responseDTO = modelMapper.map(status, StatusTableResponseDTO.class);
//                    return responseDTO;
//                })
//                .collect(Collectors.toList());
//    }
//    @GetMapping("/statuses/{id}")
//    public StatusDetailResponseDTO getStatusById(@PathVariable Integer id){
//        Status status = service.getStatus(id);
//        StatusDetailResponseDTO responseDTO = modelMapper.map(status, StatusDetailResponseDTO.class);
//        return responseDTO;
//
//    }
//    @PostMapping("/statuses")
//    public ResponseEntity<StatusAddRequestDTO> addStatus(@Valid @RequestBody StatusAddRequestDTO statusAddRequestDTO){
//        Status createStatus = service.createNewStatus(statusAddRequestDTO);
//        StatusAddRequestDTO addRequestDTO = modelMapper.map(createStatus, StatusAddRequestDTO.class);
//        URI location = URI.create("/Statuses/");
//        return ResponseEntity.created(location).body(addRequestDTO);
//    }
//
//    @PutMapping("/statuses/{id}")
//    public ResponseEntity<StatusUpdateRequestDTO> updateTask (@RequestBody StatusAddRequestDTO statusAddRequestDTO, @PathVariable Integer id) {
//        Status updatedStatus = service.getStatus(id);
//        StatusUpdateRequestDTO updatedStatusDTO = modelMapper.map(updatedStatus, StatusUpdateRequestDTO.class);
//
//        updatedStatusDTO.setName(statusAddRequestDTO.getName());
//        updatedStatusDTO.setDescription(statusAddRequestDTO.getDescription());
//
//        service.updateStatus(updatedStatusDTO);
//        return ResponseEntity.ok().body(updatedStatusDTO);
//    }
//
//    @DeleteMapping("/statuses/{id}")
//    public ResponseEntity<StatusDeleteRequestDTO> deleteStatus(@PathVariable Integer id) {
//        Status deletedStatus = service.getStatus(id);
//        StatusDeleteRequestDTO deletedStatusDTO = modelMapper.map(deletedStatus, StatusDeleteRequestDTO.class);
//        deletedStatus.setId(deletedStatusDTO.getStatusId());
//        deletedStatus.setName(deletedStatusDTO.getName());
//        deletedStatus.setDescription(deletedStatusDTO.getDescription());
//        service.deleteStatus(id);
//        return ResponseEntity.ok().body(deletedStatusDTO);
//    }
//
//    @DeleteMapping("/statuses/{id}/{newId}")
//    public ResponseEntity<StatusDeleteRequestDTO> deleteStatusAndReassign(@PathVariable Integer id, @PathVariable Integer newId) {
//        Status deletedStatus = service.reassignAndDeleteStatus(id, newId);
//        StatusDeleteRequestDTO deletedStatusDTO = modelMapper.map(deletedStatus, StatusDeleteRequestDTO.class);
//        return ResponseEntity.ok().body(deletedStatusDTO);
//    }
//}