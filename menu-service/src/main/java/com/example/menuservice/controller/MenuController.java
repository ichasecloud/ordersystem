package com.example.menuservice.controller;

import com.example.menuservice.entity.MenuItem;
import com.example.menuservice.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.List;
import java.nio.file.*;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/menu")
@Tag(name = "Menu Service", description = "API for menu operations")
public class MenuController {

    private final MenuService menuService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // Get all items on menu
    @GetMapping("/items")
    @Operation(summary = "Get all items on menu")
    public List<MenuItem> getAllMenuItems() {
        return menuService.getAllMenuItems();
    }

    // Get item by ID
    @GetMapping("/items/{id}")
    @Operation(summary = "Get item by id")
    public Optional<MenuItem> getMenuItemById(@PathVariable Long id) {
        return menuService.getMenuItemById(id);
    }

    /**
     * Add a new item to the menu
     * @param menuItem item to be added
     * @param file image file to be updated
     * @return ResponseEntity
     */
    @PostMapping(value = "/items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add a new item to menu with picture")
    public ResponseEntity<MenuItem> addMenuItem(
            @RequestPart("menuItem") MenuItem menuItem,
            @RequestPart("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return new ResponseEntity("Image is required", HttpStatus.BAD_REQUEST);
        }

        try {
            String imageUrl = saveFile(file);
            menuItem.setImageUrl(imageUrl);
            MenuItem savedItem = menuService.addMenuItem(menuItem);
            return new ResponseEntity<>(savedItem, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update the item in the menu
     * @param id the id of item to be updated
     * @param menuItem application/json format
     * @param file image file to be uploaded
     * @return ResponseEntity
     */
    @PutMapping(value = "/items/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update an item to menu with picture")
    public ResponseEntity<MenuItem> updateMenuItemWithImage(
            @PathVariable Long id,
            @RequestPart("menuItem") MenuItem menuItem,
            @RequestPart("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return new ResponseEntity("Image is required", HttpStatus.BAD_REQUEST);
        }

        try {
            String imageUrl = saveFile(file);
            menuItem.setImageUrl(imageUrl);
            menuItem.setId(id);
            MenuItem updatedItem = menuService.updateMenuItem(menuItem);
            return new ResponseEntity<>(updatedItem, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete item in menu
    @DeleteMapping("/items/{id}")
    @Operation(summary = "Delete an item")
    public void deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
    }

    /**
     * Store the uploaded file and return the image url
     * @param file image file
     * @return img url
     * @throws IOException throws IO exception
     */
    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Clean the original name and get the extension name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex != -1) {
            fileExtension = originalFileName.substring(dotIndex);
        }

        // Generate the specific file name
        String fileName = System.currentTimeMillis() + fileExtension;
        Path targetPath = uploadPath.resolve(fileName);

        // Store the file in the location
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "http://localhost:8080/menu/images/" + fileName;
    }
}
