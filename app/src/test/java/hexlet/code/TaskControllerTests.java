package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.util.ModelGenerator;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTests {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    private JwtRequestPostProcessor token;

    private Task testTask;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testTask = Instancio.of(modelGenerator.getTaskModel())
                .create();
        taskRepository.save(testTask);
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void testCreate() throws Exception {

        var data = new HashMap<>();
        data.put("title", testTask.getName());
        data.put("index", testTask.getIndex());
        data.put("content", testTask.getDescription());
        data.put("status", testTask.getTaskStatus().getSlug());
        data.put("assignee_id", testTask.getAssignee().getId());

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var task = taskRepository.findByName(testTask.getName()).get();

        assertThat(task.getIndex()).isEqualTo(testTask.getIndex());
        assertThat(task.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(task.getTaskStatus()).isEqualTo(testTask.getTaskStatus());
        assertThat(task.getAssignee()).isEqualTo(testTask.getAssignee());
    }

    @Test
    public void testUpdate() throws Exception {

        var data = new HashMap<>();
        data.put("title", "NewTitle");
        data.put("index", 1);
        data.put("content", "NewContent");

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId()).get();

        assertThat(task.getName()).isEqualTo("NewTitle");
        assertThat(task.getIndex()).isEqualTo(1);
        assertThat(task.getDescription()).isEqualTo("NewContent");
    }
}
