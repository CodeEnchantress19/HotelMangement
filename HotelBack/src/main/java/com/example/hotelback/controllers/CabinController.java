package com.example.hotelback.controllers;

import com.example.hotelback.Entities.Cabin;
import com.example.hotelback.dto.CabinDto;
import com.example.hotelback.requests.CabinRequest;
import com.example.hotelback.responses.CabinResponseWithoutResrvation;
import com.example.hotelback.services.CabinService;
import com.example.hotelback.services.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cabins")
public class CabinController {

    @Autowired
    private CabinService cabinService;
    @Autowired
    private StorageService storageService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<CabinResponseWithoutResrvation> createCabine(
            @RequestPart("cabin") String cabin,
            @RequestParam("cabinImage")MultipartFile cabinImage
    ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        CabinRequest cabinRequest = objectMapper.readValue(cabin,CabinRequest.class);

        CabinDto cabinDto = new CabinDto();
        BeanUtils.copyProperties(cabinRequest,cabinDto);

        CabinDto addedCabin = cabinService.addCabine(cabinDto);

        CabinResponseWithoutResrvation cabinResponse = new CabinResponseWithoutResrvation();
        BeanUtils.copyProperties(addedCabin,cabinResponse);

        //image upload
        String uploadImage = storageService.uploadImageToFileSystem(cabinResponse.getIdcabin(),cabinImage);


        byte[] image = Files.readAllBytes(new File(uploadImage).toPath());
        cabinResponse.setImageFile(image);
        //cabinResponse.setImagePath(uploadImage);

        return ResponseEntity.ok(cabinResponse);
    }


    @GetMapping("/open/allCabins")
    public ResponseEntity<Page<CabinResponseWithoutResrvation>> getAllCabins(
            @PageableDefault(size = 12, page = 0) Pageable pageable
    ) {
        Page<CabinResponseWithoutResrvation> cabinResponses = cabinService.getAllCabins(pageable)
                .map(cabinDto -> {
                    CabinResponseWithoutResrvation cabinResponse = new CabinResponseWithoutResrvation();
                    BeanUtils.copyProperties(cabinDto, cabinResponse);

                    try {
                        String cabinImage = storageService.getImageByIdCabin(cabinDto.getIdcabin());
                        byte[] image = Files.readAllBytes(new File(cabinImage).toPath());
                        cabinResponse.setImageFile(image);
                    } catch (IOException e) {
                        // Handle the exception as needed
                        e.printStackTrace(); // Example: Print the stack trace
                    }

                    return cabinResponse;
                });

        return ResponseEntity.ok(cabinResponses);
    }
  
    @GetMapping("/open/getallCabins")
    public ResponseEntity<List<CabinResponseWithoutResrvation>> AllCabins() throws IOException {
        List<CabinResponseWithoutResrvation> cabineResponses = new ArrayList<>();
        List<CabinDto> cabins = cabinService.getAllCabines();

        for(CabinDto cabinDto:cabins){
            CabinResponseWithoutResrvation cabinResponse = new CabinResponseWithoutResrvation();
            BeanUtils.copyProperties(cabinDto,cabinResponse);

            //add image
            String cabinImage = storageService.getImageByIdCabin(cabinDto.getIdcabin());
            byte[] image =null ;
            if(cabinImage!=null)
                image = Files.readAllBytes(new File(cabinImage).toPath());
            cabinResponse.setImageFile(image);

            cabineResponses.add(cabinResponse);
        }

        return ResponseEntity.ok(cabineResponses);

    }

    @GetMapping("/open/cabin/{idCabin}")
    public ResponseEntity<CabinResponseWithoutResrvation> getCabinByIdCabin(@PathVariable Long idCabin) throws IOException {
        CabinDto cabinDto = cabinService.getCabinByIdCabin(idCabin);
        CabinResponseWithoutResrvation cabinResponse = new CabinResponseWithoutResrvation();

        BeanUtils.copyProperties(cabinDto,cabinResponse);
        //add image
        String cabinImage = storageService.getImageByIdCabin(cabinDto.getIdcabin());

        byte[] image = Files.readAllBytes(new File(cabinImage).toPath());
        cabinResponse.setImageFile(image);

        return ResponseEntity.ok(cabinResponse);

    }
    @DeleteMapping("/delete/{idCabin}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<Void> deleteCabin(@PathVariable Long idCabin) {
        cabinService.deleteCabin(idCabin);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/update/{idCabin}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<CabinResponseWithoutResrvation> updateCabin(
            @PathVariable Long idCabin,
            @RequestBody CabinRequest cabinRequest
    ) throws IOException {
        CabinDto cabinDto = new CabinDto();
        BeanUtils.copyProperties(cabinRequest, cabinDto);
        cabinDto.setIdcabin(idCabin);

        Cabin updatedCabin = cabinService.updateCabin(cabinDto);

        CabinResponseWithoutResrvation cabinResponse = new CabinResponseWithoutResrvation();
        BeanUtils.copyProperties(updatedCabin, cabinResponse);

        try {
            String cabinImage = storageService.getImageByIdCabin(updatedCabin.getIdcabin());
            byte[] image = Files.readAllBytes(new File(cabinImage).toPath());
            cabinResponse.setImageFile(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(cabinResponse);
    }
}
