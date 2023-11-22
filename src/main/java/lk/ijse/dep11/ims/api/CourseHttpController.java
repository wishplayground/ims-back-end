package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.dto.Course;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/courses")
@CrossOrigin
public class CourseHttpController {
    private HikariDataSource pool;
    public CourseHttpController(){
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("7575");
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setMaximumPoolSize(10);

        pool = new HikariDataSource(hikariConfig);
        System.out.println("connection pool name" +pool);
    }

    @GetMapping(produces = "application/json")
    public List<Course> getAllCourses(){
        List<Course> courseList = new LinkedList<>();
        try (Connection connection = pool.getConnection()) {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM course");
            while(rst.next()){
                int id  = rst.getInt("id");
                String name = rst.getString("name");
                int durationInMonths = rst.getInt("duration_in_months");
                Course course = new Course(id, name, durationInMonths);
                courseList.add(course);
            }
            return courseList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Course getCourse(@PathVariable int id){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            stm.setInt(1,id);
            ResultSet rst = stm.executeQuery();
            if(!rst.next())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid ID");
            String name = rst.getString("name");
            int durationInMonths = rst.getInt("duration_in_months");
            return new Course(id,name,durationInMonths );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable int id){
        System.out.println("Start deleting");
        try (Connection connection = pool.getConnection()) {
            /* Business Validation before deletion*/
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            stm.setInt(1,id);
            ResultSet rst = stm.executeQuery();
            if(!rst.next())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid ID");

            /*Deletion from database*/
            PreparedStatement stmDel = connection.prepareStatement("DELETE FROM course WHERE id=?");
            stmDel.setInt(1,id);
            int i = stmDel.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Complete deleting");
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Course createCourse(@RequestBody @Validated(Course.Create.class) Course course){
        System.out.println(course);
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO course (name, duration_in_months) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1,course.getName());
            stm.setInt(2,course.getDurationInMonths());
            int i = stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            return new Course(id, course.getName(), course.getDurationInMonths());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @PatchMapping(value = "/{id}", consumes = "application/json")
    public void updateCourse(@PathVariable int id,
            @RequestBody @Validated Course course){
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM course WHERE id=?");
            stm.setInt(1,id);
            ResultSet rst = stm.executeQuery();
            if(!rst.next())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Course ID not found");

            PreparedStatement updateStm = connection.prepareStatement("UPDATE course SET name=?, duration_in_months=? WHERE id=?");
            updateStm.setString(1,course.getName());
            updateStm.setInt(2,course.getDurationInMonths());
            updateStm.setInt(3,id);
            updateStm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
