package hexlet.code.dto.tasks;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TaskDTO {

    private Long id;

    private String title;

    private Long index;

    private String content;

    private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    private List<Long> taskLabelIds;
}
