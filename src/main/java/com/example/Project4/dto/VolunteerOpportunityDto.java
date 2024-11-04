package com.example.Project4.dto;

import com.example.Project4.model.Organization;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class VolunteerOpportunityDto {
    private String title;
    private String description;
    private List<MultipartFile> files;
    private Organization organization;

}
