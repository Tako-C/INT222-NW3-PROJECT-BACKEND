//package sit.int221.mytasksservice.services;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//import sit.int221.mytasksservice.dtos.response.request.TaskAddRequestDTO;
//import sit.int221.mytasksservice.dtos.response.request.TaskUpdateRequestDTO;
//import sit.int221.mytasksservice.models.primary.Kradanboard;
//import sit.int221.mytasksservice.repositories.primary.KradanboardRepository;
//import org.modelmapper.ModelMapper;
//import sit.int221.mytasksservice.repositories.primary.StatusRepository;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class KradanboardService {
//    @Autowired
//    private KradanboardRepository repository;
//    @Autowired
//    private StatusRepository statusRepository;
//    @Autowired
//    private ModelMapper modelMapper;
//    public List<Kradanboard> getAllTasks() {
//        List<Kradanboard> tasks = repository.findAll();
//        for (Kradanboard task : tasks) {
//            trimTaskFields(task);
//        }
////        if (tasks.isEmpty()) {
////            throw new ResponseStatusException(HttpStatus.OK, "No tasks found");
//////            return Collections.emptyList();
////        }
//        return tasks;
//    }
//    public Kradanboard getTask(Integer id) {
//        Optional<Kradanboard> optionalTask = repository.findById(id);
//        if (optionalTask.isPresent()) {
//            Kradanboard task = optionalTask.get();
//            trimTaskFields(task);
//            return task;
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    public List<Kradanboard> getAllTasksSortByAsc(String sort) {
//        return repository.findAll(Sort.by(Sort. Direction.ASC, sort));
//    }
//    public List<Kradanboard> getAllFilter(List<String> filterStatuses, String sort) {
//        List<Kradanboard> filteredTasks = getAllTasksSortByAsc(sort).stream()
//                .filter(task -> filterStatuses.contains(task.getStatus().getName()))
//                .collect(Collectors.toList());
//        return filteredTasks;
//    }
//    public Kradanboard createNewTask(TaskAddRequestDTO taskAddRequestDTO) {
//        validateStatus(taskAddRequestDTO.getStatus());
//
//        Kradanboard task = modelMapper.map(taskAddRequestDTO, Kradanboard.class);
//        processTaskFields(task, taskAddRequestDTO.getDescription(), taskAddRequestDTO.getAssignees());
//
//        Integer convertStatusId = Integer.valueOf(taskAddRequestDTO.getStatus());
//        task.setStatus(statusRepository.findById(convertStatusId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status Not Found")));
//        return repository.save(task);
//    }
//    public Kradanboard updateTask(TaskUpdateRequestDTO taskUpdateRequestDTO) {
//        validateStatus(taskUpdateRequestDTO.getStatus());
//
//        Kradanboard task = modelMapper.map(taskUpdateRequestDTO, Kradanboard.class);
//        processTaskFields(task, taskUpdateRequestDTO.getDescription(), taskUpdateRequestDTO.getAssignees());
//
//        Integer convertStatusId = Integer.valueOf(taskUpdateRequestDTO.getStatus());
//        task.setStatus(statusRepository.findById(convertStatusId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status Not Found")));
//
//        return repository.save(task);
//    }
//    public void deleteTask(Integer id) {
//        repository.deleteById(id);
//    }
//
//    private void trimTaskFields(Kradanboard task) {
//        task.setAssignees(task.getAssignees() != null ? task.getAssignees().trim() : null);
//        task.setTitle(task.getTitle() != null ? task.getTitle().trim() : null);
//        task.setDescription(task.getDescription() != null ? task.getDescription().trim() : null);
//    }
//    private void validateStatus(String status) {
//        if (status == null || status.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required!");
//        }
//        try {
//            Integer convertStatusId = Integer.valueOf(status);
//            if (!statusRepository.existsById(convertStatusId)) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status does not exist");
//            }
//        } catch (NumberFormatException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status ID format");
//        }
//    }
//    private void processTaskFields(Kradanboard task, String description, String assignees) {
//        trimTaskFields(task);
//
//        if (description != null && description.isEmpty()) {
//            task.setDescription(null);
//        } else {
//            task.setDescription(description);
//        }
//
//        if (assignees != null && assignees.isEmpty()) {
//            task.setAssignees(null);
//        } else {
//            task.setAssignees(assignees);
//        }
//    }
//
//}
