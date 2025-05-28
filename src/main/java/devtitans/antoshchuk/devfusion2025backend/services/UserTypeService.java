package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.models.user.UserType;
import devtitans.antoshchuk.devfusion2025backend.repositories.UserTypeRepository;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.UserTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTypeService {
    private UserTypeRepository userTypeRepository;
    @Autowired
    public UserTypeService(UserTypeRepository userTypeRepository) {
        this.userTypeRepository = userTypeRepository;
    }

    public UserType getUserTypeByName(UserTypes type) {
        if(type == UserTypes.SEEKER){
            return findUserTypeById(2);
        }
        else if(type == UserTypes.COMPANY){
            return findUserTypeById(1);
        }
        throw new RuntimeException("NotFoundRole");
    }

    private UserType findUserTypeById(Integer id) {
        return userTypeRepository.findById(id).orElse(null);
    }
}
