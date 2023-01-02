package id.holigo.services.holigouserservice.services;

import java.util.Objects;
import java.util.Optional;

import id.holigo.services.holigouserservice.services.influencer.InfluencerService;
import id.holigo.services.holigouserservice.web.model.ImageKitDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import id.holigo.services.holigouserservice.domain.User;
import id.holigo.services.holigouserservice.domain.UserPersonal;
import id.holigo.services.holigouserservice.domain.UserPersonalPhotoProfile;
import id.holigo.services.holigouserservice.repositories.UserPersonalPhotoProfileRepository;
import id.holigo.services.holigouserservice.repositories.UserPersonalRepository;
import id.holigo.services.holigouserservice.repositories.UserRepository;
import id.holigo.services.holigouserservice.services.storage.FileStorageService;
import id.holigo.services.holigouserservice.web.exceptions.NotFoundException;
import id.holigo.services.holigouserservice.web.mappers.UserPersonalMapper;
import id.holigo.services.holigouserservice.web.mappers.UserPersonalPhotoProfileMapper;
import id.holigo.services.holigouserservice.web.model.UserPersonalDto;
import id.holigo.services.holigouserservice.web.model.UserPersonalPhotoProfileDto;
import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserPersonalServiceImpl implements UserPersonalService {

    private final UserRepository userRepository;

    private final UserPersonalMapper userPersonalMapper;

    private final UserPersonalPhotoProfileMapper userPersonalPhotoProfileMapper;

    private final UserPersonalRepository userPersonalRepository;

    private final FileStorageService fileStorageService;

    private final UserPersonalPhotoProfileRepository userPersonalPhotoProfileRepository;

    private final InfluencerService influencerService;

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

    @Override
    public UserPersonal createUserPersona(UserPersonal userPersonal) throws Exception {
        UserPersonal savedUserPersonal = userPersonalRepository.save(userPersonal);
        if (savedUserPersonal.getId() == null) {
            throw new Exception("Failed save personal data");
        }
        return savedUserPersonal;
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

    @Transactional
    @Override
    public UserPersonalDto updateUserPersonal(Long personalId, UserPersonalDto userPersonalDto) {
        Optional<UserPersonal> fetchUserPersonal = userPersonalRepository.findById(personalId);
        if (fetchUserPersonal.isEmpty()) {
            throw new NotFoundException("Personal data not found");
        }
        UserPersonal userPersonal = fetchUserPersonal.get();
        User user = userPersonal.getUser();
        String oldEmail = user.getEmail();
        userPersonal.setGender(userPersonalDto.getGender());
        userPersonal.setBirthDate(userPersonalDto.getBirthDate());
        userPersonal.setCity(userPersonalDto.getCity());
        user.setEmail(userPersonalDto.getEmail());
        user.setName(userPersonalDto.getName());
        if (!Objects.equals(oldEmail, userPersonal.getEmail())) {
            user.setEmailStatus(UserServiceImpl.INIT_EMAIL_STATUS);
        }
        userRepository.save(user);
        return userPersonalMapper.userPersonalToUserPersonalDto(userPersonalRepository.save(userPersonal));
    }

    @Transactional
    @Override
    public UserPersonalPhotoProfileDto savePhotoProfile(Long personalId, MultipartFile file) throws Exception {
        Optional<UserPersonal> fetchUserPersonal = userPersonalRepository.findById(personalId);
        if (fetchUserPersonal.isEmpty()) {
            throw new NotFoundException("Personal data not found");
        }
        UserPersonalPhotoProfile tempPhotoProfile = null;
        UserPersonal userPersonal = fetchUserPersonal.get();
        if (userPersonal.getPhotoProfile() != null) {
            tempPhotoProfile = userPersonal.getPhotoProfile();

        }
        ImageKitDto imageKitDto = fileStorageService.storeFile(file, personalId);
        UserPersonalPhotoProfile userPersonalPhotoProfile = new UserPersonalPhotoProfile();
        userPersonalPhotoProfile.setFileName(imageKitDto.getFileName());
        userPersonalPhotoProfile.setFileDownloadUri(imageKitDto.getUrl());
        userPersonalPhotoProfile.setFileType(file.getContentType());
        userPersonalPhotoProfile.setFileId(imageKitDto.getFileId());
        userPersonalPhotoProfile.setSize(file.getSize());

        UserPersonalPhotoProfile savedUserPersonalPhotoProfile = userPersonalPhotoProfileRepository
                .save(userPersonalPhotoProfile);
        if (savedUserPersonalPhotoProfile.getId() == null) {
            throw new Exception("Failed save photo profile");
        }

        userPersonal.setPhotoProfile(savedUserPersonalPhotoProfile);
        userPersonalRepository.save(userPersonal);

        //delete previous image
        if (savedUserPersonalPhotoProfile.getId() != null && tempPhotoProfile != null) {
            fileStorageService.deleteFile(tempPhotoProfile.getFileId());
            userPersonalPhotoProfileRepository.delete(tempPhotoProfile);
        }
        UserPersonalPhotoProfileDto userPersonalPhotoProfileDto = userPersonalPhotoProfileMapper
                .userPersonalPhotoProfileToUserPersonalPhotoProfileDto(savedUserPersonalPhotoProfile);
        if (userPersonal.getUser().getIsOfficialAccount()) {
            try {
                influencerService.updateProfilePicture(userPersonal.getUser().getId(), userPersonalPhotoProfileDto);
            } catch (Exception e) {
                log.error("Error : " + e.getMessage());
            }
        }
        return userPersonalPhotoProfileDto;
    }

//    @Override
//    public Resource getPhotoProfile(String fileName) {
//        return fileStorageService.loadFileAsResource(fileName);
//    }

    @Transactional
    @Override
    public boolean deletePhotoProfile(Long photoProfileId) {
        boolean isDeleted = false;
        Optional<UserPersonalPhotoProfile> fetchUserPersonalPhotoProfile = userPersonalPhotoProfileRepository
                .findById(photoProfileId);
        if (fetchUserPersonalPhotoProfile.isEmpty()) {
            throw new NotFoundException("Photo profile not found");
        }
        UserPersonalPhotoProfile userPersonalPhotoProfile = fetchUserPersonalPhotoProfile.get();
        UserPersonal userPersonal = userPersonalPhotoProfile.getUserPersonal();
        userPersonal.setPhotoProfile(null);
        UserPersonal updatedUserPersonal = userPersonalRepository.save(userPersonal);
        if (updatedUserPersonal.getPhotoProfile() == null) {
            isDeleted = fileStorageService.deleteFile(userPersonalPhotoProfile.getFileId());
            if (isDeleted) {
                userPersonalPhotoProfileRepository.deleteById(userPersonalPhotoProfile.getId());
            }
        }
        return isDeleted;
    }

}
