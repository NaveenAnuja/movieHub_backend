package org.example.dto.suggestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.movie.MovieRequest;
import org.example.dto.user.UserRequest;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionRequest {

    private Integer id;

    @NotBlank
    @Size(min = 5, max = 50)
    private String comment;

    private UserRequest userRequest;

    private MovieRequest movieRequest;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
