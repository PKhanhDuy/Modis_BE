package com.example.modis.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.modis.admin.service.ManagementAccountService;
import com.example.modis.user.dto.UpdateUserRoleRequest;
import com.example.modis.user.model.User;

@RestController
@RequestMapping("/admin/accounts")
public class ManagementAccountController {
    @Autowired
    private ManagementAccountService accountService;

    @GetMapping("/getAccounts")
    public ResponseEntity<List<User>> getAccounts() {
        System.out.println(accountService.getAllUsers());
        return ResponseEntity.ok(accountService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id){
        User updatedUser = accountService.deleteUser(id );
        return ResponseEntity.ok(
                Map.of(
                        "message", "Đã xóa tài khoản",
                        "data", Map.of(
                                "message", updatedUser.getId()
                        )
                )
        );
    }

    @PutMapping("/{id}/update-role")
    public ResponseEntity<?> updateRole(@PathVariable String id,
                                        @RequestBody UpdateUserRoleRequest request) {
        User updatedUser = accountService.updateRole(id, request.getRole());
        return ResponseEntity.ok(
                Map.of(
                        "message", "Đã cập nhật quyền của tài khoản",
                        "data", Map.of(
                                "message", updatedUser.getId(),
                                "role", updatedUser.getRole()
                        )
                )
        );
    }
    

}
