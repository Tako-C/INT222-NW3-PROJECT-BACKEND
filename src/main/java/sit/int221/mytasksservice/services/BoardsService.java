package sit.int221.mytasksservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.mytasksservice.dtos.response.request.BoardsAddRequestDTO;
import sit.int221.mytasksservice.dtos.response.response.BoardsResponseDTO;
import sit.int221.mytasksservice.dtos.response.response.ItemNotFoundException;
import sit.int221.mytasksservice.dtos.response.response.StatusTableResponseDTO;
import sit.int221.mytasksservice.models.primary.Boards;
import sit.int221.mytasksservice.repositories.primary.BoardsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardsService {
    @Autowired
    private BoardsRepository boardsRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Boards getBoards(String boards){
        return boardsRepository.findById(boards).orElseThrow(ItemNotFoundException::new);
    }

    public List<BoardsResponseDTO> getAllBoards(){
        List<Boards> boards = boardsRepository.findAll();
        return boards.stream().map(board ->
                modelMapper.map(board, BoardsResponseDTO.class)
        ).collect(Collectors.toList());
    }

    public Boards createBoards(BoardsAddRequestDTO boardsAddRequestDTO){
        Boards boards = modelMapper.map(boardsAddRequestDTO, Boards.class);

        return boardsRepository.save(boards);
    }

}