package org.example.dto.suggestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionUpdateRequest {

    @NotBlank
    @Size(min = 5, max = 50)
    private String comment;
}
