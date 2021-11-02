package id.holigo.services.holigouserservice.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.domain.UserPersonal;
import id.holigo.services.holigouserservice.domain.UserPersonalPhotoProfil;
import id.holigo.services.holigouserservice.repositories.UserPersonalPhotoProfileRepository;
import id.holigo.services.holigouserservice.repositories.UserPersonalRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.services.storage.FileStorageService;
import id.holigo.services.holigouserservice.web.exceptions.NotFoundException;
import id.holigo.services.holigouserservice.web.mappers.UserPersonalMapper;
import id.holigo.services.holigouserservice.web.mappers.UserPersonalPhotoProfileMapper;
import id.holigo.services.holigouserservice.web.model.UserPersonalDto;
import id.holigo.services.holigouserservice.web.model.UserPersonalPhotoProfilDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserPersonalServiceImpl implements UserPersonalService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserPersonalMapper userPersonalMapper;

    @Autowired
    private final UserPersonalPhotoProfileMapper userPersonalPhotoProfileMapper;

    @Autowired
    private final UserPersonalRepository userPersonalRepository;

    @Autowired
    private final FileStorageService fileStorageService;

    @Autowired
    private final UserPersonalPhotoProfileRepository userPersonalPhotoProfileRepository;

    @Override
    public UserPersonalDto getUserPersonalByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User fetchUser = user.get();
        UserPersonal userPersonal = fetchUser.getUserPersonal();
        if (userPersonal == null) {
            return null;
        }
        return userPersonalMapper.userPersonalToUserPersonalDto(userPersonal);
    }

    @Transactional
    @Override
    public UserPersonalDto createUserPersonalByUserId(Long userId, UserPersonalDto userPersonalDto) throws Exception {
        UserPersonal userPersonalSaved;
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User fetchUser = user.get();
        UserPersonal userPersonal = userPersonalMapper.userPersonalDtoToUserPersonal(userPersonalDto);
        userPersonal.setUser(fetchUser);
        userPersonalSaved = userPersonalRepository.save(userPersonal);
        if (userPersonalSaved.getId() == null) {
            throw new Exception("Opps, something went wrong. We're working on it. Please try again leter.");
        }
        try {
            fetchUser.setUserPersonal(userPersonalSaved);
            userRepository.save(fetchUser);
        } catch (Exception e) {
            throw new Exception("Opps, something went wrong. We're working on it. Please try again leter.");
        }
        return userPersonalMapper.userPersonalToUserPersonalDto(userPersonalSaved);
    }

    @Override
    public UserPersonalDto updateUserPersonal(Long personalId, UserPersonalDto userPersonalDto) {
        Optional<UserPersonal> userPersonal = userPersonalRepository.findById(personalId);
        if (userPersonal.isEmpty()) {
            throw new NotFoundException("Personal data not found");
        }
        UserPersonal fetchUserPersonal = userPersonal.get();
        UserPersonal updateUserPersonal = userPersonalMapper.userPersonalDtoToUserPersonal(userPersonalDto);
        updateUserPersonal.setId(fetchUserPersonal.getId());
        UserPersonal userPersonalUpdated = userPersonalRepository.save(updateUserPersonal);
        return userPersonalMapper.userPersonalToUserPersonalDto(userPersonalUpdated);
    }

    @Transactional
    @Override
    public UserPersonalPhotoProfilDto savePhotoProfile(Long personalId, MultipartFile file) throws Exception {
        Optional<UserPersonal> fetchUserPersonal = userPersonalRepository.findById(personalId);
        if (fetchUserPersonal.isEmpty()) {
            throw new NotFoundException("Personal data not found");
        }
        UserPersonal userPersonal = fetchUserPersonal.get();
        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/users/" + Long.toString(userPersonal.getUser().getId()) + "/photoProfile/")
                .path(fileName).toUriString();

        UserPersonalPhotoProfil userPersonalPhotoProfil = new UserPersonalPhotoProfil();
        userPersonalPhotoProfil.setFileName(fileName);
        userPersonalPhotoProfil.setFileDownloadUri(fileDownloadUri);
        userPersonalPhotoProfil.setFileType(file.getContentType());
        userPersonalPhotoProfil.setSize(file.getSize());

        UserPersonalPhotoProfil savedUserPersonalPhotoProfile = userPersonalPhotoProfileRepository
                .save(userPersonalPhotoProfil);
        if (savedUserPersonalPhotoProfile.getId() == null) {
            throw new Exception("Failed save photo profile");
        }
        userPersonal.setPhotoProfil(savedUserPersonalPhotoProfile);
        userPersonalRepository.save(userPersonal);
        return userPersonalPhotoProfileMapper
                .userPersonalPhotoProfileToUserPersonalPhotoProfileDto(savedUserPersonalPhotoProfile);
    }

    @Override
    public Resource getPhotoProfile(String fileName) {
        return fileStorageService.loadFileAsResource(fileName);
    }

    @Transactional
    @Override
    public boolean deletePhotoProfile(Long photoProfileId) {
        boolean isDeleted = false;
        Optional<UserPersonalPhotoProfil> fetchUserPersonalPhotoProfile = userPersonalPhotoProfileRepository
                .findById(photoProfileId);
        if (fetchUserPersonalPhotoProfile.isEmpty()) {
            throw new NotFoundException("Photo ptofile not found");
        }
        UserPersonalPhotoProfil userPersonalPhotoProfil = fetchUserPersonalPhotoProfile.get();
        UserPersonal userPersonal = userPersonalPhotoProfil.getUserPersonal();
        userPersonal.setPhotoProfil(null);
        UserPersonal updatedUserPersonal = userPersonalRepository.save(userPersonal);
        if (updatedUserPersonal.getPhotoProfil() == null) {
            isDeleted = fileStorageService.deleteFile(userPersonalPhotoProfil.getFileName());
            if (isDeleted) {
                userPersonalPhotoProfileRepository.deleteById(userPersonalPhotoProfil.getId());
            }
        }
        return isDeleted;
    }

}
