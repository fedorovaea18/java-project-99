package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
public class LabelControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    private JwtRequestPostProcessor token;

    private Label testLabel;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testLabel = Instancio.of(modelGenerator.getLabelModel())
                .create();
        labelRepository.save(testLabel);
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/labels").with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void testCreate() throws Exception {

        var data = Instancio.of(modelGenerator.getLabelModel())
                .create();

        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var label = labelRepository.findByName(data.getName()).get();

        assertNotNull(label);
        assertThat(labelRepository.findByName(testLabel.getName())).isPresent();
        assertThat(label.getName()).isEqualTo(data.getName());
    }

    @Test
    public void testUpdate() throws Exception {

        var data = new HashMap<>();
        data.put("name", "NewName");

        var request = put("/api/labels/" + testLabel.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var label = labelRepository.findById(testLabel.getId()).get();

        assertThat(label.getName()).isEqualTo("NewName");
    }
}
