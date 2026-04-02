package tourist_cocoon.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tourist_cocoon.dto.CheckInRequestDTO;
import tourist_cocoon.dto.CheckInResponseDTO;
import tourist_cocoon.service.CheckInService;

@RestController
@RequestMapping("/checkin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @PostMapping
    public ResponseEntity<CheckInResponseDTO> realizarCheckIn(
            @Valid @RequestBody CheckInRequestDTO dto
    ) {
        CheckInResponseDTO response = checkInService.realizarCheckIn(dto);
        return ResponseEntity.ok(response);
    }
}