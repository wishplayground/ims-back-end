package lk.ijse.dep11.ims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO implements Serializable {
    @Null(message = "ID Should be Empty")
    private Integer id;
    @Pattern(regexp = "^[A-Za-z ]{4,}$",message = "Invalid Name")
    private String name;
    @Pattern(regexp = "^\\d{3}-\\d{7}$",message = "Invalid Name")
    private String contact;
}
