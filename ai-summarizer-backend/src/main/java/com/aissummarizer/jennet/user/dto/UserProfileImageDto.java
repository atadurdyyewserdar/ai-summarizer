package com.aissummarizer.jennet.user.dto;

import org.springframework.web.multipart.MultipartFile;

public record UserProfileImageDto (String userName, MultipartFile imageFile){
}
