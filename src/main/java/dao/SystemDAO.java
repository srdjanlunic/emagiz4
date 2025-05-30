package dao;

import model.ITSystem;
import java.util.List;

public class SystemDAO {

    // create new system
    public ITSystem create(ITSystem system) {
        // TODO: implement database insert
        return null;
    }

    // get system by id
    public ITSystem findById(Long id) {
        // TODO: implement database query
        return null;
    }

    // get all systems
    public List<ITSystem> findAll() {
        // TODO: implement database query
        return null;
    }

    // update system
    public ITSystem update(ITSystem system) {
        // TODO: implement database update
        return null;
    }

    // delete system
    public boolean delete(Long id) {
        // TODO: implement database delete
        return false;
    }

    // find systems by department
    public List<ITSystem> findByDepartment(Long departmentId) {
        // TODO: implement database query
        return null;
    }

    // find systems by owner
    public List<ITSystem> findByOwner(Long ownerId) {
        // TODO: implement database query
        return null;
    }
}
