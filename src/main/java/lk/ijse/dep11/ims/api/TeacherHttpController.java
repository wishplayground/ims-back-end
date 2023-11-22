package lk.ijse.dep11.ims.api;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.dto.TeacherDTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@RestController
@CrossOrigin
@RequestMapping("/teachers")
public class TeacherHttpController {
    public final HikariDataSource dataSource;
    public TeacherHttpController() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/application.properties"));

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(properties.getProperty("app.db.url"));
        hikariConfig.setUsername(properties.getProperty("app.db.username"));
        hikariConfig.setPassword(properties.getProperty("app.db.password"));
        hikariConfig.setDriverClassName(properties.getProperty("app.db.driverClassName"));
        hikariConfig.setMaximumPoolSize(20);
        dataSource = new HikariDataSource(hikariConfig);
        System.out.println(dataSource);
    }

    @PreDestroy
    public void destroy(){
        if(!dataSource.isClosed()){
            dataSource.close();
        }
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TeacherDTO> getAllTeachers(){
        LinkedList<TeacherDTO> teacherDTOS = new LinkedList<>();
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM teacher");
            ResultSet resultSet = stm.executeQuery();
            while (resultSet.next()){
                teacherDTOS.add(new TeacherDTO(resultSet.getInt("id"),resultSet.getString("name"),resultSet.getString("contact")));
            }
            return teacherDTOS;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}")
    public TeacherDTO getTeacherById(@PathVariable int id){
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM teacher WHERE id=?");
            stm.setInt(1,id);
            ResultSet resultSet = stm.executeQuery();
            if(resultSet.next()){
                return new TeacherDTO(resultSet.getInt("id"),resultSet.getString("name"),resultSet.getString("contact"));
            }else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"ID Not Found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "application/json",produces = "application/json")
    public TeacherDTO registerNewTeacher(@RequestBody @Validated TeacherDTO teacher) {
        try (Connection connection = dataSource.getConnection()){
            System.out.println(teacher.getName() + " " + teacher.getContact());
            PreparedStatement stm = connection.prepareStatement("INSERT INTO teacher (name, contact) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1,teacher.getName());
            stm.setString(2,teacher.getContact());
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            return new TeacherDTO(id,teacher.getName(),teacher.getContact());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{id}",consumes = "application/json")
    public void updateTeacher(@PathVariable int id,@RequestBody TeacherDTO teacher){
        //chech if exist
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM teacher WHERE id=?");
            stm.setInt(1,id);
            ResultSet resultSet = stm.executeQuery();
            if(!resultSet.next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Not Existing Teacher");
            }else {
                PreparedStatement stm1 = dataSource.getConnection().prepareStatement("UPDATE teacher SET name=?,contact=? WHERE id=?");
                stm1.setString(1,teacher.getName());
                stm1.setString(2,teacher.getContact());
                stm1.setInt(3,id);
                stm1.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteTeacher(@PathVariable int id){
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM teacher WHERE id=?");
            stm.setInt(1,id);
            ResultSet resultSet = stm.executeQuery();
            if(!resultSet.next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Not Existing Teacher");
            }else {
                PreparedStatement stm1 = connection.prepareStatement("DELETE FROM teacher WHERE id=?");
                stm1.setInt(1,id);
                stm1.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
