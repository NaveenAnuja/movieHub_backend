package org.example.dto.movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.constants.MovieCategory;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieRequest {

    private Integer id;

    @NotBlank
    private String movieName;

    @NotNull
    private String description;

    @NotBlank
    private String rate;

    @NotNull
    private MovieCategory category;

    @NotBlank
    private String imageUrl;

}
