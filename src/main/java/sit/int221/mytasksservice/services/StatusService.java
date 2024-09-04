package sit.int221.mytasksservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.mytasksservice.dtos.response.request.StatusAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.request.StatusUpdateRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.ItemNotFoundException;
import sit.int221.mytasksservice.models.primary.Kradanboard;
import sit.int221.mytasksservice.models.primary.Status;
import sit.int221.mytasksservice.repositories.primary.KradanboardRepository;
import sit.int221.mytasksservice.repositories.primary.StatusRepository;

import java.util.List;

@Service
public class StatusService {

    @Autowired
    private StatusRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private KradanboardRepository kradanboardRepository;

    public List<Status> getAllStatus() {
        return repository.findAll();
    }

    public Status getStatus(Integer id) {
        return repository.findById(id)
                .orElseThrow(ItemNotFoundException::new);
    }

    public Status createNewStatus(StatusAddRequestDTO statusAddRequestDTO) {
        checkStatusNameExists(statusAddRequestDTO.getName());

        Status status = modelMapper.map(statusAddRequestDTO, Status.class);
        trimAndValidateStatusFields(status, statusAddRequestDTO.getName(), statusAddRequestDTO.getDescription());;

        return repository.save(status);
    }

    public Status updateStatus(StatusUpdateRequestDTO statusUpdateRequestDTO) {
        Status status = repository.findById(statusUpdateRequestDTO.getStatusId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status not found"));

        if ("No Status".equals(status.getName()) || "Done".equals(status.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify this status");
        }

        checkStatusNameExists(statusUpdateRequestDTO.getName());

        status.setName(statusUpdateRequestDTO.getName());
        status.setDescription(statusUpdateRequestDTO.getDescription());

        return repository.save(status);
    }

    public void deleteStatus(Integer id) {
        Status status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status not found"));

        if ("No Status".equals(status.getName()) || "Done".equals(status.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete this status");
        }

        if (!kradanboardRepository.findByStatusId(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete status with associated tasks");
        }

        repository.deleteById(id);
    }

    public Status reassignAndDeleteStatus(Integer id, Integer newId) {
        if (id.equals(newId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source and destination statuses cannot be the same");
        }

        Status oldStatus = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source status not found"));

        Status newStatus = repository.findById(newId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination status not found"));

        List<Kradanboard> tasksWithThisStatus = kradanboardRepository.findByStatusId(id);

        tasksWithThisStatus.forEach(task -> {
            task.setStatus(newStatus);
            kradanboardRepository.save(task);
        });

        repository.deleteById(id);
        return oldStatus;
    }

    private void trimAndValidateStatusFields(Status status, String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status name cannot be null or empty!");
        }
        if (description != null && description.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status description cannot be empty!");
        }
        if (description != null){
            status.setDescription(description.trim());

        }else {
            status.setName(name.trim());
        }
    }

    private void checkStatusNameExists(String name) {
        if (repository.findByName(name) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status name already exists!");
        }
    }

}
