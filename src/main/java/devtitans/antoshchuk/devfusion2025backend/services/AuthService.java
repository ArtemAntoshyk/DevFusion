package devtitans.antoshchuk.devfusion2025backend.services;

import devtitans.antoshchuk.devfusion2025backend.dto.request.UserAccountDTO;
import devtitans.antoshchuk.devfusion2025backend.dto.request.UserRegisterRequestDTO;
import devtitans.antoshchuk.devfusion2025backend.exceptions.UserAlreadyExistsException;
import devtitans.antoshchuk.devfusion2025backend.exceptions.ValidationException;
import devtitans.antoshchuk.devfusion2025backend.models.user.Company;
import devtitans.antoshchuk.devfusion2025backend.models.user.Seeker;
import devtitans.antoshchuk.devfusion2025backend.models.user.UserAccount;
import devtitans.antoshchuk.devfusion2025backend.repositiories.CompanyRepository;
import devtitans.antoshchuk.devfusion2025backend.repositiories.SeekerRepository;
import devtitans.antoshchuk.devfusion2025backend.repositiories.UserAccountRepository;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.UserMapper;
import devtitans.antoshchuk.devfusion2025backend.util.mappers.UserTypes;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SeekerRepository seekerRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private void validateUserAccount(UserAccountDTO userAccount) {
        if (userAccount == null) {
            throw new ValidationException("Дані користувача не можуть бути порожніми");
        }
        if (userAccount.getEmail() == null || userAccount.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email не може бути порожнім");
        }
        if (userAccount.getPassword() == null || userAccount.getPassword().trim().isEmpty()) {
            throw new ValidationException("Пароль не може бути порожнім");
        }
        if (userAccount.getPassword().length() < 6) {
            throw new ValidationException("Пароль повинен містити щонайменше 6 символів");
        }
        if (!userAccount.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Невірний формат email");
        }
    }

    private void validateSeeker(UserRegisterRequestDTO userRegisterRequestDTO) {
        if (userRegisterRequestDTO.getSeeker() == null) {
            throw new ValidationException("Дані шукача роботи не можуть бути порожніми");
        }
        if (userRegisterRequestDTO.getSeeker().getFirstName() == null || 
            userRegisterRequestDTO.getSeeker().getFirstName().trim().isEmpty()) {
            throw new ValidationException("Ім'я не може бути порожнім");
        }
        if (userRegisterRequestDTO.getSeeker().getLastName() == null || 
            userRegisterRequestDTO.getSeeker().getLastName().trim().isEmpty()) {
            throw new ValidationException("Прізвище не може бути порожнім");
        }
    }

    private void validateCompany(UserRegisterRequestDTO userRegisterRequestDTO) {
        if (userRegisterRequestDTO.getCompany() == null) {
            throw new ValidationException("Дані компанії не можуть бути порожніми");
        }
        if (userRegisterRequestDTO.getCompany().getName() == null || 
            userRegisterRequestDTO.getCompany().getName().trim().isEmpty()) {
            throw new ValidationException("Назва компанії не може бути порожньою");
        }
    }

    @Transactional
    UserAccount registerUser(UserAccountDTO userAccount, UserTypes userType) {
        validateUserAccount(userAccount);
        
        // Проверка на существующий email
        if (userAccountRepository.findByEmail(userAccount.getEmail()) != null) {
            throw new UserAlreadyExistsException("Користувач з таким email вже існує");
        }

        UserAccount registeredAccount = modelMapper.map(userAccount, UserAccount.class);
        registeredAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));
        registeredAccount.setUserType(userTypeService.getUserTypeByName(userType));
        return userAccountRepository.save(registeredAccount);
    }

    @Transactional
    public UserAccount registerSeeker(UserRegisterRequestDTO userRegisterRequestDTO) {
        validateSeeker(userRegisterRequestDTO);
        
        UserAccount userAccount = registerUser(userRegisterRequestDTO.getUser(), UserTypes.SEEKER);
        Seeker seeker = modelMapper.map(userRegisterRequestDTO.getSeeker(), Seeker.class);
        
        // Устанавливаем двустороннюю связь
        seeker.setUserAccount(userAccount);
        userAccount.setSeeker(seeker);
        
        // Сохраняем seeker
        seekerRepository.save(seeker);
        
        // Обновляем и возвращаем обновленный userAccount
        return userAccountRepository.save(userAccount);
    }

    @Transactional
    public UserAccount registerCompany(UserRegisterRequestDTO userRegisterRequestDTO) {
        validateCompany(userRegisterRequestDTO);
        
        UserAccount userAccount = registerUser(userRegisterRequestDTO.getUser(), UserTypes.COMPANY);
        Company company = modelMapper.map(userRegisterRequestDTO.getCompany(), Company.class);
        company.setUser(userAccount);
        companyRepository.save(company);
        return userAccount;
    }
}