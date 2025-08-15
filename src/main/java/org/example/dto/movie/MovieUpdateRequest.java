package org.example.dto.movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.constants.MovieCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieUpdateRequest {

    @NotNull
    private String description;
    @NotBlank
    private String rate;
    @NotBlank
    private String imageUrl;
    @NotNull
    private MovieCategory category;

}
